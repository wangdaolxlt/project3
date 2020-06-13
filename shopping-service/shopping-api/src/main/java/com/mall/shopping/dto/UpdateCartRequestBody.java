package com.mall.shopping.dto;

import lombok.Data;

/**
 * @author lenovo
 * @PackgeName: UpdateCartRequestBody
 * @date: 2020/6/13
 * @Description:
 */
@Data
public class UpdateCartRequestBody {
    Long userId;
    Long productId;
    Integer productNum;
    String checked;
}
