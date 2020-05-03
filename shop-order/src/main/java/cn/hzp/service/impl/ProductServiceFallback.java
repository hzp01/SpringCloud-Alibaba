package cn.hzp.service.impl;

import cn.hzp.domain.Product;
import cn.hzp.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceFallback implements ProductService {
    @Override
    public Product findByPid(Integer pid) {
        Product p = Product.builder()
                .pid(-1)
                .pname("远程调用产品服务查询产品信息异常")
                .build();
        return p;
    }
}
