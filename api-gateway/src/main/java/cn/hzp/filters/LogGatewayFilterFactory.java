//package cn.hzp.filters;
//
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.List;
//
///**
// * 网关gateway-自定义过滤器类
// */
//@Slf4j
//@Component
//public class LogGatewayFilterFactory extends AbstractGatewayFilterFactory<LogGatewayFilterFactory.Config> {
//    /**
//     * 构造函数
//     */
//    public LogGatewayFilterFactory() {
//        super(LogGatewayFilterFactory.Config.class);
//    }
//
//    /**
//     * 将配置文件的属性信息赋值到配置类中
//     */
//    @Override
//    public List<String> shortcutFieldOrder() {
//        return Arrays.asList("consoleLog", "cacheLog");
//    }
//
//    /**
//     * 自定义过滤逻辑
//     */
//    @Override
//    public GatewayFilter apply(LogGatewayFilterFactory.Config config) {
//        return (exchange, chain) -> {
//            if (config.isConsoleLog()) {
//                log.info("输出控制台日志");
//            }
//            if (config.isCacheLog()) {
//                log.info("输出缓存日志");
//            }
//            return chain.filter(exchange);
//        };
//    }
//
//    /**
//     * 配置类
//     */
//    @Data
//    @NoArgsConstructor
//    public static class Config {
//        private boolean consoleLog;
//        private boolean cacheLog;
//    }
//}
