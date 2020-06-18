package com.mall.promo.mq;

import com.alibaba.fastjson.JSON;
import com.mall.promo.cache.CacheManager;
import com.mall.promo.dal.persistence.PromoItemMapper;
import com.mall.promo.dto.SeckillOrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * @PackgeName: com.mall.promo.mq
 * @ClassName: PromoMqProducer
 * @Author: Li Haiquan
 * Date: 2020/6/18 23:18
 * project name: cs-mall
 */
@Slf4j
@Component
public class PromoMqProducer {
    @Value("${mq.nameserver.addr}")
    private String addr;

    @Value("${mq.topicname}")
    private String topic;

    @Autowired
    private PromoItemMapper promoItemMapper;

    @Autowired
    private CacheManager cacheManager;

    private TransactionMQProducer transactionMQProducer;

    @PostConstruct
    public void init(){
        transactionMQProducer = new TransactionMQProducer("transaction_producer");

        transactionMQProducer.setNamesrvAddr(addr);

        transactionMQProducer.setTransactionListener(new TransactionListener() {
            //  执行本地事务

            // 对于我们这里来说要去扣减秒杀库存
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object arg) {
                HashMap<String,Object> argsMap = (HashMap<String, Object>) arg;
                Long productId = (Long) argsMap.get("productId");
                Long psId = (Long) argsMap.get("psId");

                Integer effectRows = promoItemMapper.decreaseStock(productId, psId);
                if (effectRows < 1) {
                    log.info("扣减库存失败，请求参数，productId:{},psId:{}",productId,psId);
                    //假如本地事务执行失败了，也打个标记
                    String key = "local_transaction_id_" + message.getTransactionId();
                    cacheManager.setCache(key,"fail",3);

                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }

                //假如执行本地事务成功了，打个标记
                String key = "local_transaction_id_" + message.getTransactionId();
                cacheManager.setCache(key,"success",3);

                return LocalTransactionState.COMMIT_MESSAGE;
            }

            // 检查本地事务
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                String key = "local_transaction_id_" + messageExt.getTransactionId();
                String value = cacheManager.checkCache(key);
                if (StringUtils.isBlank(value)) {
                    return LocalTransactionState.UNKNOW;
                }
                else if (value.equals("success")) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
                else if (value.equals("fail")) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.UNKNOW;
            }
        });
        try {
            transactionMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    // 发送一个事务型消息
    public Boolean sendOrderMessageIntransaction(SeckillOrderRequest request) {

        HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("productId",request.getProductId());
        messageMap.put("userId",request.getUserId());
        messageMap.put("username",request.getUserName());
        messageMap.put("price",request.getSeckillPrice());
        messageMap.put("addressId",request.getAddressId());

        messageMap.put("tel",request.getTel());
        messageMap.put("streetName",request.getStreetName());

        HashMap<String, Object> argsMap = new HashMap<>();
        argsMap.put("productId",request.getProductId());
        argsMap.put("psId",request.getPsId());

        String bodyStr = JSON.toJSONString(messageMap);

        Message  message = new Message(topic,bodyStr.getBytes(Charset.forName("utf-8")));

        TransactionSendResult transactionSendResult = null;
        try {

            //发送事务型消息
            transactionSendResult = transactionMQProducer.sendMessageInTransaction(message, argsMap);
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        // 获取发送消息的状态
        SendStatus sendStatus = transactionSendResult.getSendStatus();
        if (SendStatus.SEND_OK.equals(sendStatus)) {
            return Boolean.TRUE;
        }

        return false;
    }
}
