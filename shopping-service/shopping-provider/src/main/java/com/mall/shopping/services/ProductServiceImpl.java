package com.mall.shopping.services;

import com.mall.shopping.IProductService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.ProductConverter;
import com.mall.shopping.converter.ProductDetailConverter;
import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dal.entitys.ItemDesc;
import com.mall.shopping.dal.persistence.ItemDescMapper;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dto.*;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
@Component
public class ProductServiceImpl implements IProductService {

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    ItemDescMapper itemDescMapper;

    @Autowired
    ProductDetailConverter productDetailConverter;

    @Override
    public ProductDetailResponse getProductDetail(ProductDetailRequest request) {
        return null;
    }

    //获取商品详情
    @Override
    public ProductDetailResponse getProductDetails(Integer id) {
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        Example example = new Example(Item.class);
        example.createCriteria().andEqualTo("id",id);
        List<Item> items = itemMapper.selectByExample(example);
        ItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(items.get(0).getId());
        if(CollectionUtils.isEmpty(items) || itemDesc == null){
            productDetailResponse.setCode(ShoppingRetCode.DB_EXCEPTION.getCode());
            productDetailResponse.setMsg(ShoppingRetCode.DB_EXCEPTION.getMessage());
        }
        ProductDetailDto productDetailDto = productDetailConverter.convert2DetailDto(items.get(0));
        productDetailDto.setProductImageSmall(items.get(0).getImages());
        productDetailDto.setDetail(itemDesc.getItemDesc());
        productDetailResponse.setProductDetailDto(productDetailDto);
        return productDetailResponse;
    }

    @Override
    public AllProductResponse getAllProduct(AllProductRequest request) {
        return null;
    }

    @Override
    public RecommendResponse getRecommendGoods() {
        return null;
    }
}
