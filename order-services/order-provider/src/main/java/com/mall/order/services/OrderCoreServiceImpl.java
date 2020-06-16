package com.mall.order.services;

import com.mall.order.OrderCoreService;
import com.mall.order.biz.TransOutboundInvoker;
import com.mall.order.biz.context.AbsTransHandlerContext;
import com.mall.order.biz.factory.OrderProcessPipelineFactory;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dal.entitys.Order;
import com.mall.order.dal.entitys.OrderItem;
import com.mall.order.dal.entitys.Stock;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.OrderShippingMapper;
import com.mall.order.dal.persistence.StockMapper;
import com.mall.order.dto.*;
import com.mall.order.rocketmq.OrderProducer;
import com.mall.order.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
	@Autowired
	OrderProducer orderProducer;

	/**
	 * 创建订单的处理流程
	 *
	 * @param request
	 * @return
	 */
	@Override
	public CreateOrderResponse createOrder(CreateOrderRequest request) {
		/**
		 * 先对request进行一下处理
		 */
		//校验商品checked状态
		List<CartProductDto> list = request.getCartProductDtoList();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			CartProductDto temp = (CartProductDto) iterator.next();
			if("false".equals(temp.getChecked())){
				iterator.remove();
			}
		}
		//校验总金额数量
		BigDecimal total = new BigDecimal(0);
		for (CartProductDto cartProductDto : list) {
			total = total.add(cartProductDto.getSalePrice().multiply(new BigDecimal(cartProductDto.getProductNum())));
		}
		request.setCartProductDtoList(list);
		request.setOrderTotal(total);
		/**
		 * 正式处理下订单
		 */
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
			String orderId = response.getOrderId();
			orderProducer.delayCancelOrder(orderId);
		} catch (Exception e) {
			log.error("OrderCoreServiceImpl.createOrder Occur Exception :" + e);
			ExceptionProcessorUtils.wrapperHandlerException(response, e);
		}
		return response;
	}

	/**
	 * 取消订单
	 * @param orderId
	 * @return
	 */
	@Override
	@Transactional
	public CancelOrderResponse cancelOrder(String orderId) {
		CancelOrderResponse response = new CancelOrderResponse();
		//通过id查当前订单的状态
		Order order = orderMapper.selectByPrimaryKey(orderId);
		/**
		 * 先判断需不需要取消，已经付款就不需要取消订单
		 */
		if(order.getStatus() > 0){
			response.setCode(OrderRetCode.SUCCESS.getCode());
			return response;
		}

		//取消订单，即将订单的status改为交易取消
		order.setStatus(7);
		order.setUpdateTime(new Date());
		int updateOrder = orderMapper.updateByPrimaryKey(order);
		if(updateOrder < 1){
		    response.setCode(OrderRetCode.DB_EXCEPTION.getCode());
			response.setMsg(OrderRetCode.DB_EXCEPTION.getMessage());
			return response;
		}

		//查询订单中的商品
		Example orderItemExample = new Example(OrderItem.class);
		orderItemExample.createCriteria().andEqualTo("orderId", orderId);
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
		response.setCode(OrderRetCode.SUCCESS.getCode());
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
			response.setCode(OrderRetCode.SYSTEM_ERROR.getCode());
			return response;
		}

		int delete = orderMapper.deleteByPrimaryKey(order);
		if(delete < 1){
            response.setCode(OrderRetCode.DB_EXCEPTION.getCode());
			response.setMsg(OrderRetCode.DB_EXCEPTION.getMessage());
			return response;
		}
		response.setCode(OrderRetCode.SUCCESS.getCode());
		return response;
	}

}
