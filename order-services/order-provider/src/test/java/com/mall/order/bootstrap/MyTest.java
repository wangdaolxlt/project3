package com.mall.order.bootstrap;

import com.mall.order.OrderCoreService;
import com.mall.order.dto.CartProductDto;
import com.mall.order.dto.CreateOrderRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @PackgeName: com.mall.order.bootstrap
 * @ClassName: MyTest
 * @Author: Li Haiquan
 * Date: 2020/6/12 1:22
 * project name: cs-mall
 */
public class MyTest extends OrderProviderApplicationTests{
    @Autowired
    OrderCoreService orderCoreService;

    @Test
    public void myTest01(){
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setTel("18782059038");
        request.setUserName("cskaoyan01");
        request.setUniqueKey(UUID.randomUUID().toString());
        request.setStreetName("上海青浦区汇联路");
        request.setOrderTotal(new BigDecimal(149));
        request.setAddressId(5L);
        List<CartProductDto> cartProductDtoList = new ArrayList<>();
        CartProductDto cartProductDto = new CartProductDto();
        cartProductDto.setChecked("true");
        cartProductDto.setLimitNum(100L);
        cartProductDto.setProductId(100057401L);
        cartProductDto.setSalePrice(new BigDecimal(149));
        cartProductDto.setProductNum(1L);
        cartProductDto.setProductImg("https://resource.smartisan.com/resource/005c65324724692f7c9ba2fc7738db13.png");
        cartProductDto.setProductName("Smartisan T恤 迪特拉姆斯");
        cartProductDtoList.add(cartProductDto);
        orderCoreService.createOrder(request);
    }
}

