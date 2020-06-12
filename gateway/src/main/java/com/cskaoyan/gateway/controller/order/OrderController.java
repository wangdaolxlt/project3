package com.cskaoyan.gateway.controller.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.order.OrderCoreService;
import com.mall.order.OrderQueryService;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dto.CreateOrderRequest;
import com.mall.order.dto.CreateOrderResponse;
import com.mall.order.dto.OrderListRequest;
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

    @GetMapping("/order")
    public ResponseData queryOrder(OrderListRequest request, HttpServletRequest servletRequest){
        String userInfo = (String) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject object = JSON.parseObject(userInfo);
        long uid = Long.parseLong(object.get("uid").toString());
        request.setUserId(uid);

        return new ResponseUtil<>().setErrorMsg("");
    }

}
