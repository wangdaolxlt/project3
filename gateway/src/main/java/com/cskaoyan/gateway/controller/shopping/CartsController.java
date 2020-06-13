package com.cskaoyan.gateway.controller.shopping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.shopping.ICartService;
import com.mall.shopping.dto.AddCartRequest;
import com.mall.shopping.dto.AddCartResponse;
import com.mall.shopping.dto.CartListByIdRequest;
import com.mall.shopping.dto.CartListByIdResponse;
import com.mall.user.intercepter.TokenIntercepter;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @PackgeName: com.cskaoyan.gateway.controller.shopping
 * @ClassName: CartsController
 * @Author: Pipboy
 * Date: 2020/6/12 15:31
 * project name: project3
 * @Version:
 * @Description:
 */

@RestController
@RequestMapping("shopping")
public class CartsController {

    @Reference(timeout = 3000,check = false)
    ICartService cartService;

    /**
     * 获取购物车列表
     * @return
     */
    @GetMapping("carts")
    public ResponseData carts(HttpServletRequest httpServletRequest) {
        String userInfo = (String) httpServletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject object = JSON.parseObject(userInfo);
        long uid = Long.parseLong(object.get("uid").toString());

        CartListByIdRequest idRequest = new CartListByIdRequest();
        idRequest.setUserId(uid);
        CartListByIdResponse cartListById = cartService.getCartListById(idRequest);

        ResponseData<Object> data = new ResponseData<>();
        data.setCode(200);
        data.setMessage("success");
        data.setResult(cartListById);
        data.setSuccess(true);
        data.setTimestamp(System.currentTimeMillis());
        return data;
    }

    @PostMapping("carts")
    public ResponseData carts(@RequestBody AddCartRequest addCartRequest) {

        AddCartResponse response = cartService.addToCart(addCartRequest);

        ResponseData<Object> data = new ResponseData<>();
        data.setCode(200);
        data.setMessage("success");
        data.setResult(response.getResult());
        data.setSuccess(true);
        data.setTimestamp(System.currentTimeMillis());
        return data;
    }
}
