package com.mall.shopping.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.tool.redisconfig.RedissonAutoConfiguration;
import com.mall.shopping.ICartService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.CartItemConverter;
import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dto.*;
import com.mall.shopping.services.cache.CacheManager;
import com.mall.shopping.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @PackgeName: com.mall.shopping.services
 * @ClassName: CartServiceImpl
 * @Author: Pipboy
 * Date: 2020/6/12 16:05
 * project name: project3
 * @Version:
 * @Description:
 */
@Slf4j
@Component
@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private ItemMapper itemMapper;


    @Override
    public CartListByIdResponse getCartListById(CartListByIdRequest request) {
        Long userId = request.getUserId();
        String userIdS = String.valueOf(userId);
        String data = cacheManager.checkCache(userIdS);
        String parse = ((String) JSON.parse(data));

        List<CartProductDto> list = new ArrayList<CartProductDto>();
        list = JSONObject.parseArray(parse, CartProductDto.class);

        CartListByIdResponse response = new CartListByIdResponse();
        response.setCartProductDtos(list);
        return response;
    }

    @Override
    public AddCartResponse addToCart(AddCartRequest request) {
        String s = String.valueOf(request.getUserId());

//        Example example = new Example(Item.class);
//        example.createCriteria().andEqualTo("id",request.getItemId());
        Item item = itemMapper.selectByPrimaryKey(request.getItemId());
        CartProductDto cartProductDto = CartItemConverter.item2Dto(item);
        ArrayList<CartProductDto> dtos = new ArrayList<>();
        dtos.add(cartProductDto);
        String dtosString = dtos.toString();

        cacheManager.setCache(s, dtosString, 3000);

        AddCartResponse response = new AddCartResponse();
        response.setResult("成功");
        return response;
    }

    @Override
    public UpdateCartNumResponse updateCartNum(UpdateCartNumRequest request) {

        request.requestCheck();

        UpdateCartNumResponse response = new UpdateCartNumResponse();

        Long userId = request.getUserId();
        Long itemId = request.getItemId();
        Integer num = request.getNum();
        String checked = request.getChecked();

        String key = "cart_" + userId;
        RBucket<String> bucket = redissonClient.getBucket(key);

        try {
            if (bucket.isExists()){
                String cartJson = bucket.get();
                List<CartProductDto> cartList = JSON.parseArray(cartJson, CartProductDto.class);

                // 遍历 list 找到 userId和 itemId 都一样的那个商品，修改num,checked
                for (CartProductDto cartProductDto : cartList) {
                    Long productId = cartProductDto.getProductId();

                    if (itemId.equals(productId)){
                        cartProductDto.setProductNum(num.longValue());
                        cartProductDto.setChecked(checked);
                        break;
                    }
                }
                // 更改redis里面的值
                bucket.set(JSON.toJSONString(cartList));

                response.setCode(ShoppingRetCode.SUCCESS.getCode());
                response.setMsg(ShoppingRetCode.SUCCESS.getMessage());

            }else {
                response.setCode(ShoppingRetCode.DB_EXCEPTION.getCode());
                response.setMsg(ShoppingRetCode.DB_EXCEPTION.getMessage());
            }
        } catch (Exception e) {
            log.error("ICartServiceImpl.updateCartNum Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;

    }

//    @Override
//    public CheckAllItemResponse checkAllCartItem(CheckAllItemRequest request) {
//
//        return null;
//    }

    @Override
    @Transactional
    public DeleteCartItemResponse deleteCartItem(DeleteCartItemRequest request) {

        DeleteCartItemResponse response = new DeleteCartItemResponse();

        try {
            request.requestCheck();
            String key = "cart_" + request.getUserId();

            RBucket<String> bucket = redissonClient.getBucket(key);
            if (bucket.isExists()){
                String str = bucket.get();
                List<CartProductDto> cartList = JSON.parseArray(str, CartProductDto.class);
                for (CartProductDto item : cartList) {
                    if (item.getProductId().equals(request.getItemId())){
                        cartList.remove(item);
                        break;
                    }
                }
                bucket.set(JSON.toJSONString(cartList));
                response.setCode(ShoppingRetCode.SUCCESS.getCode());
                response.setMsg(ShoppingRetCode.SUCCESS.getMessage());

            }
        } catch (Exception e) {
            log.error("An error occurs in ICartServiceImpl.deleteCartItem :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response,e);
        }
        return response;
    }

    @Override
    public DeleteCheckedItemResposne deleteCheckedItem(DeleteCheckedItemRequest request) {

        DeleteCheckedItemResposne response = new DeleteCheckedItemResposne();

        try {
            request.requestCheck();
            String key = "cart_" + request.getUserId();
            RBucket<String> bucket = redissonClient.getBucket(key);
            if (bucket.get() != null){
                String cartListJson = bucket.get();
                List<CartProductDto> cartList = JSON.parseArray(cartListJson, CartProductDto.class);
                for (CartProductDto temp : cartList) {
                    if(temp.getChecked().equals("true")){
                        cartList.remove(temp);
                        break;
                    }
                }
                bucket.set(JSON.toJSONString(cartList));
                response.setCode(ShoppingRetCode.SUCCESS.getCode());
                response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
            }
        } catch (Exception e) {
            log.error("ICartServiceImpl.deleteCheckedItem Occurs Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }
//
//    @Override
//    public ClearCartItemResponse clearCartItemByUserID(ClearCartItemRequest request) {
//        return null;
//    }
}
