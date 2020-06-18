package com.mall.promo.converter;

import com.mall.promo.dto.PromoProductDetailDTO;
import com.mall.shopping.dto.ProductDetailDto;
import org.mapstruct.Mapper;

/**
 * @PackgeName: com.mall.promo.converter
 * @ClassName: PromoConverter
 * @Author: Li Haiquan
 * Date: 2020/6/17 14:53
 * project name: cs-mall
 */
@Mapper(componentModel = "spring")
public interface PromoConverter {
    PromoProductDetailDTO shopping2promo(ProductDetailDto productDetailDto);
}
