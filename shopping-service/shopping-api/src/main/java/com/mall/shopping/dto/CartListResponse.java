package com.mall.shopping.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


/**
 * @PackgeName: com.mall.shopping.dto
 * @ClassName: CartListResponse
 * @Author: Pipboy
 * Date: 2020/6/13 20:28
 * project name: project3
 * @Version:
 * @Description:
 */
@Data
public class CartListResponse implements Serializable {
    private static final long serialVersionUID = -3439250524153183052L;

    private List<CartProductDto> result;
}
