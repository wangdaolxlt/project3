package com.mall.user.rocketmq;

import com.alibaba.fastjson.JSON;
import com.mall.user.IUserService;
import com.mall.user.dto.UserRegisterRequest;
import com.mall.user.services.IUserServiceImpl;
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
import java.util.Map;


/**
 * @PackgeName: com.mall.user.rocketmq
 * @ClassName: UserConsumer
 * @Author: Li Haiquan
 * Date: 2020/6/16 17:21
 * project name: cs-mall
 */
@Component
@Slf4j
public class UserConsumer {

    @Autowired
    private IUserServiceImpl userService;

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
                String bodyStr = new String(body);
                Map map = JSON.parseObject(bodyStr, Map.class);
                String uuid = (String) map.get("uuid");
                UserRegisterRequest registerRequest = (UserRegisterRequest) map.get("registerRequest");
                userService.sendEmail(uuid, registerRequest);
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
