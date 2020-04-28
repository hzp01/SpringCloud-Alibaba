[TOC]
## 记忆点简述
```
1 nacos需要下载安装，手动启动
2 nacos通过配置文件中spring-name实现服务的注册
3 以下代码发现服务集群，通过服务实例ServiceInstance获取服务ip和端口
    List<ServiceInstance> instances = discoveryClient.getInstances("微服务名称");
4 这个步骤引入了一个自定义的负载均衡
```

## 服务治理nacos的安装使用
### 安装nacos
- 下载zip格式安装包解压缩
```
下载地址：https://github.com/alibaba/nacos/releases
```
- 默认单机模式启动nacos： startup.cmd -m standalone
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
- 4 类中引入DiscoveryClient对象，实现在方法中获取远程服务信息
```
        // 根据微服务名称获取集群信息
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        // 随机数实现负载均衡，随机访问集群中某一个服务
        int random = new Random().nextInt(instances.size());
        ServiceInstance serviceInstance = instances.get(random);
        String host = serviceInstance.getHost();
        int port = serviceInstance.getPort();   
        // 拼接远程调用的服务地址
        String url = "http://" + host + ":" + port + "/product/" + pid;
        // 通过restTemplate远程调用对应服务
        Product product = restTemplate.getForObject(url, Product.class);
```
