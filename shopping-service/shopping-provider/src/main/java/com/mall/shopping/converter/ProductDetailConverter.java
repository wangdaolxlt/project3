package com.mall.shopping.converter;

import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dto.ProductDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProductDetailConverter {
    @Mappings({
            @Mapping(source = "id",target = "productId"),
            @Mapping(source = "title",target = "productName"),
            @Mapping(source = "price",target = "salePrice"),
            @Mapping(source = "sellPoint",target = "subTitle"),
            @Mapping(source = "imageBig",target = "productImageBig"),
            @Mapping(source = "limitNum",target = "limitNum")
    })
    ProductDetailDto convert2DetailDto(Item item);
}
