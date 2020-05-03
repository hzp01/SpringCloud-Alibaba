[TOC]
## 1 feign整合sentinel实现容错简述
可以通过fallback实现简单容错，通过fallbackFactory实现容错异常处理
## 2 fallback案例演示
### 2.1 pom文件加入sentinel客户端
```
        <!--添加sentinel依赖-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency> 
```
### 2.2 yml文件增加feign对sentinel的支持
```
# 开启feign对sentinel的支持
feign:
  sentinel:
    enabled: true
```
### 2.3 远程调用service增加熔断属性fallback
```
/**
 * value 指远程调用的服务名称
 * fallback 指远程调用失败时熔断方法
 */
@FeignClient(value = "service-product", fallback = ProductServiceFallback.class)
public interface ProductService {

    /**
     * feignClient的值和requestMapping的值组合起来就是远程访问的请求路径
     */
    @GetMapping("/product/{pid}")
    Product findByPid(@PathVariable("pid") Integer pid);
}
```

### 2.4 增加fallback容错类
```
package cn.hzp.service.impl;

import cn.hzp.domain.Product;
import cn.hzp.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceFallback implements ProductService {
    @Override
    public Product findByPid(Integer pid) {
        Product p = Product.builder()
                .pid(-1)
                .pname("远程调用产品服务查询产品信息异常")
                .build();
        return p;
    }
}

```
### 2.5 在controller中下单方法，增加熔断后代码处理
```
        // 远程调用失败进入熔断
        if (product.getPid() == -1) {
            return Order.builder()
                    .oid(-100)
                    .pname("下单失败")
                    .build();
        }
```

### 2.6 测试验证，启动order服务请求`http://localhost:8091/order/product/1`测试

## 3 fallbackFactory支持容错异常的信息处理
只需要修改2.3属性改为fallbackFactory和2.4实现FallbackFactory接口
```
/**
 * value 指远程调用的服务名称
 * fallback 指远程调用失败时熔断方法
 * fallbackFactory 类似fallback，相对fallback提供了熔断异常信息处理
 * 注意fallbackFactory和fallback只能用一个
 */
@FeignClient(value = "service-product", fallbackFactory = ProductServiceFallbackFactory.class)
public interface ProductService {

    /**
     * feignClient的值和requestMapping的值组合起来就是远程访问的请求路径
     */
    @GetMapping("/product/{pid}")
    Product findByPid(@PathVariable("pid") Integer pid);
}
```
熔断类
```
package cn.hzp.service.impl;

import cn.hzp.domain.Product;
import cn.hzp.service.ProductService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductServiceFallbackFactory implements FallbackFactory<ProductService> {
    @Override
    public ProductService create(Throwable throwable) {
        return pid -> {
            log.error("异常信息为：{}", throwable.getMessage());
            Product p = Product.builder()
                    .pid(-1)
                    .pname("远程调用产品服务查询产品信息异常")
                    .build();
            return p;
        };
    }
}
```