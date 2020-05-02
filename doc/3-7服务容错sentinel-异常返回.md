[TOC]
## 1 异常返回
包括自定义全局异常和单个资源异常处理

##　2　自定义异常页面演示
需要实现接口UrlBlockHandler，在config方法里增加类MyUrlBlockHandler
```
@Component
public class MyUrlBlockHandler implements UrlBlockHandler {
    @Override
    public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ResponseDate responseDate = null;
        if (e instanceof FlowException) {
            responseDate = new ResponseDate(-1, "接口被限流了");
        } else if (e instanceof DegradeException) {
            responseDate = new ResponseDate(-2, "接口被降级了");
        } else if (e instanceof ParamFlowException) {
            responseDate = new ResponseDate(-2, "接口被热点限流了");
        } else if (e instanceof AuthorityException) {
            responseDate = new ResponseDate(-2, "接口被授权规则限制访问了");
        } else if (e instanceof SystemBlockException) {
            responseDate = new ResponseDate(-2, "接口被系统规则限制了了");
        }
        httpServletResponse.getWriter().write(JSON.toJSONString(responseDate));
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ResponseDate {
    private Integer code;
    private String message;
}
```

## 3 注解sentinelResource里的具体异常返回
### 3.1 概念说明
- 属性value值表示资源名
- 属性blockHandler表示产生sentinel异常需要处理的方法
- 属性fallback表示产生任何异常需要处理的方法
- 属性blockHandlerClass表示blockHandler指向的方法所在的类，用该属性方法必须为静态方法
注意：这个时候不要用自定义异常页面MyUrlBlockHandler会发生冲突.

### 3.2 简单演示
#### 3.2.1 在order服务的controller中增加测试方法
```
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
```
####　3.2.2 新增异常处理类MyBlockHandler
```
package cn.hzp.Exception;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyBlockHandler {
    // 此方法必须为静态方法
    public static String blockHandler(String name, BlockException b) {
        log.info("进入sentinelResource注解测试,进入blockHandler，参数name={},b={}", name, b.toString());
        return "blockHandler";
    }
}
```
#### 3.2.3 在order服务的controller中增加异常处理方法
注意参数为Throwable，这个方法可以像BlockException那样抽出来
```
    public String fallback(String name, Throwable e){
        log.info("进入sentinelResource注解测试,进入fallback，参数name={},b={}", name, e.toString());
        return "fallback";
    }
```

#### 3.2.4 测试验证
- 对资源resourceName设置流控规则1s的qps为1
- 访问`http://localhost:8091/sentinel/sentinelResource?name=test`
```
第一次可以正常访问
多次刷新会返回blockHandler
每个第三次都会返回fallback
```