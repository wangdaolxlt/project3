package com.cskaoyan.gateway.controller.pay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.pay.PayService;
import com.mall.pay.constants.PayRetCode;
import com.mall.pay.dto.PayForm;
import com.mall.pay.dto.PaymentRequest;
import com.mall.pay.dto.PaymentResponse;
import com.mall.pay.dto.QueryAlipayStatusResponse;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.intercepter.TokenIntercepter;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.timer.Timeout;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * @PackgeName: com.cskaoyan.gateway.controller.pay
 * @ClassName: PayController
 * @Author: Li Haiquan
 * Date: 2020/6/20 22:21
 * project name: cs-mall
 */
@RestController
@RequestMapping("/cashier")
public class PayController {
    @Reference(timeout = 3000, check = false)
    PayService payService;

    @RequestMapping("/pay")
    public ResponseData pay(@RequestBody PayForm payForm, HttpServletRequest servletRequest){
        PaymentRequest request = new PaymentRequest();
        String userInfo = (String) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject object = JSON.parseObject(userInfo);
        long uid = Long.parseLong(object.get("uid").toString());

        request.setPayerUid((int) uid);
        request.setMoney(payForm.getMoney());
        request.setOrderId(payForm.getOrderId());
        request.setPayerName(payForm.getNickName());
        request.setPayWay(payForm.getPayType());
        request.setProductName(payForm.getInfo());
        request.setRemark("支付宝支付");

        PaymentResponse response = payService.aliPay(request);
        if(! SysRetCodeConstants.SUCCESS.getCode().equals(response.getCode())){
            return new ResponseUtil().setErrorMsg(response.getMsg());
        }
        String picName = response.getPicName();
        String url = "http://localhost:8080/image/" + picName;
        return new ResponseUtil().setData(url);
    }

    @GetMapping("/queryStatus")
    public ResponseData queryAlipayStatus(String orderId){
        if(StringUtils.isBlank(orderId)){
            return new ResponseUtil<>().setErrorMsg("传入参数不能为空");
        }

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(orderId);
        QueryAlipayStatusResponse response = payService.queryAlipayStatus(request);
        if(! PayRetCode.SUCCESS.getCode().equals(response.getCode())){
            return new ResponseUtil().setErrorMsg(response.getMsg());
        }
        return new ResponseUtil<>().setData(null);
    }
}
