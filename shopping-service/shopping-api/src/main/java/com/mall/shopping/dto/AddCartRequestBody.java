package com.mall.shopping.dto;

import com.mall.commons.result.AbstractRequest;

/**
 * @author lenovo
 * @PackgeName: AddCartRequestBody
 * @date: 2020/6/13
 * @Description:
 */
public class AddCartRequestBody extends AbstractRequest {
    Long userId;
    Long productId;
    Integer productNum;

    @Override
    public void requestCheck() {

    }
}
