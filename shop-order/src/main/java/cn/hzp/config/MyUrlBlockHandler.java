//package cn.hzp.config;
//
//import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
//import com.alibaba.csp.sentinel.slots.block.BlockException;
//import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
//import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
//import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
//import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
//import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
//import com.alibaba.fastjson.JSON;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * sentinel测试：自定义返回异常页面
// */
//@Component
//public class MyUrlBlockHandler implements UrlBlockHandler {
//    @Override
//    public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
//        httpServletResponse.setContentType("application/json;charset=UTF-8");
//        ResponseDate responseDate = null;
//        if (e instanceof FlowException) {
//            responseDate = new ResponseDate(-1, "接口被限流了");
//        } else if (e instanceof DegradeException) {
//            responseDate = new ResponseDate(-2, "接口被降级了");
//        } else if (e instanceof ParamFlowException) {
//            responseDate = new ResponseDate(-2, "接口被热点限流了");
//        } else if (e instanceof AuthorityException) {
//            responseDate = new ResponseDate(-2, "接口被授权规则限制访问了");
//        } else if (e instanceof SystemBlockException) {
//            responseDate = new ResponseDate(-2, "接口被系统规则限制了了");
//        }
//        httpServletResponse.getWriter().write(JSON.toJSONString(responseDate));
//    }
//}
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//class ResponseDate {
//    private Integer code;
//    private String message;
//}
