[TOC]
## 1 热点规则
对参数限流，防止一个ip多次请求如下单等操作；参数对象可以为商品id、用户id等

### 1.1 代码示例
在上游order服务的controller增加测试方法
```
    /**
     * 服务容错组件sentinel的热点参数限流规则测试
     */
    @RequestMapping("/sentinel/hot")
    @SentinelResource("hot")
    public String hot(Integer productId, Integer userId, String otherParams) {
        log.info("防止一个ip多次下单或者恶意查询等操作，限制热点参数的qps值，如商品id：{}，用户id：｛｝", productId, userId);
        return "服务容错组件sentinel的热点参数限流规则测试";
    }
```

### 1.2 验证测试
控制台选中资源hot，对参数0进行热点参数限流，设置60s内qps为阈值3，
- 测试访问`http://localhost:8091/sentinel/hot?productId=1`，多次刷新返回500
- 测试访问`http://localhost:8091/sentinel/hot?userId=1`,多次刷新正常返回。