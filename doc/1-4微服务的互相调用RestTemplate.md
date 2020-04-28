[TOC]
## 记忆点简述
```
启动类中通过@Bean注解加入RestTemplate对象
在controller中通过@Autowired注解引入RestTemplate对象
方法中restTemplate.getForObject("远程调用ip地址:端口/方法", 返回的实体对象)
```

## 微服务的相互调用
- 需求说明：下单，订单服务调用商品服务查询库存
### 开发步骤
#### 1 数据库
- 增加商品初始化数据
```
insert into `shop_product` (`pid`, `pname`, `pprice`, `stock`) values('1','小米','1000','5000');
insert into `shop_product` (`pid`, `pname`, `pprice`, `stock`) values('2','华为','2000','5000');
insert into `shop_product` (`pid`, `pname`, `pprice`, `stock`) values('3','苹果','3000','5000');
```
#### 2 商品微服务
- 开发查询库存的操作

#### 3 订单微服务,采用RestTemplate进行远程调用
- 在启动类OrderApplication中引入RestTemplate实现远程调用
```  
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
```
- 在OrderController中通过@Autowired引入RestTemplate对象, 实现在方法中远程调用
```
        Product product = restTemplate.getForObject(
                "http://localhost:8081/product/" + pid,
                Product.class
        );
```

