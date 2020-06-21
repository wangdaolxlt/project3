package com.mall.order.services;

import com.mall.commons.tool.exception.BizException;
import com.mall.order.OrderCoreService;
import com.mall.order.biz.TransOutboundInvoker;
import com.mall.order.biz.context.AbsTransHandlerContext;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.factory.OrderProcessPipelineFactory;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.constants.OrderConstants;
import com.mall.order.dal.entitys.Order;
import com.mall.order.dal.entitys.OrderItem;
import com.mall.order.dal.entitys.OrderShipping;
import com.mall.order.dal.entitys.Stock;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.OrderShippingMapper;
import com.mall.order.dal.persistence.StockMapper;
import com.mall.order.dto.*;
import com.mall.order.rocketmq.OrderProducer;
import com.mall.order.utils.ExceptionProcessorUtils;
import com.mall.promo.constants.PromoRetCode;
import com.mall.promo.dto.SeckillOrderRequest;
import com.mall.promo.dto.SeckillOrderResponse;
import com.mall.shopping.IProductService;
import com.mall.shopping.dto.ProductDetailDto;
import com.mall.shopping.dto.ProductDetailResponse;
import com.mall.user.IMemberService;
import com.mall.user.dto.QueryMemberRequest;
import com.mall.user.dto.QueryMemberResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

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

	@Reference(timeout = 3000, check = false)
	private IProductService productService;
	@Reference(timeout = 3000, check = false)
	private IMemberService memberService;

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
		} catch (Exception e) {
			log.error("OrderCoreServiceImpl.createOrder Occur Exception :" + e);
			ExceptionProcessorUtils.wrapperHandlerException(response, e);
		}
		return response;
	}

	/**
	 * 创建秒杀订单
	 */
	@Override
	public SeckillOrderResponse createSeckillOrder(SeckillOrderRequest request) {
		SeckillOrderResponse response = new SeckillOrderResponse();
		/**
		 * 查询用户昵称
		 */
		QueryMemberRequest memberRequest = new QueryMemberRequest();
		memberRequest.setUserId(request.getUserId());
		QueryMemberResponse memberResponse = memberService.queryMemberById(memberRequest);
		String username = memberResponse.getUsername();
		/**
		 * 插入订单表
		 */
		Order order = new Order();
		String orderId = UUID.randomUUID().toString();
		order.setOrderId(orderId);
		order.setUserId(request.getUserId());
		order.setBuyerNick(username);
		order.setPayment(request.getSeckillPrice());
		order.setCreateTime(new Date());
		order.setUpdateTime(new Date());
		order.setStatus(OrderConstants.ORDER_STATUS_INIT);
		order.setUniqueKey(request.getUniqueKey());
		int orderInsert = orderMapper.insert(order);
		if(orderInsert < 1){
			response.setCode(OrderRetCode.DB_EXCEPTION.getCode());
			response.setMsg(OrderRetCode.DB_EXCEPTION.getMessage());
			return response;
		}
		/**
		 * 插入订单商品关联表
		 */
		ProductDetailResponse productDetailResp = productService.getProductDetails(request.getProductId());
		ProductDetailDto productDetailDto = productDetailResp.getProductDetailDto();
		OrderItem orderItem = new OrderItem();
		String orderItemId = UUID.randomUUID().toString();
		orderItem.setId(orderItemId);
		orderItem.setItemId(request.getProductId());
		orderItem.setOrderId(orderId);
		orderItem.setNum(1);
		orderItem.setPrice(request.getSeckillPrice().doubleValue());
		orderItem.setTitle(productDetailDto.getProductName());
		orderItem.setPicPath(productDetailDto.getProductImageBig());
		orderItem.setTotalFee(request.getSeckillPrice().doubleValue());
		orderItem.setStatus(1);
		int orderItemInsert = orderItemMapper.insert(orderItem);
		if(orderItemInsert < 1){
			response.setCode(OrderRetCode.DB_EXCEPTION.getCode());
			response.setMsg(OrderRetCode.DB_EXCEPTION.getMessage());
			return response;
		}
		/**
		 * 生成邮寄信息
		 */
		OrderShipping orderShipping = new OrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setReceiverName(request.getUserName());
		orderShipping.setReceiverAddress(request.getStreetName());
		orderShipping.setReceiverMobile(request.getTel());
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		int insert = orderShippingMapper.insert(orderShipping);
		if(insert < 1){
			response.setCode(OrderRetCode.SHIPPING_DB_SAVED_FAILED.getCode());
			response.setMsg(OrderRetCode.SHIPPING_DB_SAVED_FAILED.getMessage());
			return response;
		}
		response.setCode(PromoRetCode.SUCCESS.getCode());
		response.setMsg(PromoRetCode.SUCCESS.getMessage());
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

	/**
	 * 更新订单状态
	 * @param request
	 * @return
	 */
	@Override
	public UpdateOrderResponse updateOrder(UpdateOrderRequest request) {
		UpdateOrderResponse response = new UpdateOrderResponse();
		String orderId = request.getOrderId();
		Order order = new Order();
		order.setStatus(1);
		order.setOrderId(orderId);
		order.setUpdateTime(new Date());
		int updateRow = orderMapper.updateByPrimaryKeySelective(order);
		if(updateRow < 1){
			response.setCode(OrderRetCode.DB_EXCEPTION.getCode());
			response.setMsg(OrderRetCode.DB_EXCEPTION.getMessage());
			return response;
		}
		response.setCode(OrderRetCode.SUCCESS.getCode());
		response.setMsg(OrderRetCode.SUCCESS.getMessage());
		return response;
	}

}
