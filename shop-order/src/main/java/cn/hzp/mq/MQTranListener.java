package cn.hzp.mq;

import cn.hzp.dao.TxLogDao;
import cn.hzp.domain.Order;
import cn.hzp.domain.TxLog;
import cn.hzp.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQTransactionListener(txProducerGroup = "txProducerGroup")
public class MQTranListener implements RocketMQLocalTransactionListener {
    @Autowired
    private OrderService orderService;
    @Autowired
    private TxLogDao txLogDao;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        try {
            log.info("执行本地事务：进行下单操作");
            String txId = (String) message.getHeaders().get("txId");
            Order order = (Order) arg;
            orderService.saveMQTest(txId, order);
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        log.info("mq服务端执行消息回查");
        String txId = (String) message.getHeaders().get("txId");
        TxLog txLog = txLogDao.findById(txId).get();
        if (txLog != null) {
            log.info("回查结果，下单成功");
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            log.info("回查结果，下单失败");
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}