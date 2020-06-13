//package com.cskaoyan.gateway.controller.shopping;
//
//import com.mall.commons.result.ResponseData;
//import com.mall.commons.result.ResponseUtil;
//import com.mall.shopping.ICartService;
//import com.mall.shopping.constants.ShoppingRetCode;
//import com.mall.shopping.dto.*;
//
//import com.mall.user.constants.SysRetCodeConstants;
//import io.swagger.annotations.Api;
////import jdk.nashorn.internal.ir.annotations.Reference;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
///**
// * @author lenovo
// * @PackgeName: CartController
// * @date: 2020/6/12
// * @Description:
// */
//@RestController
//@RequestMapping("/shopping")
//@Api(tags = "CartController",description = "添加商品到购物车")
//public class CartController {
//
//    //@Reference(timeout = 3000,check = false)
//    ICartService cartService;
//
////    @PostMapping("/carts")
////    public ResponseData addCart(@RequestBody Map map){
////        AddCartRequest request = new AddCartRequest();
////        String userIdString = (String) map.get("userId");
////        Integer productIdInteger = (Integer) map.get("productId");
////        Integer productNum = (Integer) map.get("productNum");
////
////        long userId = Long.parseLong(userIdString);
////        long productId = Long.parseLong(productIdInteger.toString());
////
////        request.setUserId(userId);
////        request.setItemId(productId);
////        request.setNum(productNum);
////        AddCartResponse addCartResponse = cartService.addToCart(request);
////        if (addCartResponse.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
////            return new ResponseUtil<>().setData("成功");
////        }
////        return new ResponseUtil<>().setErrorMsg(addCartResponse.getMsg());
////    }
//
//    /**
//     * 更新购物车中的商品
//     * @param requestBody
//     * @return
//     */
//    @PutMapping("/carts")
//    public ResponseData putCarts(@RequestBody UpdateCartRequestBody requestBody){
//
//        Long userId = requestBody.getUserId();
//        Long productId = requestBody.getProductId();
//        Integer productNum = requestBody.getProductNum();
//        String checked = requestBody.getChecked();
//
//        UpdateCartNumRequest request = new UpdateCartNumRequest();
//        request.setChecked(checked);
//        request.setItemId(productId);
//        request.setNum(productNum);
//        request.setUserId(userId);
//        // 更新商品数量和选中的状态
//        UpdateCartNumResponse updateCartNumResponse = cartService.updateCartNum(request);
//        if (updateCartNumResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
//            return new ResponseUtil<>().setData(ShoppingRetCode.SUCCESS.getMessage());
//        }
//
//        return new ResponseUtil<>().setErrorMsg(updateCartNumResponse.getMsg());
//    }
//
//    /**
//     * 删除购物车中的商品
//     * @param uid
//     * @param pid
//     * @return
//     */
//    @DeleteMapping("/carts/{uid}/{pid}")
//    public ResponseData dropCarts(@PathVariable("uid") Long uid, @PathVariable("pid") Long pid) {
//
//        DeleteCartItemRequest request = new DeleteCartItemRequest();
//        request.setItemId(pid);
//        request.setUserId(uid);
//
//        DeleteCartItemResponse deleteCartItemResponse = cartService.deleteCartItem(request);
//
//        if (deleteCartItemResponse.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
//            return new ResponseUtil<>().setData(ShoppingRetCode.SUCCESS.getMessage());
//        }
//
//        return new ResponseUtil<>().setErrorMsg(deleteCartItemResponse.getMsg());
//    }
//
//    /**
//     * 删除购物车中选中的商品
//     * @param uid
//     * @return
//     */
//    @DeleteMapping("/items/{id}")
//    public ResponseData deleteCheckedItem(@PathVariable("id") Long uid){
//        DeleteCheckedItemRequest request = new DeleteCheckedItemRequest();
//        request.setUserId(uid);
//        DeleteCheckedItemResponse response = cartService.deleteCheckedItem(request);
//        if(response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
//            return new ResponseUtil<>().setData(ShoppingRetCode.SUCCESS.getMessage());
//        }
//        return new ResponseUtil<>().setErrorMsg(response.getMsg());
//    }
//
//}
