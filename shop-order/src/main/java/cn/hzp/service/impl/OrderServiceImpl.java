package cn.hzp.service.impl;

import cn.hzp.dao.OrderDao;
import cn.hzp.dao.TxLogDao;
import cn.hzp.domain.Order;
import cn.hzp.domain.TxLog;
import cn.hzp.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private TxLogDao txLogDao;

    @Override
    public void save(Order order) {
        orderDao.save(order);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveMQTest(String txId, Order order) {
        TxLog txLog = new TxLog();
        txLog.setTxId(txId);
        txLog.setDate(new Date());
        TxLog txLogResult = txLogDao.save(txLog);
        log.info("保存日志信息,便于mq回查事务结果,{}", txLogResult);
        Order orderResult = orderDao.save(order);
        log.info("保存订单信息,{}", orderResult);
    }
}
