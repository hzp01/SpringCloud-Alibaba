[TOC]
## 1 技术选型
```
maven：3.6.0
数据库：mysql5.7.23
持久层：SpringDataJpa
其他：
    SpringCloud-Alibaba 技术栈
        nacos【服务治理-服务注册发现剔除，配置管理】
        sentinel【服务容错-限流熔断】
            RestTemplate实现远程调用
            ribbon实现负载均衡
            hystrix实现熔断
            fegin整合了RestTemplate、ribbon、hystrix
            fegin可以整合sentinel实现容错
        Gateway网关
        sleuth链路信息收集
            zipkin提供界面支持，sleuth收集的链路信息通过zipkin展示
        分布式事务seata
    消息队列RocketMQ4.4.0
```
## 2 模块设计
```
springcloud-alibaba 父工程
shop-common         公共模块【实体类】
shop-user           用户微服务【端口：807x】
shop-product        商品微服务【端口：808x】
shop-order          订单微服务【端口：809x】
```