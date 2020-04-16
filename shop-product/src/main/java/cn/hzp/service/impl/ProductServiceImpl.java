package cn.hzp.service.impl;

import cn.hzp.dao.ProductDao;
import cn.hzp.domain.Product;
import cn.hzp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;

    @Override
    public Product findById(Integer pid) {
        return productDao.findById(pid).get();
    }
}
