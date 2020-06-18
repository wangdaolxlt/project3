package com.mall.order;

import com.mall.order.dto.*;
import com.mall.promo.dto.SeckillOrderRequest;
import com.mall.promo.dto.SeckillOrderResponse;

/**
 *  ciggar
 * create-date: 2019/7/30-上午9:13
 * 订单相关业务
 */
public interface OrderCoreService {

    /**
     * 创建订单
     * @param request
     * @return
     */
    CreateOrderResponse createOrder(CreateOrderRequest request);

    /**
     * 创建秒杀订单
     */
    SeckillOrderResponse createSeckillOrder(SeckillOrderRequest request);


    /**
     * 取消订单
     * @param orderId
     * @return
     */
    CancelOrderResponse cancelOrder(String orderId);

    /**
     * 删除订单
     * @param request
     * @return
     */
    DeleteOrderResponse deleteOrder(DeleteOrderRequest request);
}
