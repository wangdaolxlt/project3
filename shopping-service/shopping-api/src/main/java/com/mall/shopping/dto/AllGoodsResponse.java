package com.mall.shopping.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @PackgeName: com.mall.shopping.dto
 * @ClassName: AllGoodsResponse
 * @Author: Pipboy
 * Date: 2020/6/13 15:58
 * project name: project3
 * @Version:
 * @Description:
 */
@Data
public class AllGoodsResponse implements Serializable {
    private static final long serialVersionUID = 3177307546199722228L;

    private List<ProductDto> data;

    private Long total;
}
