package cn.hzp.service;

import cn.hzp.domain.Product;
import cn.hzp.service.impl.ProductServiceFallback;
import cn.hzp.service.impl.ProductServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
