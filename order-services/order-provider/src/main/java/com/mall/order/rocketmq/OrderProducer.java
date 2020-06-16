package com.mall.order.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;

/**
 * @PackgeName: com.mall.order.rocketmq
 * @ClassName: OrderProducer
 * @Author: Li Haiquan
 * Date: 2020/6/16 11:34
 * project name: cs-mall
 */
@Component
@Slf4j
public class OrderProducer {
    private DefaultMQProducer defaultMQProducer;

    @Value("${mq.nameserver.addr}")
    private String address;
    @Value("${mq.topicname}")
    private String topicName;
    @Value("${mq.producergroup}")
    private String producerGroup;
    @PostConstruct
    public void init(){

        defaultMQProducer = new DefaultMQProducer(producerGroup);

        defaultMQProducer.setNamesrvAddr(address);

        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

    }

    public  Boolean delayCancelOrder(String orderId){
        Message message = new Message(topicName, orderId.getBytes(Charset.forName("utf-8")));
        message.setDelayTimeLevel(4);
        try {
            defaultMQProducer.send(message);
            log.info("发送了消息");
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Boolean.TRUE;
    }
}
