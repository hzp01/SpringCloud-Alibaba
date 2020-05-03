package cn.hzp.service.impl;

import cn.hzp.domain.Product;
import cn.hzp.service.ProductService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductServiceFallbackFactory implements FallbackFactory<ProductService> {
    @Override
    public ProductService create(Throwable throwable) {
        return pid -> {
            log.error("异常信息为：{}", throwable.getMessage());
            Product p = Product.builder()
                    .pid(-1)
                    .pname("远程调用产品服务查询产品信息异常")
                    .build();
            return p;
        };
    }
}
