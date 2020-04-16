[TOC]
## 服务治理nacos的安装使用
### 安装nacos
- 下载zip格式安装包解压缩
```
下载地址：https://github.com/alibaba/nacos/releases
```
- 启动nacos： startup.cmd -m standalone
- 访问nacos：http://localhost:8848/nacos

### 使用nacos的服务注册功能
- 1 添加依赖
```
        <!--nacos服务注册发现-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
```
- 2 添加配置
```
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
```
- 3 主类添加注解@EnableDiscoveryClient
- 验证：在nacos控制台的服务列表查看是否注册成功
