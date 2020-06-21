package com.mall.order.rocketmq;

import com.alibaba.fastjson.JSON;
import com.mall.order.OrderCoreService;
import com.mall.order.constant.OrderRetCode;
import com.mall.promo.dto.SeckillOrderRequest;
import com.mall.promo.dto.SeckillOrderResponse;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @PackgeName: com.mall.order.rocketmq
 * @ClassName: PromoOrderConsumer
 * @Author: Li Haiquan
 * Date: 2020/6/19 0:49
 * project name: cs-mall
 */
@Component
@Slf4j
public class PromoOrderConsumer {

    @Value("${mq.nameserver.addr}")
    private String addr;

    private final static String topicName = "promoTopic";

    private DefaultMQPushConsumer mqConsumer;

    @Autowired
    private OrderCoreService orderCoreService;

    @PostConstruct
    public void init() throws MQClientException {
        log.info("PromoOrderConsumer ->初始化...,topic:{},addre:{} ", topicName, addr);
        mqConsumer = new DefaultMQPushConsumer("promo_order_group");
        mqConsumer.setNamesrvAddr(addr);
        mqConsumer.subscribe(topicName, "*");

        // 消费一个创建秒杀订单的消息
        mqConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info("收到秒杀订单创建消息...");
                try {
                    MessageExt messageExt = msgs.get(0);
                    byte[] body = messageExt.getBody();
                    String bodyStr = new String(body);


                    Map<String,Object> map = JSON.parseObject(bodyStr, Map.class);
                    String username = (String) map.get("username");
                    Integer userId = (Integer) map.get("userId");
                    Integer productId = (Integer) map.get("productId");
                    BigDecimal price = (BigDecimal) map.get("price");

                    Integer addressId = (Integer) map.get("addressId");
                    String tel = (String) map.get("tel");
                    String streetName = (String) map.get("streetName");



                    // 组装参数
                    SeckillOrderRequest request = new SeckillOrderRequest();
                    request.setUserName(username);
                    request.setUserId(Long.valueOf(userId));
                    request.setProductId(Long.valueOf(productId));
                    request.setSeckillPrice(price);

                    request.setAddressId(Long.valueOf(addressId));
                    request.setTel(tel);
                    request.setStreetName(streetName);

                    log.info("秒杀创建订单接口参数request:{}", JSON.toJSONString(request));

                    // 生成订单 插入物流信息
                    SeckillOrderResponse response = orderCoreService.createSeckillOrder(request);
                    log.info("秒杀订单消费结果resp:{}",JSON.toJSON(response));

                    if (response.getCode().equals(OrderRetCode.SUCCESS.getCode())) {
                        log.info("秒杀订单返回了成功信息-------------");
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }else {
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });

        mqConsumer.start();
    }
}
