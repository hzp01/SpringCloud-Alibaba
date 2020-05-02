package cn.hzp.controller;

import cn.hzp.Exception.MyBlockHandler;
import cn.hzp.domain.Order;
import cn.hzp.domain.Product;
import cn.hzp.service.OrderService;
import cn.hzp.service.ProductService;
import cn.hzp.service.impl.OrderFlowControlLinkServiceImpl;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@Slf4j
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderFlowControlLinkServiceImpl orderFlowControlLinkService;

    @RequestMapping("/order/product/{pid}")
    public Order createOrder(@PathVariable Integer pid) {
        /***** 查库存 *****/
        log.info("进行下单操作，开始远程调用查库存");
//        log.info("通过restTemplate远程调用对应服务");
//        Product product = restTemplate.getForObject(getUrl("ribbon", pid), Product.class);
        log.info("通过Feign远程调用对应服务");
        Product product = productService.findByPid(pid);
        log.info("库存查询结果为:{}", JSON.toJSONString(product));

        /*****  下单 *****/
        log.info("下单操作，开始进行下单");
        Order order = Order.builder()
                .pid(pid)
                .pname(product.getPname())
                .pprice(product.getPprice())
                .uid(1).uname("测试用户")
                .number(1)
                .build();
        orderService.save(order);
        log.info("下单成功，订单信息为{}", JSON.toJSONString(order));
//         模拟高并发场景，演示服务雪崩雏形
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return order;
    }

    /**
     * 获取远程调用的服务url
     */
    private String getUrl(String way, Integer pid) {
        String url = null;
        if ("default".equals(way)) {
            // 缺点url中ip和端口写死了，硬编码扩展性差
            url = "http://localhost:8081/product/" + pid;
            log.info("采用默认方式获取url:{}", url);
        } else if ("nacos".equals(way)) {
            // ip和端口通过nacos注册中心根据服务名称实现服务发现，同时自定义负载均衡实现随机访问产品服务1和产品服务2
            List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
            int random = new Random().nextInt(instances.size());
            ServiceInstance serviceInstance = instances.get(random);
            String host = serviceInstance.getHost();
            int port = serviceInstance.getPort();
            url = "http://" + host + ":" + port + "/product/" + pid;
            log.info("采用nacos方式自定义负载均衡，获取url:{}", url);
        } else if ("ribbon".equals(way)) {
            // 通过服务名称来调用
            String serviceName = "service-product";
            url = "http://" + serviceName + "/product/" + pid;
            log.info("在nacos基础上采用ribbon方式实现负载均衡获取url:{}", url);
        }
        return url;
    }

    /**
     * 服务容错：模拟服务雪崩的高并发测试方法
     */
    @RequestMapping("/sentinel/highConcurrency")
    public String highConcurrency() {
        return "测试高并发";
    }

    /**
     * 服务容错组件sentinel的流控测试：3种模式（直接、关联、链路）
     */
    @RequestMapping("/sentinel/flow1")
    public String flow1() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");
        log.info("start：{}", sdf.format(new Date()));
        orderFlowControlLinkService.flowControlLink();
        log.info("end：{}", sdf.format(new Date()));
        return "流控测试：3种模式（直接、关联、链路）";
    }

    /**
     * 服务容错组件sentinel的流控测试：配合测试3种模式（直接、关联、链路）中的关联和链路
     */
    @RequestMapping("/sentinel/flow2")
    public String flow2() {
        orderFlowControlLinkService.flowControlLink();
        return "流控测试：配合测试3种模式（直接、关联、链路）中的关联和链路";
    }

    /**
     * 服务容错组件sentinel的降级测试：配合测试3种策略（RT平均响应时间、异常比例、异常数）中的异常比例、异常数
     */
    int i = 0;

    @RequestMapping("/sentinel/degrade")
    public String degrade() {
        i++;
        // 此时异常比例为1/3=0.3333……
        if (i % 3 == 0) {
            throw new RuntimeException();
        }
        return "服务容错组件sentinel的降级测试：配合测试3种策略（RT平均响应时间、异常比例、异常数）中的异常比例、异常数";
    }

    /**
     * 服务容错组件sentinel的热点参数限流规则测试
     */
    @RequestMapping("/sentinel/hot")
    @SentinelResource("hot")
    public String hot(Integer productId, Integer userId, String otherParams) {
        log.info("防止一个ip多次下单或者恶意查询等操作，限制热点参数的qps值，如商品id：{}，用户id：｛｝", productId, userId);
        return "服务容错组件sentinel的热点参数限流规则测试";
    }

    /**
     * 服务容错组件sentinel-演示sentinelResource属性
     */
    int m = 0;

    @RequestMapping("/sentinel/sentinelResource")
    @SentinelResource(value = "resourceName",
            blockHandlerClass = MyBlockHandler.class,
            // 这里blockHandler方法必须为静态方法
            blockHandler = "blockHandler",
            fallback = "fallback")
    public String sentinelResource(String name) {
        log.info("进入sentinelResource注解测试,参数name={}", name);
        m++;
        if (m % 3 == 0) {
            throw new RuntimeException("fallback异常");
        }
        return "服务容错组件sentinel中注解sentinel属性的使用演示";
    }

    public String fallback(String name, Throwable e){
        log.info("进入sentinelResource注解测试,进入fallback，参数name={},b={}", name, e.toString());
        return "fallback";
    }
}
