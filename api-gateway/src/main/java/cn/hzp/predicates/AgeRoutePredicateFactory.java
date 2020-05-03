//package cn.hzp.predicates;
//
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.function.Predicate;
//
///**
// * 断言类名称必须是"断言的配置属性"+RoutePredicateFactory
// * 必须继承AbstractRoutePredicateFactory<配置类>
// */
//@Slf4j
//@Component
//public class AgeRoutePredicateFactory extends AbstractRoutePredicateFactory<AgeRoutePredicateFactory.Config> {
//    /**
//     * 构造函数
//     */
//    public AgeRoutePredicateFactory() {
//        super(AgeRoutePredicateFactory.Config.class);
//    }
//
//    /**
//     * 读取配置文件的参数信息，赋值到配置类的属性上
//     */
//    @Override
//    public List<String> shortcutFieldOrder() {
//        // 参数顺序需要和配置文件参数顺序一致
//        return Arrays.asList("minAge", "maxAge");
//    }
//
//    /**
//     * 断言逻辑
//     */
//    @Override
//    public Predicate<ServerWebExchange> apply(AgeRoutePredicateFactory.Config config) {
//        return serverWebExchange -> {
//            String ageStr = serverWebExchange.getRequest().getQueryParams().getFirst("age");
//            if (StringUtils.isNotEmpty(ageStr)) {
//                int age = Integer.parseInt(ageStr);
//                if (age >= config.getMinAge() && age <= config.getMaxAge()) {
//                    return true;
//                }
//            }
//            return false;
//        };
//    }
//
//    /**
//     * 配置类，接收配置文件中的参数
//     */
//    @Data
//    public static class Config {
//        private int minAge;
//        private int maxAge;
//    }
//}
