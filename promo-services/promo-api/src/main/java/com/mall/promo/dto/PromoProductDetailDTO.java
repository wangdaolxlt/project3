package com.mall.promo.dto;

import com.mall.shopping.dto.ProductDetailDto;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @PackgeName: com.mall.promo.dto
 * @ClassName: PromoProductDetailDTO
 * @Author: Li Haiquan
 * Date: 2020/6/17 14:07
 * project name: cs-mall
 */
@Data
public class PromoProductDetailDTO implements Serializable {

    private static final long serialVersionUID = -228392123369014230L;

    private Long productId;

    private BigDecimal salePrice;

    private String productName;

    private String subTitle;

    private Long limitNum;

    private String productImageBig;

    private String detail;

    private List<String> productImageSmall;

    private BigDecimal promoPrice;
}
