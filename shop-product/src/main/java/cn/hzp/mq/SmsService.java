package cn.hzp.mq;

import cn.hzp.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * - 需要实现RocketMQListener<消息内容>，消息内容为Order
 * 	- 监听注解@RocketMQMessageListener，
 * 		- 指定消费者组名consumerGroup
 * 		- 消费主题topic
 * 		- consumeMode消费模式，ORDERLY顺序，CONCURRENTLY默认同步即没顺序
 * 		- messageModel消息模式，
 * 			广播BRODCASTING，一个消息被多个消费者多次消费
 * 			默认集群CLUSTERING，一个消息只能被一个消费者消费
 */
@Slf4j
@Service
@RocketMQMessageListener(
        consumerGroup = "productGroup",
        topic = "orderTopic",
        consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel = MessageModel.CLUSTERING
)
public class SmsService implements RocketMQListener<Order> {
    @Override
    public void onMessage(Order order) {
        log.info("接收消息：{},接下来发送短信", order);
    }
}