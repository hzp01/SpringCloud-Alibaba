[TOC]
## 1 服务网关gateway简介
### 1.1 为什么需要网关
没有网关，客户端访问不同服务，1维护不同地址繁琐；2跨域；3独立认证复杂

### 1.2 网关可以做什么
所有服务的统一入口，可以处理公共业务，支持路由转发、监控、认证鉴权等

### 1.3 gateway诞生
zuul组件不再开源，spring公司基于spring5.0、spring boot2.0新开发的网关

## 2 gateway原理和功能简述
### 2.1 gateway执行流程简述
参考网址`https://www.jianshu.com/p/c40a757fad01`
- 1 请求被HttpWebHandlerAdapter组成网关上下文
- 2 经过DispatcherHandler分发给RoutePredicateHandlerMapping进行断言
- 3 断言成功，由FilteringWebHandler创建过滤链调用(preFilter-微服务-postFilter)

### 2.2 gateway的功能
核心功能路由转发，支持的功能包括：
- 1 支持断言（满足条件时转发），存在内置断言，支持自定义
- 2 支持过滤器（服务访问前和访问后），局部过滤器和全局过滤器，存在内置过滤器，支持自定义
- 3 支持集成sentinel限流

