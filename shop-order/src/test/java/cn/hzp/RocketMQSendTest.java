package cn.hzp;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * - 1 创建消息生产者DefaultMQProducer，设置组名produceGroup
 * - 2 为生产者指定nameserver地址192.168.149.11:9876
 * - 3 启动生产者start
 * - 4 创建消息对像Message：主题myTopic、标签myTag、消息体String.getBytes();
 * - 5 发送消息send
 * - 6 关闭生产者shutdown
 */
public class RocketMQSendTest {
    public static void main(String[] args) {
        try {
            DefaultMQProducer produceGroup = new DefaultMQProducer("produceGroup");
            produceGroup.setNamesrvAddr("192.168.149.11:9876");
            produceGroup.start();
            Message message = new Message("myTopic", "myTag", "消息体".getBytes());
            SendResult sendResult = produceGroup.send(message, 10000);
            System.out.println(sendResult);
            produceGroup.shutdown();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
