package cn.hzp.controller;

import cn.hzp.domain.Order;
import cn.hzp.domain.Product;
import cn.hzp.service.OrderService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderService orderService;

    @RequestMapping("/order/product/{pid}")
    public Order createOrder(@PathVariable Integer pid) {
        // 查库存
        log.info("进行下单操作，开始远程调用查库存");
        Product product = restTemplate.getForObject(
                "http://localhost:8081/product/" + pid,
                Product.class
        );
        log.info("库存查询结果为:{}", JSON.toJSONString(product));

        // 下单
        log.info("下单操作，开始进行下单");
        Order order = Order.builder()
                .pid(pid).pname(product.getPname()).pprice(product.getPprice())
                .uid(1).uname("测试用户").number(1).build();
        orderService.save(order);
        log.info("下单成功，订单信息为{}", JSON.toJSONString(order));
        return order;
    }
}
