[TOC]
## 1 sentinel流控规则持久化到nacos示例
参考网址1：`http://www.itmuch.com/spring-cloud-alibaba/sentinel-rules-persistence-push-mode-using-nacos/`
参考网址2：`https://www.sonake.com/2019/12/16/Sentinel-Nacos%E5%AE%9E%E7%8E%B0%E8%A7%84%E5%88%99%E6%8C%81%E4%B9%85%E5%8C%96/`
### 1.1 order服务的pom文件加入依赖
```
        <!--添加sentinel持久化到nacos的依赖-->
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-datasource-nacos</artifactId>
        </dependency>
```
### 1.2 order服务的yml文件添加配置
```
spring:
  cloud:
    sentinel:
      datasource:
        # 名称随意
        flow:
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}-flow-rules
            groupId: SENTINEL_GROUP
            # 规则类型，取值见：
            # org.springframework.cloud.alibaba.sentinel.datasource.RuleType
            rule-type: flow
        degrade:
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}-degrade-rules
            groupId: SENTINEL_GROUP
            rule-type: degrade
        system:
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}-system-rules
            groupId: SENTINEL_GROUP
            rule-type: system
        authority:
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}-authority-rules
            groupId: SENTINEL_GROUP
            rule-type: authority
        param-flow:
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}-param-flow-rules
            groupId: SENTINEL_GROUP
            rule-type: param-flow
```
### 1.3 整理nacos
#### 1.3.1 数据库mysql导入nacos的配置文件
找到/nacos/conf下面的nacos-mysql.sql文件,在mysql导入运行，建立nacos对应的库表结构
#### 1.3.2 修改文件`nacos-server-1.1.4\nacos\conf\application.properties`
```
# db mysql
spring.datasource.platform=mysql
db.num=1
db.url.0=jdbc:mysql://localhost:3306/nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
db.user=root
db.password=root
```

### 1.4 改造sentinel-dashboard
#### 1.4.1 修改pom文件,删除scope属性
```
<!-- for Nacos rule publisher sample -->
  <dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-datasource-nacos</artifactId>
    <scope>test</scope>
  </dependency>
```
####　1.4.2 复制文件
复制目录`sentinel-dashboard/src/test/java/com/alibaba/csp/sentinel/dashboard/rule/nacos`
到目录`sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/nacos`

####　1.4.3 修改文件`com.alibaba.csp.sentinel.dashboard.controller.v2.FlowControllerV2`
将下面2个注解中的`flowRuleDefault`修改为`flowRuleNacos`
```
@Autowired
@Qualifier("flowRuleDefaultProvider")
private DynamicRuleProvider<List<FlowRuleEntity>> ruleProvider;
@Autowired
@Qualifier("flowRuleDefaultPublisher")
private DynamicRulePublisher<List<FlowRuleEntity>> rulePublisher;
```
#### 1.4.4 修改properties文件，增加nacos配置，修改默认启动端口
```
#配置nacos服务的地址
nacos.url=127.0.0.1:8848
#修改服务启动端口
server.port=8858
```

#### 1.4.5 修改文件`src\main\java\com\alibaba\csp\sentinel\dashboard\rule\nacos\NacosConfig.java`
将nacos的地址更新，修改前
```
    @Bean
    public ConfigService nacosConfigService() throws Exception {
        return ConfigFactory.createConfigService("localhost");
    }
```
修改后
```
    @Value("${nacos.url}")
    private String serverAddr;

    @Bean
    public ConfigService nacosConfigService() throws Exception {
        return ConfigFactory.createConfigService(serverAddr);
    }
```

#### 1.4.6 修改文件`sentinel-dashboard/src/main/webapp/resources/app/scripts/directives/sidebar/sidebar.html`
放开下面注释
```
<!--<li ui-sref-active="active">-->
  <!--<a ui-sref="dashboard.flow({app: entry.app})">-->
    <!--<i class="glyphicon glyphicon-filter"></i>&nbsp;&nbsp;流控规则 V1</a>-->
<!--</li>-->
```

#### 1.4.7 编译打包`mvn clean package -Dmaven.test.skip=true`
在target目录找到sentinel-dashboard.jar启动控制台进行测试

注：测试要用“流控规则V1”配置,tools文件夹下有编译好的jar包可以直接测试
