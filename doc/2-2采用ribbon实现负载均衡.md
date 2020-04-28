[TOC]
## ribbon的使用方法
```
1 启动类中RestTemplate对象生成的@Bean注解下加入@LoadBalanced注解
2 方法中restTemplate.getForObject("远程调用服务名称/方法", 返回的实体对象)
3 配置文件中修改负载均衡策略，其他修改方式参见`https://www.jianshu.com/p/508f62fa0de5`
```
### 配置文件修改负载均衡策略的方法
```
service-product:
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```
说明：user-provider为要调用的服务名称，值为策略对象。
### ribbon的7种负载均衡策略

|策略对象| 简述|
|:---- |:----: |
|com.netflix.loadbalancer.RandomRule|随机选取服务|
|com.netflix.loadbalancer.RoundRobinRule|线性轮询，默认方式|
|com.netflix.loadbalancer.RetryRule|在轮询基础上添加重试机制，即在指定的重试时间内，反复使用线性轮询策略来选择可用实例|
|com.netflix.loadbalancer.WeightedResponseTimeRule|对线性轮询的扩展，响应速度越快的实例选择权重越大，越容易被选择|
|com.netflix.loadbalancer.BestAvailableRule|选择并发较小的实例|
|com.netflix.loadbalancer.AvailabilityFilteringRule|先过滤掉故障实例，再选择并发较小的实例|
|com.netflix.loadbalancer.ZoneAwareLoadBalancer|采用双重过滤，同时过滤不是同一区域的实例和故障实例，选择并发较小的实例|
```

