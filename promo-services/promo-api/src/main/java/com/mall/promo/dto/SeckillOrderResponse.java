package com.mall.promo.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;


/**
 * @PackgeName: com.mall.promo.dto
 * @ClassName: SeckillOrderResponse
 * @Author: Li Haiquan
 * Date: 2020/6/17 15:38
 * project name: cs-mall
 */
@Data
public class SeckillOrderResponse extends AbstractResponse {

    private Long productId;

    private Integer inventory;

}
