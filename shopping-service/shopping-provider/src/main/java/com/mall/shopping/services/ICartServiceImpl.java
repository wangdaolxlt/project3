//package com.mall.shopping.services;
//
//import com.alibaba.fastjson.JSON;
//import com.mall.shopping.ICartService;
//import com.mall.shopping.constants.ShoppingRetCode;
//import com.mall.shopping.converter.CartItemConverter;
//import com.mall.shopping.dal.entitys.Item;
//import com.mall.shopping.dal.persistence.ItemMapper;
//import com.mall.shopping.dto.*;
//import com.mall.shopping.utils.ExceptionProcessorUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RBucket;
//import org.redisson.api.RedissonClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.LinkedList;
//import java.util.List;
//
///**
// * @author lenovo
// * @PackgeName: ICartServiceImpl
// * @date: 2020/6/12
// * @Description:
// */
//@Slf4j
//@Component
//@Service
//public class ICartServiceImpl implements ICartService {
//
//    @Autowired
//    RedissonClient redissonClient;
//
//    @Autowired
//    ItemMapper itemMapper;
//
////    @Override
////    public AddCartResponse addToCart(AddCartRequest request) {
////
////        request.requestCheck();
////        Item item = itemMapper.selectByPrimaryKey(request.getItemId());
////        CartProductDto cartProductDto = CartItemConverter.item2Dto(item);
////        cartProductDto.setProductNum(Long.parseLong(request.getNum().toString()));
////        cartProductDto.setChecked("false");
//////        UpdateCartNumRequest updateCartNumRequest = new UpdateCartNumRequest();
//////        updateCartNumRequest.setChecked("false");
//////        updateCartNumRequest.setItemId(request.getItemId());
//////        updateCartNumRequest.setNum(request.getNum());
//////        updateCartNumRequest.setUserId(request.getUserId());
////
////        AddCartResponse addCartResponse = new AddCartResponse();
////        String key = "cart_" + request.getUserId();
////        try {
////            RBucket<String> bucket = redissonClient.getBucket(key);
////            List<CartProductDto> list = null;
////            if (bucket.get()!= null){
////                String s = bucket.get();
////                list = JSON.parseArray(s,CartProductDto.class);
////                Boolean isDuplicate = false;
////                for (CartProductDto cartDto : list) {
////                    if (cartDto.getProductId().equals(request.getItemId())){
////                        Long num = cartDto.getProductNum();
////                        num += request.getNum();
////                        cartDto.setProductNum(num);
////                        String string = JSON.toJSONString(list);
////                        bucket.set(string);
//////                        addCartResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
//////                        addCartResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
//////                        return addCartResponse;
////                        isDuplicate = true;
////
////                    }
////                }
////                if (!isDuplicate){
////                    list.add(cartProductDto);
////                }
////            }else {
////                list = new LinkedList<>();
////                list.add(cartProductDto);
////            }
////            String string = JSON.toJSONString(list);
////            bucket.set(string);
////            addCartResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
////            addCartResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
////        } catch (Exception e) {
////            log.error("INavigationServiceImpl.getNavigationList Occur Exception :" + e);
////            ExceptionProcessorUtils.wrapperHandlerException(addCartResponse,e);
////        }
////        return addCartResponse;
////    }
//
//    @Override
//    public UpdateCartNumResponse updateCartNum(UpdateCartNumRequest request) {
//
//        request.requestCheck();
//
//        UpdateCartNumResponse response = new UpdateCartNumResponse();
//
//        Long userId = request.getUserId();
//        Long itemId = request.getItemId();
//        Integer num = request.getNum();
//        String checked = request.getChecked();
//
//        String key = "cart_" + userId;
//        RBucket<String> bucket = redissonClient.getBucket(key);
//
//        try {
//            if (bucket.isExists()){
//                String cartJson = bucket.get();
//                List<CartProductDto> cartList = JSON.parseArray(cartJson, CartProductDto.class);
//
//                // 遍历 list 找到 userId和 itemId 都一样的那个商品，修改num,checked
//                for (CartProductDto cartProductDto : cartList) {
//                    Long productId = cartProductDto.getProductId();
//
//                    if (itemId.equals(productId)){
//                        cartProductDto.setProductNum(num.longValue());
//                        cartProductDto.setChecked(checked);
//                        break;
//                    }
//                }
//                // 更改redis里面的值
//                bucket.set(JSON.toJSONString(cartList));
//
//                response.setCode(ShoppingRetCode.SUCCESS.getCode());
//                response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
//
//            }else {
//                response.setCode(ShoppingRetCode.DB_EXCEPTION.getCode());
//                response.setMsg(ShoppingRetCode.DB_EXCEPTION.getMessage());
//            }
//        } catch (Exception e) {
//            log.error("ICartServiceImpl.updateCartNum Occur Exception :" + e);
//            ExceptionProcessorUtils.wrapperHandlerException(response, e);
//        }
//        return response;
//
//    }
//
//    @Override
//    @Transactional
//    public DeleteCartItemResponse deleteCartItem(DeleteCartItemRequest request) {
//        DeleteCartItemResponse response = new DeleteCartItemResponse();
//
//        try {
//            request.requestCheck();
//            String key = "cart_" + request.getUserId();
//
//            RBucket<String> bucket = redissonClient.getBucket(key);
//            if (bucket.isExists()){
//                String str = bucket.get();
//                List<CartProductDto> cartList = JSON.parseArray(str, CartProductDto.class);
//                for (CartProductDto item : cartList) {
//                    if (item.getProductId().equals(request.getItemId())){
//                        cartList.remove(item);
//                        break;
//                    }
//                }
//                bucket.set(JSON.toJSONString(cartList));
//                response.setCode(ShoppingRetCode.SUCCESS.getCode());
//                response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
//
//            }
//        } catch (Exception e) {
//            log.error("An error occurs in ICartServiceImpl.deleteCartItem :" + e);
//            ExceptionProcessorUtils.wrapperHandlerException(response,e);
//        }
//        return response;
//    }
//
//    @Override
//    public DeleteCheckedItemResposne deleteCheckedItem(DeleteCheckedItemRequest request) {
//        DeleteCheckedItemResposne response = new DeleteCheckedItemResposne();
//
//        try {
//            request.requestCheck();
//            String key = "cart_" + request.getUserId();
//            RBucket<String> bucket = redissonClient.getBucket(key);
//            if (bucket.get() != null){
//                String cartListJson = bucket.get();
//                List<CartProductDto> cartList = JSON.parseArray(cartListJson, CartProductDto.class);
//                for (CartProductDto temp : cartList) {
//                    if(temp.getChecked().equals("true")){
//                        cartList.remove(temp);
//                        break;
//                    }
//                }
//                bucket.set(JSON.toJSONString(cartList));
//                response.setCode(ShoppingRetCode.SUCCESS.getCode());
//                response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
//            }
//        } catch (Exception e) {
//            log.error("ICartServiceImpl.deleteCheckedItem Occurs Exception :" + e);
//            ExceptionProcessorUtils.wrapperHandlerException(response, e);
//        }
//        return response;
//    }
//}
