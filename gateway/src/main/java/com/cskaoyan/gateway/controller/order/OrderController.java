package com.cskaoyan.gateway.controller.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.order.OrderCoreService;
import com.mall.order.OrderQueryService;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dto.*;
import com.mall.user.intercepter.TokenIntercepter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @PackgeName: com.cskaoyan.gateway.controller.order
 * @ClassName: OrderController
 * @Author: Li Haiquan
 * Date: 2020/6/11 23:44
 * project name: cs-mall
 */
@Slf4j
@RestController
@RequestMapping("/shopping")
public class OrderController {

    @Reference(timeout = 3000, check = false)
    private OrderCoreService orderCoreService;

    @Reference(timeout = 3000, check = false)
    private OrderQueryService orderQueryService;


    /**
     * 创建订单
     * @param request
     * @param servletRequest
     * @return
     */
    @PostMapping("/order" )
    public ResponseData order(@RequestBody CreateOrderRequest request, HttpServletRequest servletRequest){
        String userInfo = (String) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject object = JSON.parseObject(userInfo);
        long uid = Long.parseLong(object.get("uid").toString());
        request.setUserId(uid);

        request.setUniqueKey(UUID.randomUUID().toString());

        CreateOrderResponse response = orderCoreService.createOrder(request);
        if(response.getCode().equals(OrderRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setData(response.getOrderId());
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }

    /**
     * 当前用户所有订单
     * @param request
     * @param servletRequest
     * @return
     */
    @GetMapping("/order")
    public ResponseData queryOrder(OrderListRequest request, HttpServletRequest servletRequest){
        String userInfo = (String) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject object = JSON.parseObject(userInfo);
        long uid = Long.parseLong(object.get("uid").toString());
        request.setUserId(uid);

        OrderListResponse response = orderQueryService.orderList(request);

        return new ResponseUtil<>().setData(response);
    }

    /**
     * 查看订单详情
     * @param id
     * @return
     */
    @GetMapping("/order/{id}")
    public ResponseData orderDetail(@PathVariable("id") String id) {
        OrderDetailRequest request = new OrderDetailRequest();
        request.setOrderId(id);
        request.requestCheck();

        OrderDetailRespVo response = orderQueryService.orderDetail(request);
        return new ResponseUtil<>().setData(response);
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @PutMapping("/order/{id}")
    public ResponseData cancelOrder(@PathVariable("id") String id){
        CancelOrderRequest request = new CancelOrderRequest();
        request.setOrderId(id);
        request.requestCheck();

        CancelOrderResponse response = orderCoreService.cancelOrder(request);
        if(response.getCode().equals(OrderRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setData("成功");
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }


    /**
     * 删除订单
     * @param id
     * @return
     */
    @DeleteMapping("/order/{id}")
    public ResponseData deleteOrder(@PathVariable("id") String id){
        DeleteOrderRequest request = new DeleteOrderRequest();
        request.setOrderId(id);
        request.requestCheck();

        DeleteOrderResponse response = orderCoreService.deleteOrder(request);
        if(response.getCode().equals(OrderRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setData("成功");
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }
}
