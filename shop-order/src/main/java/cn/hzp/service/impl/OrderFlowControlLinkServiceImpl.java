package cn.hzp.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.stereotype.Service;

@Service
public class OrderFlowControlLinkServiceImpl {
    /**
     * 服务容错组件sentinel的流控测试：测试3种模式（直接、关联、链路）中的链路
     * 注解@SentinelResource，指定资源，value值为资源名称
     */
    @SentinelResource(value="resource")
    public String  flowControlLink(){
        return "服务容错组件sentinel的流控测试：测试3种模式（直接、关联、链路）中的链路";
    }
}
