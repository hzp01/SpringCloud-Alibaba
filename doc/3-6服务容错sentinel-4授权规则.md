[TOC]
## 1 授权规则简述
白名单用户，通过request来区分来源如不同服务、app或者pc。

## 2 案例模拟
### 2.1 在config下新增自定义来源处理规则，需要实现RequsetOriginParser
```
@Component
public class MyRequestOriginParser implements RequestOriginParser {
    /**
     * 区分来源：本质通过request域获取来源标识
     * 根据不同来源如app/pc，最后返回结果值交给sentinel流控匹配处理
     */
    @Override
    public String parseOrigin(HttpServletRequest request) {
        String servName = request.getParameter("servName");
        // 这个不是授权规则必须的判断
        if (StringUtils.isEmpty(servName)) {
            throw new RuntimeException("servName不能为空");
        }
        return servName;
    }
}
```
### 2.2 控制台
设置规则前
```
访问`http://localhost:8091/sentinel/highConcurrency?servName=pc`，正常返回
访问`http://localhost:8091/sentinel/highConcurrency?servName=app`，正常返回
```
对资源/sentinel/highConcurrency设置白名单的值为pc，再测试
```
访问`http://localhost:8091/sentinel/highConcurrency?servName=pc`，正常返回
访问`http://localhost:8091/sentinel/highConcurrency?servName=app`，提示限流
```
