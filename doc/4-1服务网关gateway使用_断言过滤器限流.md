[TOC]
## 1 服务网关gateway入手案例
### 1.1 在父工程下新增modulr网关服务api-gateway
### 1.2 pom文件引入gateway的jar包依赖
```
    <dependencies>
        <!--引入gateway网关依赖，注：不能同时引入依赖starter-web-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
    </dependencies>
```
### 1.3 yml文件增加基础配置
```
# 服务基础配置
server:
  port: 7000
spring:
  application:
    name: api-gateway
  # 网关配置
  cloud:
    gateway:
      routes: # 路由数组，支持多个路由，路由配置参照RouteDefinition
        - id: route-product # 路由标识
          uri: http://localhost:8081 # 要转发到的远程服务地址
          order: 0 # 排序，数字越小优先级越高
          predicates: #断言数组，满足条件实现转发，支持自定义断言
            - Path=/product-serv/**
          filters: #过滤数组，转发前服务处理，支持前置过滤和后置过滤，支持自定义过滤（局部过滤、全局过滤）
            - StripPrefix=1 # 转发前去掉一层访问路径
```
###　1.4 增加启动类ApiGateWayApplication
```
package cn.hzp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class);
    }
}
```
### 1.5 启动gateway网关服务和product商品服务
### 1.6 浏览器请求`http://localhost:7000/product-serv/product/1`测试验证


## 2 服务网关gateway集成nacos读取服务信息
在案例1的基础上，为了优化路由转发uri的值`http://localhost:8081`，集成nacos来读取服务信息
### 2.1 pom文件加入nacos依赖nacos-discovery
```
        <!--引入nacos依赖，实现网关注册到nacos同时从nacos读取其他服务信息-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
```
### 2.2 启动类上增加nacos服务发现注解@EnableDiscoveryClient
### 2.3 yml文件集成nacos，修改路由转发信息 
```
# 服务基础配置
server:
  port: 7000
spring:
  application:
    name: api-gateway
  # 网关配置
  cloud:
    # 增加nacos配置信息
    nacos:
      discovery:
        server-addr: localhost:8848
    gateway:
      # 使gateway可以读取nacos中服务信息
      discovery:
        locator:
          enabled: true
      # 路由数组，支持多个路由，路由配置参照RouteDefinition
      routes:
        - id: route-product # 路由标识
#          uri: http://localhost:8081 # 要转发到的远程服务地址
          uri: lb://service-product # lb支持负载均衡(loadBalance)
          order: 0 # 排序，数字越小优先级越高
          predicates: #断言数组，满足条件实现转发，支持自定义断言
            - Path=/product-serv/**
          filters: #过滤数组，转发前服务处理，支持前置过滤和后置过滤，支持自定义过滤（局部过滤、全局过滤）
            - StripPrefix=1 # 转发前去掉一层访问路径
```
### 2.4 浏览器请求`http://localhost:7000/product-serv/product/1`测试验证

## 3 服务网关断言使用
### 3.1 内置网关断言
这些断言类都继承AbstractRoutePredicateFactory，使用参考网址`https://www.cnblogs.com/wgslucky/p/11396579.html`

### 3.2 自定义断言
参照内置断言类`PathRoutePredicateFactory`
#### 3.2.1 pom文件引入lombok依赖
```
        <!--引入lombok，方便日志输出-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
```
#### 3.2.2 增加断言类
```
package cn.hzp.predicates;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * 断言类名称必须是"断言的配置属性"+RoutePredicateFactory
 * 必须继承AbstractRoutePredicateFactory<配置类>
 */
@Slf4j
@Component
public class AgeRoutePredicateFactory extends AbstractRoutePredicateFactory<AgeRoutePredicateFactory.Config> {
    /**
     * 构造函数
     */
    public AgeRoutePredicateFactory() {
        super(AgeRoutePredicateFactory.Config.class);
    }

    /**
     * 读取配置文件的参数信息，赋值到配置类的属性上
     */
    @Override
    public List<String> shortcutFieldOrder() {
        // 参数顺序需要和配置文件参数顺序一致
        return Arrays.asList("minAge", "maxAge");
    }

    /**
     * 断言逻辑
     */
    @Override
    public Predicate<ServerWebExchange> apply(AgeRoutePredicateFactory.Config config) {
        return serverWebExchange -> {
            String ageStr = serverWebExchange.getRequest().getQueryParams().getFirst("age");
            if (StringUtils.isNotEmpty(ageStr)) {
                int age = Integer.parseInt(ageStr);
                if (age >= config.getMinAge() && age <= config.getMaxAge()) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * 配置类，接收配置文件中的参数
     */
    @Data
    public static class Config {
        private int minAge;
        private int maxAge;
    }
}
```
#### 3.2.3 yml文件增加Age断言
```
# 服务基础配置
server:
  port: 7000
spring:
  application:
    name: api-gateway
  # 网关配置
  cloud:
    # 增加nacos配置信息
    nacos:
      discovery:
        server-addr: localhost:8848
    gateway:
      # 使gateway可以读取nacos中服务信息
      discovery:
        locator:
          enabled: true
      # 路由数组，支持多个路由，路由配置参照RouteDefinition
      routes:
        - id: route-product # 路由标识
#          uri: http://localhost:8081 # 要转发到的远程服务地址
          uri: lb://service-product # lb支持负载均衡(loadBalance)
          order: 0 # 排序，数字越小优先级越高
          predicates: #断言数组，满足条件实现转发，支持自定义断言
            - Path=/product-serv/**
            - Age=18,60 #自定义断言，实现服务只能被18-60岁之间的人访问
          filters: #过滤数组，转发前服务处理，支持前置过滤和后置过滤，支持自定义过滤（局部过滤、全局过滤）
            - StripPrefix=1 # 转发前去掉一层访问路径
```
#### 3.2.4 浏览器测试验证
- 请求`http://localhost:7000/product-serv/product/1?age=61`返回404
- 请求`http://localhost:7000/product-serv/product/1?age=60`返回正常

## 4 服务网关过滤器使用
- 前置pre过滤器可以验证身份、记录调试信息等。如StripPrefix
- 后置post过滤器可以修改响应标准header、收集统计信息等。如SetStatus
- 包括局部过滤器和全局过滤器

### 4.1 内置局部过滤器和内置全局过滤器
- 内置局部过滤器都继承AbstractGatewayFilterFactory，如SetStatusGatewayFilterFactory
- 内置全局过滤器都继承GlobalFilter, Ordered，如LoadBalancerClientFilter

### 4.2 自定义局部过滤器
方法和自定义断言一样，参照SetStatusGatewayFilterFactory，设计局部过滤器实现控制日志的输出
#### 4.2.1 新增局部过滤器类LogGatewayFilterFactory
```
package cn.hzp.filters;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 网关gateway-自定义过滤器类
 */
@Slf4j
@Component
public class LogGatewayFilterFactory extends AbstractGatewayFilterFactory<LogGatewayFilterFactory.Config> {
    /**
     * 构造函数
     */
    public LogGatewayFilterFactory() {
        super(LogGatewayFilterFactory.Config.class);
    }

    /**
     * 将配置文件的属性信息赋值到配置类中
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("consoleLog", "cacheLog");
    }

    /**
     * 自定义过滤逻辑
     */
    @Override
    public GatewayFilter apply(LogGatewayFilterFactory.Config config) {
        return (exchange, chain) -> {
            if (config.isConsoleLog()) {
                log.info("输出控制台日志");
            }
            if (config.isCacheLog()) {
                log.info("输出缓存日志");
            }
            return chain.filter(exchange);
        };
    }

    /**
     * 配置类
     */
    @Data
    @NoArgsConstructor
    public static class Config {
        private boolean consoleLog;
        private boolean cacheLog;
    }
}
```
#### 4.2.2 yml文件增加过滤器属性Log
```
# 服务基础配置
server:
  port: 7000
spring:
  application:
    name: api-gateway
  # 网关配置
  cloud:
    # 增加nacos配置信息
    nacos:
      discovery:
        server-addr: localhost:8848
    gateway:
      # 使gateway可以读取nacos中服务信息
      discovery:
        locator:
          enabled: true
      # 路由数组，支持多个路由，路由配置参照RouteDefinition
      routes:
        - id: route-product # 路由标识
#          uri: http://localhost:8081 # 要转发到的远程服务地址
          uri: lb://service-product # lb支持负载均衡(loadBalance)
          order: 0 # 排序，数字越小优先级越高
          predicates: #断言数组，满足条件实现转发，支持自定义断言
            - Path=/product-serv/**
#            - Age=18,60 #自定义断言，实现服务只能被18-60岁之间的人访问
          filters: #过滤数组，转发前服务处理，支持前置过滤和后置过滤，支持自定义过滤（局部过滤、全局过滤）
            - StripPrefix=1 # 转发前去掉一层访问路径
            - Log=true, false # 自定义过滤，控制日志输出
```
#### 4.2.3 测试验证
浏览器请求`http://localhost:7000/product-serv/product/1`,控制台输出"输出控制台日志"表示成功。

### 4.3 自定义全局过滤器
设计统一鉴权逻辑，鉴权中心忽略
#### 4.3.1 新增全局过滤器类AuthGlobalFilter
```
package cn.hzp.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    /**
     * 自定义过滤器逻辑
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 统一鉴权逻辑
        String token = exchange.getRequest().getQueryParams().getFirst("token");
        if(!StringUtils.equals("admin", token)) {
            log.info("认证失败");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    /**
     * 当前过滤器优先级，值越小优先级越高
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
```

#### 4.3.2 测试验证
- 浏览器请求`http://localhost:7000/product-serv/product/1?token=admin`,正常返回结果
- 浏览器请求`http://localhost:7000/product-serv/product/1?token=admins`,返回401

## 5 gateway集成sentinel限流
- 1 路由维度限流，根据路由id进行限流
- 2 自定义api维度限流，根据api分组进行限流

### 5.1 路由维度限流案例
#### 5.1.1 pom文件增加依赖sentinel-spring-cloud-gateway-adapter
```
        <!--gateway集成sentinel实现限流-->
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-spring-cloud-gateway-adapter</artifactId>
        </dependency>
```
#### 5.1.2 编写配置类GatewaySentinelConfiguration
注入SentinelGatewayFilter和SentinelGatewayBlockExceptionHandler
```
package cn.hzp.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import javafx.beans.property.ObjectProperty;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

@Configuration
public class GatewaySentinelConfiguration {
    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewaySentinelConfiguration(ObjectProvider<List<ViewResolver>> vewResolverProvider, ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = vewResolverProvider.getIfAvailable(Collections:: emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * 初始化一个限流过滤器
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter(){
        return new SentinelGatewayFilter();
    }

    /**
     * 配置初始化的限流参数
     */
    @PostConstruct
    public void initGatewayRules(){
        Set<GatewayFlowRule> rules = new HashSet<>();
        // resource为路由id，count为阈值，interval为统计时间窗口，默认1秒
        rules.add(new GatewayFlowRule("route-product")
        .setCount(1)
        .setIntervalSec(1));
        GatewayRuleManager.loadRules(rules);
    }

    /**
     * 配置异常处理器
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler(){
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    /**
     * 自定义限流异常返回页面
     */
    @PostConstruct
    public void initBlockHandlers() {
        BlockRequestHandler blockRequestHandler = (serverWebExchange, throwable) -> {
            Map map = new HashMap<>();
            map.put("code", 0);
            map.put("message", "接口被限流了");
            return ServerResponse.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(BodyInserters.fromObject(map));
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }

}
```

#### 5.1.3 启动测试
浏览器请求`http://localhost:7000/product-serv/product/1?token=admin`，多次刷新页面出现限流，测试成功

### 5.2 api维度限流简述
#### 5.2.1 修改配置类
在配置类中新增方法自定义api分组,修改初始化限流方法initGatewayRules
```
package cn.hzp.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

import javax.annotation.PostConstruct;
import java.util.*;

@Configuration
public class GatewaySentinelConfiguration {
    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewaySentinelConfiguration(ObjectProvider<List<ViewResolver>> vewResolverProvider, ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = vewResolverProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * 初始化一个限流过滤器
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    /**
     * 配置初始化的限流参数
     */
    @PostConstruct
    public void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        // 以路由id为限流维度，resource为路由id，count为阈值，interval为统计时间窗口，默认1秒
//        rules.add(new GatewayFlowRule("route-product").setCount(1).setIntervalSec(1));
        // 以api分组为限流维度
        rules.add(new GatewayFlowRule("product-api1").setCount(1).setIntervalSec(1));
        rules.add(new GatewayFlowRule("product-api2").setCount(1).setIntervalSec(1));
        GatewayRuleManager.loadRules(rules);
    }

    /**
     * 配置异常处理器
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    /**
     * 自定义限流异常返回页面
     */
    @PostConstruct
    public void initBlockHandlers() {
        BlockRequestHandler blockRequestHandler = (serverWebExchange, throwable) -> {
            Map map = new HashMap<>();
            map.put("code", 0);
            map.put("message", "接口被限流了");
            return ServerResponse.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(BodyInserters.fromObject(map));
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }

    /**
     * api维度限流，自定义api分组
     */
    @PostConstruct
    public void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();
        // api分组，apiName自定义唯一即可
        ApiDefinition api1 = new ApiDefinition("product-api1")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem()
                            .setPattern("/product-serv/api1/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX)
                    );
                }});
        ApiDefinition api2 = new ApiDefinition("product-api2")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem()
                            .setPattern("/product-serv/api2/demo2")
                    );
                }});
        definitions.add(api1);
        definitions.add(api2);
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }

}
```
#### 5.2.2 product服务中ProductController中增加测试方法
```

    /**
     * 测试gateway的api分组限流
     */
    @RequestMapping("/api1/demo1")
    public String api1Demo1() {
        return "/api1/demo1";
    }
    /**
     * 测试gateway的api分组限流
     */
    @RequestMapping("/api1/demo2")
    public String api1Demo2() {
        return "/api1/demo2";
    }
    /**
     * 测试gateway的api分组限流
     */
    @RequestMapping("/api2/demo1")
    public String api2Demo1() {
        return "/api2/demo1";
    }
    /**
     * 测试gateway的api分组限流
     */
    @RequestMapping("/api2/demo2")
    public String api2Demo2() {
        return "/api2/demo2";
    }
```

#### 5.2.3 测试验证
- 1 浏览器疯狂请求`http://localhost:7000/product-serv/api1/demo1`，会提示被限流
- 2 浏览器疯狂请求`http://localhost:7000/product-serv/api1/demo2`，会提示被限流
- 3 浏览器疯狂请求`http://localhost:7000/product-serv/api2/demo1`，一直正常
- 4 浏览器疯狂请求`http://localhost:7000/product-serv/api1/demo2`，会提示被限流