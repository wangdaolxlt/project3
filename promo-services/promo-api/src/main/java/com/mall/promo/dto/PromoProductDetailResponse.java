package com.mall.promo.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

/**
 * @PackgeName: com.mall.promo.dto
 * @ClassName: PromoProductDetailResponse
 * @Author: Li Haiquan
 * Date: 2020/6/17 12:22
 * project name: cs-mall
 */
@Data
public class PromoProductDetailResponse extends AbstractResponse {
    private PromoProductDetailDTO promoProductDetailDTO;
}
