package com.mall.order.biz.handler;

import com.mall.commons.tool.exception.BizException;
import com.mall.commons.tool.utils.NumberUtils;
import com.mall.order.biz.callback.SendEmailCallback;
import com.mall.order.biz.callback.TransCallback;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.constants.OrderConstants;
import com.mall.order.dal.entitys.Order;
import com.mall.order.dal.entitys.OrderItem;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dto.CartProductDto;
import com.mall.order.utils.GlobalIdGeneratorUtil;
import com.mall.user.constants.SysRetCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *  ciggar
 * create-date: 2019/8/1-下午5:01
 * 初始化订单 生成订单
 */

@Slf4j
@Component
public class InitOrderHandler extends AbstractTransHandler {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @Transactional
    public boolean handle(TransHandlerContext context) {
        CreateOrderContext createOrderContext = (CreateOrderContext) context;

        //插入订单表
        Order order = new Order();
        String orderId = UUID.randomUUID().toString();
        order.setOrderId(orderId);
        order.setUserId(createOrderContext.getUserId());
        order.setBuyerNick(createOrderContext.getBuyerNickName());
        order.setPayment(createOrderContext.getOrderTotal());
        System.out.println(new Date());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setStatus(OrderConstants.ORDER_STATUS_INIT);
        int orderInsert = orderMapper.insert(order);
        if(orderInsert < 1){
            throw new BizException(OrderRetCode.DB_EXCEPTION.getCode(), OrderRetCode.DB_EXCEPTION.getMessage());
        }


        List<Long> buyProductIds = new ArrayList();
        //插入订单商品关联表
        List<CartProductDto> cartProductDtoList = createOrderContext.getCartProductDtoList();
        for (CartProductDto cartProductDto : cartProductDtoList) {
            OrderItem orderItem = new OrderItem();
            String orderItemId = UUID.randomUUID().toString();
            orderItem.setId(orderItemId);
            orderItem.setItemId(cartProductDto.getProductId());
            orderItem.setOrderId(orderId);
            orderItem.setNum(cartProductDto.getProductNum().intValue());
            orderItem.setPrice(cartProductDto.getSalePrice().doubleValue());
            orderItem.setTitle(cartProductDto.getProductName());
            orderItem.setPicPath(cartProductDto.getProductImg());
            BigDecimal total = cartProductDto.getSalePrice().multiply(new BigDecimal(cartProductDto.getProductNum()));
            orderItem.setTotalFee(total.doubleValue());

            //已锁定仓库
            orderItem.setStatus(1);
            buyProductIds.add(cartProductDto.getProductId());

            int orderItemInsert = orderItemMapper.insert(orderItem);
            if(orderItemInsert < 1){
                throw new BizException(OrderRetCode.DB_EXCEPTION.getCode(), OrderRetCode.DB_EXCEPTION.getMessage());
            }
        }

        createOrderContext.setOrderId(orderId);
        createOrderContext.setBuyProductIds(buyProductIds);
        return true;
    }
}
