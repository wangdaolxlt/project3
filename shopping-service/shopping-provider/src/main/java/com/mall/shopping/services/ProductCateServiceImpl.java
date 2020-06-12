package com.mall.shopping.services;

import com.mall.shopping.IProductCateService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.ProductCateConverter;
import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dal.entitys.ItemCat;
import com.mall.shopping.dal.persistence.ItemCatMapper;
import com.mall.shopping.dto.*;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
@Component
public class ProductCateServiceImpl implements IProductCateService {
    @Autowired
    ItemCatMapper itemCatMapper;

    @Autowired
    ProductCateConverter productCateConverter;

    @Override
    public AllProductCateResponse getAllProductCate(AllProductCateRequest request) {
        return null;
    }

    @Override
    public AllProductCateResponse getAllProductCategory() {
        AllProductCateResponse allProductCateResponse = new AllProductCateResponse();
        Example example = new Example(Item.class);
        example.setOrderByClause("sort_order");//升序排列
        List<ItemCat> itemCats = itemCatMapper.selectByExample(example);
        List<ProductCateDto> productCateDtos = productCateConverter.items2Dto(itemCats);
        allProductCateResponse.setProductCateDtoList(productCateDtos);
        if(CollectionUtils.isEmpty(productCateDtos)) {
            allProductCateResponse.setCode(ShoppingRetCode.DB_EXCEPTION.getCode());
            allProductCateResponse.setMsg(ShoppingRetCode.DB_EXCEPTION.getMessage());
        }
        allProductCateResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
        allProductCateResponse.setCode(ShoppingRetCode.SUCCESS.getMessage());
        return allProductCateResponse;
    }
}
