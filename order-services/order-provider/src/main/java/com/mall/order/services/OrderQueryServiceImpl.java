package com.mall.order.services;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.mall.commons.tool.exception.BizException;
import com.mall.order.OrderQueryService;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.converter.OrderConverter;
import com.mall.order.dal.entitys.*;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.OrderShippingMapper;
import com.mall.order.dto.*;
import com.mall.order.utils.ExceptionProcessorUtils;
import com.mall.user.constants.SysRetCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 *  ciggar
 * create-date: 2019/7/30-上午10:04
 */
@Slf4j
@Component
@Service
public class OrderQueryServiceImpl implements OrderQueryService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderConverter orderConverter;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    OrderShippingMapper orderShippingMapper;


    /**
     * 查询全部订单
     * @param request
     * @return
     */
    @Override
    public OrderListResponse orderList(OrderListRequest request) {
        PageHelper.startPage(request.getPage(), request.getSize());
        Long userId = request.getUserId();

        //通过userId查其所有订单
        Example orderExample = new Example(Order.class);
        orderExample.createCriteria().andEqualTo("userId", userId);
        List<Order> orders = orderMapper.selectByExample(orderExample);
        List<OrderDetailInfo> orderDetailInfoList = new ArrayList<>();
        for (Order order : orders) {
            //converter
            OrderDetailInfo orderDetailInfo = orderConverter.order2detail(order);


            //通过orderId查订单中所有的Item
            Example orderItemExample = new Example(OrderItem.class);
            orderItemExample.createCriteria().andEqualTo("orderId",order.getOrderId());
            List<OrderItem> orderItems = orderItemMapper.selectByExample(orderItemExample);
            if (CollectionUtils.isEmpty(orderItems)) {
                throw new BizException(OrderRetCode.DB_EXCEPTION.getCode(),OrderRetCode.DB_EXCEPTION.getMessage());
            }
            List<OrderItemDto> orderItemDto = orderConverter.item2dto(orderItems);
            orderDetailInfo.setOrderItemDto(orderItemDto);

            //通过orderId查订单的快递信息
            OrderShipping orderShipping = orderShippingMapper.selectByPrimaryKey(order.getOrderId());
            OrderShippingDto orderShippingDto = orderConverter.shipping2dto(orderShipping);
            orderDetailInfo.setOrderShippingDto(orderShippingDto);

            orderDetailInfoList.add(orderDetailInfo);
        }

        PageInfo<OrderDetailInfo> pageInfo = new PageInfo<>(orderDetailInfoList);
        long total = pageInfo.getTotal();
        OrderListResponse orderListResponse = new OrderListResponse();
        orderListResponse.setData(orderDetailInfoList);
        orderListResponse.setTotal(total);
        return orderListResponse;
    }

    /**
     * 查询订单详情
     * @param request
     * @return
     */
    @Override
    public OrderDetailRespVo orderDetail(OrderDetailRequest request) {
        //取出订单信息
        Order order = orderMapper.selectByPrimaryKey(request.getOrderId());
        OrderDetailRespVo response = orderConverter.order2response(order);
        //通过订单id取商品信息
        Example orderItemExample = new Example(OrderItem.class);
        orderItemExample.createCriteria().andEqualTo("orderId", request.getOrderId());
        List<OrderItem> orderItems = orderItemMapper.selectByExample(orderItemExample);
        List<OrderItemDto> orderItemDtos = orderConverter.item2dto(orderItems);
        response.setGoodsList(orderItemDtos);
        //在通过id取快递信息
        OrderShipping orderShipping = orderShippingMapper.selectByPrimaryKey(request.getOrderId());
        if(StringUtil.isNotEmpty(orderShipping.getReceiverPhone())){
            response.setTel(orderShipping.getReceiverPhone());
        }
        response.setTel(orderShipping.getReceiverMobile());
        response.setStreetName(orderShipping.getReceiverAddress());
        return response;
    }

}
