package com.mall.shopping.dto;

import com.mall.commons.result.AbstractRequest;
import lombok.Data;

/**
 * @author lenovo
 * @PackgeName: AddCartRequestBody
 * @date: 2020/6/13
 * @Description:
 */
@Data
public class AddCartRequestBody extends AbstractRequest {
    Long userId;
    Long productId;
    Integer productNum;

    @Override
    public void requestCheck() {

    }
}
