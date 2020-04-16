package cn.hzp.service.impl;

import cn.hzp.dao.OrderDao;
import cn.hzp.domain.Order;
import cn.hzp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Override
    public void save(Order order) {
        orderDao.save(order);
    }
}
