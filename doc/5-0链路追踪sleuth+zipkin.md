[TOC]
## 1 链路追踪
- 1 链路追踪可以快速定位服务问题、故障影响的范围、服务依赖是否合理、链路性能。
- 2 阿里没有提供链路追踪技术，这里采用sleuth+zipkin方案实现

## 2 sleuth说明
### 2.1 基础概念
由链路标识traceId、服务节点标识spanid、时间注释Annotation构成
- traceId：一次请求，整条链路的唯一id标识
- spanId：每一个服务节点的唯一标识
- 时间注释：cs(client send),sr(server recieve),ss(server send),cr(client receive)

注：sr-cs请求网络延迟，ss-sr服务处理时间，cr-ss返回网络延迟，cr-cs请求总时间
### 2.2 使用说明
#### 2.2.1 只需在pom文件引入sleuth依赖即可,这里在父工程引入
```
    <dependencies>
        <!--链路追踪sleuth监控各节点服务信息-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-sleuth</artifactId>
        </dependency>
    </dependencies>
``` 
#### 2.2.2 测试验证，启动gateway服务和product服务
浏览器请求`http://localhost:7000/product-serv/product/1`,product服务控制台输出日志内容为
- `[service-product,004b2d759b2a44e9,49d1f0bba0c7d830,false]`,分别表示服务名，traceId，spanId，是否将监控信息输出到第三方

## 3 zipkin说明
### 3.1 zipkin基础概念
#### 3.1.1 四个核心组件
- Collector收集信息转为span格式，支持后续存储分析展示
- storage存储，支持内存ram默认、mysql、es等
- restfulApi提供外部访问
- webui：直观查询分析链路跟踪信息

#### 3.1.2 zipkin构成
zipkin由服务端和客户端构成，服务端由jar包启动即可。

### 3.2 简单使用
#### 3.2.1 父工程pom文件引入zipkin依赖
```
        <!-- 链路追踪zipkin依赖，收集存储显示sleuth信息-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>
        </dependency>
```
#### 3.2.2 所有服务yml文件引入zipkin和sleuth配置
```
spring:
  zipkin:
    base-url: http://localhost:9411
    # nacos不会将zipkin作为一个服务注册发现，只把它作为url对待
    discoveryClientEnabled: false
  sleuth:
    sampler:
      probability: 1.0 # zipkin从sleuth的信息采集率，0-1之间
```

### 3.3 zipkin数据持久化
#### 3.3.1 创建zipkin数据库，导入zipkin初始化表
```
CREATE TABLE IF NOT EXISTS zipkin_spans (
  `trace_id_high` BIGINT NOT NULL DEFAULT 0 COMMENT 'If non zero, this means the trace uses 128 bit traceIds instead of 64 bit',
  `trace_id` BIGINT NOT NULL,
  `id` BIGINT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `parent_id` BIGINT,
  `debug` BIT(1),
  `start_ts` BIGINT COMMENT 'Span.timestamp(): epoch micros used for endTs query and to implement TTL',
  `duration` BIGINT COMMENT 'Span.duration(): micros used for minDuration and maxDuration query'
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARACTER SET=utf8 COLLATE utf8_general_ci;
 
ALTER TABLE zipkin_spans ADD UNIQUE KEY(`trace_id_high`, `trace_id`, `id`) COMMENT 'ignore insert on duplicate';
ALTER TABLE zipkin_spans ADD INDEX(`trace_id_high`, `trace_id`, `id`) COMMENT 'for joining with zipkin_annotations';
ALTER TABLE zipkin_spans ADD INDEX(`trace_id_high`, `trace_id`) COMMENT 'for getTracesByIds';
ALTER TABLE zipkin_spans ADD INDEX(`name`) COMMENT 'for getTraces and getSpanNames';
ALTER TABLE zipkin_spans ADD INDEX(`start_ts`) COMMENT 'for getTraces ordering and range';
 
CREATE TABLE IF NOT EXISTS zipkin_annotations (
  `trace_id_high` BIGINT NOT NULL DEFAULT 0 COMMENT 'If non zero, this means the trace uses 128 bit traceIds instead of 64 bit',
  `trace_id` BIGINT NOT NULL COMMENT 'coincides with zipkin_spans.trace_id',
  `span_id` BIGINT NOT NULL COMMENT 'coincides with zipkin_spans.id',
  `a_key` VARCHAR(255) NOT NULL COMMENT 'BinaryAnnotation.key or Annotation.value if type == -1',
  `a_value` BLOB COMMENT 'BinaryAnnotation.value(), which must be smaller than 64KB',
  `a_type` INT NOT NULL COMMENT 'BinaryAnnotation.type() or -1 if Annotation',
  `a_timestamp` BIGINT COMMENT 'Used to implement TTL; Annotation.timestamp or zipkin_spans.timestamp',
  `endpoint_ipv4` INT COMMENT 'Null when Binary/Annotation.endpoint is null',
  `endpoint_ipv6` BINARY(16) COMMENT 'Null when Binary/Annotation.endpoint is null, or no IPv6 address',
  `endpoint_port` SMALLINT COMMENT 'Null when Binary/Annotation.endpoint is null',
  `endpoint_service_name` VARCHAR(255) COMMENT 'Null when Binary/Annotation.endpoint is null'
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARACTER SET=utf8 COLLATE utf8_general_ci;
 
ALTER TABLE zipkin_annotations ADD UNIQUE KEY(`trace_id_high`, `trace_id`, `span_id`, `a_key`, `a_timestamp`) COMMENT 'Ignore insert on duplicate';
ALTER TABLE zipkin_annotations ADD INDEX(`trace_id_high`, `trace_id`, `span_id`) COMMENT 'for joining with zipkin_spans';
ALTER TABLE zipkin_annotations ADD INDEX(`trace_id_high`, `trace_id`) COMMENT 'for getTraces/ByIds';
ALTER TABLE zipkin_annotations ADD INDEX(`endpoint_service_name`) COMMENT 'for getTraces and getServiceNames';
ALTER TABLE zipkin_annotations ADD INDEX(`a_type`) COMMENT 'for getTraces';
ALTER TABLE zipkin_annotations ADD INDEX(`a_key`) COMMENT 'for getTraces';
 
CREATE TABLE IF NOT EXISTS zipkin_dependencies (
  `day` DATE NOT NULL,
  `parent` VARCHAR(255) NOT NULL,
  `child` VARCHAR(255) NOT NULL,
  `call_count` BIGINT
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARACTER SET=utf8 COLLATE utf8_general_ci;
 
ALTER TABLE zipkin_dependencies ADD UNIQUE KEY(`day`, `parent`, `child`);
```
#### 3.3.2 服务端启动命令更新
```
java -jar zipkin-server-2.12.9-exec.jar --STORAGE_TYPE=mysql --MYSQL_HOST=127.0.0.1 --MYSQL_TCP_PORT=3306 --MYSQL_DB=zipkin --MYSQL_USER=root --MYSQL_PASS=root
```