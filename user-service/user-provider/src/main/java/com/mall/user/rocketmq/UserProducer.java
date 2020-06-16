package com.mall.user.rocketmq;

import com.alibaba.fastjson.JSON;
import com.mall.user.dto.UserRegisterRequest;
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
import java.util.HashMap;

/**
 * @PackgeName: com.mall.user.rocketmq
 * @ClassName: UserProducer
 * @Author: Li Haiquan
 * Date: 2020/6/16 17:21
 * project name: cs-mall
 */
@Component
@Slf4j
public class UserProducer {
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
        log.info("topic----" + topicName);
        log.info("add-----" + address);
        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

    }

    public Boolean sendEmail(String uuid, UserRegisterRequest registerRequest){
        HashMap<String, Object> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("registerRequest", registerRequest);
        String jsonStr = JSON.toJSONString(map);
        Message message = new Message(topicName, jsonStr.getBytes(Charset.forName("utf-8")));
        try {
            defaultMQProducer.send(message);
            log.info("发送邮箱成功");
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
