package com.mall.promo.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SeckillProductDTO implements Serializable {

    private static final long serialVersionUID = -1586640602479158109L;

    private Long id;

    private Integer inventory;

    private BigDecimal price;

    private BigDecimal seckillPrice;

    private String picUrl;

    private String productName;


}
