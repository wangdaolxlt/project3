package com.mall.order.rocketmq;

import com.mall.order.OrderCoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @PackgeName: com.mall.order.rocketmq
 * @ClassName: OrderConsumer
 * @Author: Li Haiquan
 * Date: 2020/6/16 11:31
 * project name: cs-mall
 */
@Component
@Slf4j
public class OrderConsumer {
    //start mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true
    @Autowired
    private OrderCoreService orderCoreService;

    private DefaultMQPushConsumer consumer;

    @Value("${mq.consumergroup}")
    private String consumerGroup;
    @Value("${mq.nameserver.addr}")
    private String address;
    @Value("${mq.topicname}")
    private String topicName;


    @PostConstruct
    public void init(){
        consumer = new DefaultMQPushConsumer(consumerGroup);
        //配置
        consumer.setNamesrvAddr(address);

        try {
            //订阅
            consumer.subscribe(topicName,"*");
        } catch (MQClientException e) {
            e.printStackTrace();
        }

        //监听器

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt messageExt = list.get(0);
                byte[] body = messageExt.getBody();
                String orderId = new String(body);
                orderCoreService.cancelOrder(orderId);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

        log.info("consumer启动");
    }
}
