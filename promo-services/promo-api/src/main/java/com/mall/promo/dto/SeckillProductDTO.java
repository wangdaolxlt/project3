package com.mall.promo.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author lenovo
 * @PackgeName: SeckillProductDTO
 * @date: 2020/6/18
 * @Description:
 */
@Data
public class SeckillProductDTO implements Serializable {
    private static final long serialVersionUID = 2910066947973025313L;

    private Long id;

    private Integer inventory;

    private BigDecimal price;

    private BigDecimal seckillPrice;

    private String picUrl;

    private String productName;
}
