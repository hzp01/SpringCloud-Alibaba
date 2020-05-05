[TOC]
## 1 Sentinel哨兵使用简述
```
1 sentinel控制台dashboard需要单独启动（由spring boot开发）
2 sentinel依赖配置要在上游order服务引入
    pom文件需要引入sentinel依赖
    yaml文件需要加入对接控制台的相关配置
```
注意：sentinel默认为懒加载模式，需要访问服务后才会加载服务信息
## 2 简单演示
### 2.1 启动控制台
所需jar包在tools文件夹下，测试访问`localhost:8080`默认用户名和密码都是sentinel
```
// linux系统下的启动命令
java -Dserver.port=8080 \
-Dcsp.sentinel.dashboard.server=localhost:8080 \
-Dproject.name=sentinel-dashboard \
-jar sentinel-dashboard.jar
```
```
:window下的启动命令
java -Dserver.port=8080 ^
-Dcsp.sentinel.dashboard.server=localhost:8080 ^
-Dproject.name=sentinel-dashboard ^
-jar sentinel-dashboard.jar
```

|参数内容|简述|
|:----|:----:|
|-Dserver.port=8080|指定Spring Boot服务端启动端口，默认8080可省略
|Dcsp.sentinel.dashboard.server=localhost:8080|向Sentinel接入端指定控制台的地址
### 2.2 上游order服务pom文件加入sentinel依赖
```
        <!--添加sentinel依赖-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>
```
### 2.3 上游order服务yaml文件加入sentinel配置
```
#服务容错组件sentinel配置内容
spring:
  cloud:
    sentinel:
      transport:
        port: 9999 #跟控制台交流的端口（指定未使用过得端口）
        dashboard: localhost:8080 #控制台的服务地址
```
### 2.4 验证
浏览器访问order服务任意方法，可以在sentinel控制台看到order服务信息