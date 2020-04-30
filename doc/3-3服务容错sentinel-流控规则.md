[TOC]
## 1 容错组件sentinel的流控规则
### 1 流控规则概念
```
资源名：保护的资源对象，可以是容器、类、方法、代码
阈值类型：QPS（每秒访问次数）、线程数；
流控模式：
    直接：接口达到限流时开启限流
    关联：关联达到限流时开启限流，适用应用让步场景：查询query为关联save让步
    链路：某个接口来的资源达到限流时开启限流
流控效果：
    快速失败: 直接失败
    warm-up: 预热，开始阈值为最大阈值的1/3缓慢增长到最大阈值，适用于突然增大的流量转换为缓步增长的场景
    排队等待：超过超时时间就会丢弃请求
```
### 2 流控规则中链路模式案例演示
#### 2.1 修改父工程的pom文件，2.1.1.RELEASE才支持链路功能
```
        <spring-cloud-alibaba.version>2.1.1.RELEASE</spring-cloud-alibaba.version>
```
#### 2.2 修改上游服务order工程的pom文件，关闭sentinel的CommonFilter实例化
```
# 实现流控规则中的链路功能，关闭sentinel的CommonFilter实例化
spring:
  cloud:
    sentinel:
      filter:
        enabled: false
```
#### 2.3 手动注入CommonFilter的实例，关闭sentinel的CommonFilter实例化
```
@Configuration
public class FilterContextConfig {
    @Bean
    public FilterRegistrationBean sentinelFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CommonFilter());
        registration.addUrlPatterns("/*");
        // 入口资源关闭聚合
        registration.addInitParameter(CommonFilter.WEB_CONTEXT_UNIFY, "false");
        registration.setName("sentinelFilter");
        registration.setOrder(1);
        return registration;
    }
}
```
#### 2.4 新增serviceImpl增加链路需要的资源
```
@Service
public class OrderFlowControlLinkServiceImpl {
    /**
     * 服务容错组件sentinel的流控测试：测试3种模式（直接、关联、链路）中的链路
     * 注解@SentinelResource，指定资源，value值为资源名称
     */
    @SentinelResource(value="resource")
    public String  flowControlLink(){
        return "服务容错组件sentinel的流控测试：测试3种模式（直接、关联、链路）中的链路";
    }
}
```
#### 2.5 在controller中添加两个入口的测试方法
```
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
     * 服务容错组件sentinel的流控测试：配合测试3种模式（直接、关联、链路）中的关联
     */
    @RequestMapping("/sentinel/flow2")
    public String flow2() {
        orderFlowControlLinkService.flowControlLink();
        return "流控测试：配合测试3种模式（直接、关联、链路）中的关联和链路";
    }
```
#### 2.6 控制台可以对资源resource增加链路控制，入口资源可以为flow1或flow2，进行测试验证。


