package com.mall.promo.dto;

import com.mall.commons.result.AbstractRequest;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @PackgeName: com.mall.promo.dto
 * @ClassName: SeckillOrderRequest
 * @Author: Li Haiquan
 * Date: 2020/6/17 15:36
 * project name: cs-mall
 */
@Data
public class SeckillOrderRequest extends AbstractRequest {

    /**
     * psId				Long							秒杀场次主键id
     * productId 	Long							商品id
     * addressId 	Long							地址id
     * tel       	String						手机号码
     * streetName 	String						地址
     * userName  	String						用户名
     */
    private Long userId;

    private BigDecimal seckillPrice;

    private Long psId;

    private Long productId;

    private Long addressId;

    private String tel;

    private String streetName;

    private String userName;

    private String uniqueKey;
    @Override
    public void requestCheck() {

    }
}
