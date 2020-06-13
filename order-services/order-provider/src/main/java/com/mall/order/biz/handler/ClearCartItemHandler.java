package com.mall.order.biz.handler;

import com.mall.commons.result.ResponseUtil;
import com.mall.commons.tool.exception.BizException;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dto.CartProductDto;
import com.mall.shopping.ICartService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  ciggar
 * create-date: 2019/8/1-下午5:05
 * 将购物车中的缓存失效
 */
@Slf4j
@Component
public class ClearCartItemHandler extends AbstractTransHandler {

    @Reference(timeout = 3000,check = false)
    ICartService cartService;

    //是否采用异步方式执行
    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean handle(TransHandlerContext context) {
        CreateOrderContext createOrderContext = (CreateOrderContext) context;
        Long uid = createOrderContext.getUserId();
        List<CartProductDto> cartProductDtoList = createOrderContext.getCartProductDtoList();
        for (CartProductDto cartProductDto : cartProductDtoList) {
            Long pid = cartProductDto.getProductId();
            DeleteCartItemRequest request = new DeleteCartItemRequest();
            request.setItemId(pid);
            request.setUserId(uid);
            DeleteCartItemResponse response = cartService.deleteCartItem(request);
            if(!response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
                return false;
            }
        }
        return true;
/*        DeleteCheckedItemRequest request = new DeleteCheckedItemRequest();
        request.setUserId(uid);
        DeleteCheckedItemResposne response = cartService.deleteCheckedItem(request);
        if(response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
            return true;
        }
        return false;*/
    }
}
