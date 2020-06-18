package com.mall.order.biz.context;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @PackgeName: com.mall.order.biz.context
 * @ClassName: CreateSeckillOrderContext
 * @Author: Li Haiquan
 * Date: 2020/6/17 16:47
 * project name: cs-mall
 */
@Data
public class CreateSeckillOrderContext extends AbsTransHandlerContext{
    private Long userId;

    private BigDecimal seckillPrice;

    private Long psId;

    private Long productId;

    private Long addressId;

    private String tel;

    private String streetName;

    private String userName;

    private String uniqueKey;
}
