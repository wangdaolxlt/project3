package com.mall.order.services;

import com.mall.commons.tool.exception.BizException;
import com.mall.order.OrderCoreService;
import com.mall.order.biz.TransOutboundInvoker;
import com.mall.order.biz.context.AbsTransHandlerContext;
import com.mall.order.biz.factory.OrderProcessPipelineFactory;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.constants.OrderConstants;
import com.mall.order.dal.entitys.Order;
import com.mall.order.dal.entitys.OrderItem;
import com.mall.order.dal.entitys.Stock;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.OrderShippingMapper;
import com.mall.order.dal.persistence.StockMapper;
import com.mall.order.dto.*;
import com.mall.order.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  ciggar
 * create-date: 2019/7/30-上午10:05
 */
@Slf4j
@Component
@Service(cluster = "failfast")
public class OrderCoreServiceImpl implements OrderCoreService {

	@Autowired
	OrderMapper orderMapper;

	@Autowired
	OrderItemMapper orderItemMapper;

	@Autowired
	OrderShippingMapper orderShippingMapper;

	@Autowired
    OrderProcessPipelineFactory orderProcessPipelineFactory;
	@Autowired
	StockMapper stockMapper;

	/**
	 * 创建订单的处理流程
	 *
	 * @param request
	 * @return
	 */
	@Override
	public CreateOrderResponse createOrder(CreateOrderRequest request) {
		CreateOrderResponse response = new CreateOrderResponse();
		try {
			//创建pipeline对象
			TransOutboundInvoker invoker = orderProcessPipelineFactory.build(request);

			//启动pipeline
			invoker.start(); //启动流程（pipeline来处理）

			//获取处理结果
			AbsTransHandlerContext context = invoker.getContext();

			//把处理结果转换为response
			response = (CreateOrderResponse) context.getConvert().convertCtx2Respond(context);
		} catch (Exception e) {
			log.error("OrderCoreServiceImpl.createOrder Occur Exception :" + e);
			ExceptionProcessorUtils.wrapperHandlerException(response, e);
		}
		return response;
	}

	/**
	 * 取消订单
	 * @param request
	 */
	@Override
	@Transactional
	public CancelOrderResponse cancelOrder(CancelOrderRequest request) {
		CancelOrderResponse response = new CancelOrderResponse();
		//通过id查当前订单的状态
		Order order = orderMapper.selectByPrimaryKey(request.getOrderId());
		/**
		 * 状态在已发货之前才可以取消订单
		 */
		if(order.getStatus() > 2){
			response.setMsg("订单已发货无法取消订单");
			return response;
		}

		//取消订单，即将订单的status改为交易取消
		order.setStatus(7);
		int updateOrder = orderMapper.updateByPrimaryKey(order);
		if(updateOrder < 1){
		    response.setCode(OrderRetCode.DB_EXCEPTION.getCode());
			response.setMsg(OrderRetCode.DB_EXCEPTION.getMessage());
			return response;
		}

		//查询订单中的商品
		Example orderItemExample = new Example(OrderItem.class);
		orderItemExample.createCriteria().andEqualTo("orderId", request.getOrderId());
		List<OrderItem> orderItems = orderItemMapper.selectByExample(orderItemExample);
		List<Long> productIds = new ArrayList<>();
		for (OrderItem orderItem : orderItems) {
			productIds.add(orderItem.getItemId());
		}
		//锁定库存
		List<Stock> stockList = stockMapper.findStocksForUpdate(productIds);

		//恢复库存
		for (OrderItem orderItem : orderItems) {
			Long productId = orderItem.getItemId();
			Integer productNum = orderItem.getNum();

			Stock stock = new Stock();
			stock.setItemId(productId);
			stock.setLockCount(-productNum.intValue());
			stock.setStockCount(productNum.longValue());
			stockMapper.updateStock(stock);
		}
		response.setCode("000000");
		return response;
	}

	/**
	 * 删除订单
	 * @param request
	 * @return
	 */
	@Override
	public DeleteOrderResponse deleteOrder(DeleteOrderRequest request) {
		DeleteOrderResponse response = new DeleteOrderResponse();
		//通过id查当前订单的状态
		Order order = orderMapper.selectByPrimaryKey(request.getOrderId());
		/**
		 *规定还在进行中的订单无法删除
		 */
		if(order.getStatus() < 4){
			response.setMsg("订单仍在进行无法删除");
			return response;
		}

		int delete = orderMapper.deleteByPrimaryKey(order);
		if(delete < 1){
            response.setCode(OrderRetCode.DB_EXCEPTION.getCode());
			response.setMsg(OrderRetCode.DB_EXCEPTION.getMessage());
			return response;
		}
		response.setCode("000000");
		return response;
	}

}
