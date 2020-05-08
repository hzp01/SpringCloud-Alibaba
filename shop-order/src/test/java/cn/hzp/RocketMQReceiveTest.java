package cn.hzp;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * - 1 创建消费者DefaultMQPushConsumer，组名consumerGroup
 * - 2 指定nameserver地址192.168.149.11:9876
 * - 3 指定消费者订阅subscribe的主题myTopic和标签*
 * - 4 设置回调函数registerMessageListener，通过MessageListenerConcurrently对象编写处理消息方法，返回消息状态ConsumeConcurrentlyStatus.CONSUME_SUCCESS
 * - 5 启动消费者start
 * 2 启动接收消息类，查看控制台，再启动发送消息类，再看接收控制台
 */
public class RocketMQReceiveTest {
    public static void main(String[] args) {
        try {
            DefaultMQPushConsumer consumerGroup = new DefaultMQPushConsumer("consumerGroup");
            consumerGroup.setNamesrvAddr("192.168.149.11:9876");
            consumerGroup.subscribe("myTopic", "*");
            consumerGroup.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
                System.out.println("消息列表为：" + list);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            consumerGroup.start();
            System.out.println("消费者启动");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
