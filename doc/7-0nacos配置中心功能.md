[TOC]
# 1 nacos配置中心
## 1.1 简单实现
- 1 搭建nacos环境，启动服务，参考2-1中的nacos安装
- 2 user服务的pom引入依赖
```
        <!--nacos配置中心功能-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>
```
- 3 在nacos控制台配置管理-配置列表新增配置
```
dataId=service-user-dev.yaml(服务名+环境标识+格式)
文件格式yaml
内容直接复制user服务的配置，修改端口为8077
```
- 4 在user服务中新增bootstrap.yml,注配置文件优先级bootstrap.properties>b.yml>a.p>a.y
```
spring:
  application:
    name: service-user
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        # 配置文件格式，这里不要写成yml
        file-extension: yaml
  profiles:
    # 环境标识
    active: dev
```
- 5 user服务注释掉a.yml中所有内容，启动服务，端口号为8077，验证成功

## 1.2 支持动态刷新，可以通过nacos服务端修改端口后重启服务验证配置
注：java代码读取配置的2种方法
- configurableApplicationContext.getEnvironment().getProperty("config.appName");
- 通过注解@Value("${config.appName}")

## 1.3 配置文件实现共享
### 1.3.1 相同的服务不同的环境（开发、测试、生产）
如user服务，service-user.yaml就是公共的配置服务，可以实现不同环境的共享(service-product-dev.yaml和service-product-test.yaml)
### 1.3.1 不同服务共享配置
- 1 控制台新增datasource.yml
- 2 配置文件新增属性
```
spring.cloud.nacos.config.shared-datids=sentinel.yml # 引入的其他配置
spring.cloud.nacos.config.refreshable-datids=sentinel.yml # 配置动态刷新
```

# 2 nacos的一些简单概念
namespace环境、group项目、dataId微服务
命名空间namespace：
	- 控制台新增namespace=test，在配置列表里通过标签切换public和test
	- 配置文件引入spring.cloud.nacos.config.namespace=testId #空间id

# 3 nacos集群部署，略
参考网址：https://www.cnblogs.com/FlyAway2013/p/11201250.html