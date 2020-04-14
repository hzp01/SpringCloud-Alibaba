[TOC]
## 1 技术选型
```
maven：3.6.0
数据库：mysql5.7.23
持久层：SpringDataJpa
其他：SpringCloud-Alibaba 技术栈
```
## 2 模块设计
```
springcloud-alibaba 父工程
shop-common         公共模块【实体类】
shop-user           用户微服务【端口：807x】
shop-product        商品微服务【端口：808x】
shop-order          订单微服务【端口：809x】
```

