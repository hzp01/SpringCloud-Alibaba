//package cn.hzp.config;
//
//import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * 测试sentinel组件中的授权规则：区分应用来源
// */
//@Component
//public class MyRequestOriginParser implements RequestOriginParser {
//    /**
//     * 区分来源：本质通过request域获取来源标识
//     * 根据不同来源如app/pc，最后返回结果值交给sentinel流控匹配处理
//     */
//    @Override
//    public String parseOrigin(HttpServletRequest request) {
//        String servName = request.getParameter("servName");
//        if (StringUtils.isEmpty(servName)) {
//            throw new RuntimeException("servName不能为空");
//        }
//        return servName;
//    }
//}
