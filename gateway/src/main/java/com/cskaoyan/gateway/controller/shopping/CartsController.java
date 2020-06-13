package com.cskaoyan.gateway.controller.shopping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.ICartService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.*;
import com.mall.user.constants.SysRetCodeConstants;
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
    /**
     * 更新购物车中的商品
     * @param requestBody
     * @return
     */
    @PutMapping("/carts")
    public ResponseData putCarts(@RequestBody UpdateCartRequestBody requestBody){

        Long userId = requestBody.getUserId();
        Long productId = requestBody.getProductId();
        Integer productNum = requestBody.getProductNum();
        String checked = requestBody.getChecked();

        UpdateCartNumRequest request = new UpdateCartNumRequest();
        request.setChecked(checked);
        request.setItemId(productId);
        request.setNum(productNum);
        request.setUserId(userId);
        // 更新商品数量和选中的状态
        UpdateCartNumResponse updateCartNumResponse = cartService.updateCartNum(request);
        if (updateCartNumResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
            return new ResponseUtil<>().setData(ShoppingRetCode.SUCCESS.getMessage());
        }

        return new ResponseUtil<>().setErrorMsg(updateCartNumResponse.getMsg());
    }

    /**
     * 删除购物车中的商品
     * @param uid
     * @param pid
     * @return
     */
    @DeleteMapping("/carts/{uid}/{pid}")
    public ResponseData dropCarts(@PathVariable("uid") Long uid, @PathVariable("pid") Long pid) {

        DeleteCartItemRequest request = new DeleteCartItemRequest();
        request.setItemId(pid);
        request.setUserId(uid);

        DeleteCartItemResponse deleteCartItemResponse = cartService.deleteCartItem(request);

        if (deleteCartItemResponse.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil<>().setData(ShoppingRetCode.SUCCESS.getMessage());
        }

        return new ResponseUtil<>().setErrorMsg(deleteCartItemResponse.getMsg());
    }

    /**
     * 删除购物车中选中的商品
     * @param uid
     * @return
     */
    @DeleteMapping("/items/{id}")
    public ResponseData deleteCheckedItem(@PathVariable("id") Long uid){
        DeleteCheckedItemRequest request = new DeleteCheckedItemRequest();
        request.setUserId(uid);
        DeleteCheckedItemResposne response = cartService.deleteCheckedItem(request);
        if(response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setData(ShoppingRetCode.SUCCESS.getMessage());
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }
}
