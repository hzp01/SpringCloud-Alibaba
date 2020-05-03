package cn.hzp.controller;

import cn.hzp.domain.Product;
import cn.hzp.service.ProductService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping("/product/{pid}")
    public Product selectProduct(@PathVariable Integer pid) {
        log.info("进行{}号商品的查询", pid);
        Product product = productService.findById(pid);
        log.info("查询成功，商品信息为：{}", JSON.toJSONString(product));
        return product;
    }

    /**
     * 测试gateway的api分组限流
     */
    @RequestMapping("/api1/demo1")
    public String api1Demo1() {
        return "/api1/demo1";
    }
    /**
     * 测试gateway的api分组限流
     */
    @RequestMapping("/api1/demo2")
    public String api1Demo2() {
        return "/api1/demo2";
    }
    /**
     * 测试gateway的api分组限流
     */
    @RequestMapping("/api2/demo1")
    public String api2Demo1() {
        return "/api2/demo1";
    }
    /**
     * 测试gateway的api分组限流
     */
    @RequestMapping("/api2/demo2")
    public String api2Demo2() {
        return "/api2/demo2";
    }

}
