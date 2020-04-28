package cn.hzp.service;

import cn.hzp.domain.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-product")
public interface ProductService {

    /**
     * feignClient的值和requestMapping的值组合起来就是远程访问的请求路径
     */
    @GetMapping("/product/{pid}")
    Product findByPid(@PathVariable("pid") Integer pid);
}
