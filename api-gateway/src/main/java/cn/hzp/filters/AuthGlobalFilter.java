//package cn.hzp.filters;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//@Slf4j
//public class AuthGlobalFilter implements GlobalFilter, Ordered {
//    /**
//     * 自定义过滤器逻辑
//     */
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        // 统一鉴权逻辑
//        String token = exchange.getRequest().getQueryParams().getFirst("token");
//        if(!StringUtils.equals("admin", token)) {
//            log.info("认证失败");
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//        return chain.filter(exchange);
//    }
//
//    /**
//     * 当前过滤器优先级，值越小优先级越高
//     */
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}
