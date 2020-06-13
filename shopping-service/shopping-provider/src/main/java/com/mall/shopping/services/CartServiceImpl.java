package com.mall.shopping.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.tool.redisconfig.RedissonAutoConfiguration;
import com.mall.shopping.ICartService;
import com.mall.shopping.converter.CartItemConverter;
import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dto.*;
import com.mall.shopping.services.cache.CacheManager;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

@Component
@Service
public class CartServiceImpl implements ICartService {

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
        return null;
    }

    @Override
    public CheckAllItemResponse checkAllCartItem(CheckAllItemRequest request) {
        return null;
    }

    @Override
    public DeleteCartItemResponse deleteCartItem(DeleteCartItemRequest request) {
        return null;
    }

    @Override
    public DeleteCheckedItemResposne deleteCheckedItem(DeleteCheckedItemRequest request) {
        return null;
    }

    @Override
    public ClearCartItemResponse clearCartItemByUserID(ClearCartItemRequest request) {
        return null;
    }
}
