package com.mall.shopping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *  ciggar
 * create-date: 2019/7/24-19:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto implements Serializable {

    private static final long serialVersionUID = 2763986506997467400L;
    private Long productId;

    private BigDecimal salePrice;

    private String productName;

    private String subTitle;

    private String picUrl;
}
