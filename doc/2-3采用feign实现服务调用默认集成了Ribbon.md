[TOC]
## feign的使用方法
feign是一个声明式的伪http客户端，调用远程像调用本地一样简单
默认集成了ribbon负载均衡，nacos兼容feign，nacos下使用feign就实现了负载均衡
```
1 pom文件添加依赖
2 启动类添加注解声明
3 添加接口方法
4 本地调用
```
### 1 pom文件添加依赖
```
        <!--添加feign的依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
```

### 2 启动类添加注解@EnableFeignClients
### 3 添加新的接口，新增调用方法
```
@FeignClient(value = "service-product")
public interface ProductService {
    @RequestMapping("/product/{pid}")
    // feignClient的值和requestMapping的值组合起来就是远程访问的请求路径
    Product findByPid(@PathVariable("pid")  Integer pid);
}
```
注意：方法参数如果为@PathVariable Integer pid，会报错：PathVariable annotation was empty on param 0

### 4 本地调用
```
    @Autowired
    private ProductService productService;
    
    ....{ Product product = productService.findByPid(pid); ....}
```