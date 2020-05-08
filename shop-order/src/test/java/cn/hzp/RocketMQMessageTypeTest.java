package cn.hzp;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
@Slf4j
public class RocketMQMessageTypeTest {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void ordinaryMessage() throws Exception {
        // 同步消息,第一个参数为topic:tag，tag可以为空，直接写topic；
        SendResult sendResult = rocketMQTemplate.syncSend("typeTopicSync:tag1", "这是同步消息", 10000);
        log.info("同步消息发送结果为：{}", sendResult);

        // 异步消息
        rocketMQTemplate.asyncSend("typeTopicAync", "这是异步消息", new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("消息发送成功,{}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.info("消息发送异常，{}", throwable.getMessage());
            }
        });
        log.info("异步消息准备要发送了");
        Thread.sleep(30000);

        // 单向消息
        rocketMQTemplate.sendOneWay("typeTopicOneWay", "这是单向消息");
    }

    @Test
    public void orderMessage() {
        // 单向顺序消息，hashkey为分配到哪个队列的key
        for (int i = 0; i < 10; i++) {
            rocketMQTemplate.sendOneWayOrderly("typeTopicOneWay", "这是单向消息", "xxx");
        }
    }
}
