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