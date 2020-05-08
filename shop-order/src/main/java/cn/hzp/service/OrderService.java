package cn.hzp.service;

import cn.hzp.domain.Order;

public interface OrderService {
    void save(Order order);

    void saveMQTest(String txId, Order order);
}
