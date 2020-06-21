package com.mall.shopping.services;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mall.shopping.IProductService;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.converter.ProductConverter;
import com.mall.shopping.converter.RecommendConverter;
import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dal.entitys.Panel;
import com.mall.shopping.dal.entitys.PanelContent;
import com.mall.shopping.dal.entitys.PanelContentItem;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dal.persistence.PanelContentMapper;
import com.mall.shopping.dal.persistence.PanelMapper;

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

import java.math.BigDecimal;
import java.util.List;


/**
 * @PackgeName: com.mall.shopping.services
 * @ClassName: ProductServiceImpl
 * @Author: Pipboy
 * Date: 2020/6/11 15:03
 * project name: project3
 * @Version:
 * @Description:
 */

@Component
@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private PanelContentMapper panelContentMapper;
    @Autowired
    private ProductConverter productConverter;
    @Autowired
    private PanelMapper panelMapper;
    @Autowired
    private RecommendConverter recommendConverter;

    @Autowired
    ItemDescMapper itemDescMapper;
    @Autowired
    ProductDetailConverter productDetailConverter;

    @Autowired
    private ContentConverter contentConverter;

    /**
     * 获得所有条目
     *
     * @param request
     * @return
     */
    @Override
    public AllGoodsResponse getAllProduct(AllProductRequest request) {
        PageHelper.startPage(request.getPage(), request.getSize());
        String sort = request.getSort();
//        List<PanelContentItemDto> panelContentItems = panelContentMapper.selectAllItems();
        Example example = new Example(Item.class);
        example.createCriteria().andEqualTo("status",1)
                .andBetween("price", request.getPriceGt(), request.getPriceLte());//只显示上架商品
        if (!("".equals(sort))) {
            if ("1".equals(sort)) {
                example.setOrderByClause("price");
            } else if ("-1".equals(sort)) {
                example.setOrderByClause("price" + " desc");
            }
        }

        List<Item> items = itemMapper.selectByExample(example);
//        Long num = panelContentMapper.selectAllItemsNum();
        Long num = itemMapper.selectAllNum();

        List<ProductDto> dtoList = productConverter.items2Dto(items);
        AllGoodsResponse response = new AllGoodsResponse();

        response.setData(dtoList);
        response.setTotal(num);
        return response;
    }


    @Override
    public RecommendResponse getRecommendGoods() {
        List<PanelContentItem> panelContentItems = panelContentMapper.selectPanelContentAndProductWithPanelId(6);
        List<PanelContentItemDto> panelContentItemDtos = contentConverter.panelContentItem2Dto(panelContentItems);
        List<Panel> panels = panelMapper.selectPanelContentById(6);
        List<PanelDto> dtoList = recommendConverter.panels2Dto(panels);
        dtoList.get(0).setPanelContentItems(panelContentItemDtos);
        RecommendResponse response = new RecommendResponse();
        response.setPanelContentItemDtos(dtoList);
        return response;
    }

    //获取商品详情
    @Override
    public ProductDetailResponse getProductDetails(Long id) {
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        Example example = new Example(Item.class);
        example.createCriteria().andEqualTo("id", id);
        List<Item> items = itemMapper.selectByExample(example);
        ItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(items.get(0).getId());
        if (CollectionUtils.isEmpty(items) || itemDesc == null) {
            productDetailResponse.setCode(ShoppingRetCode.DB_EXCEPTION.getCode());
            productDetailResponse.setMsg(ShoppingRetCode.DB_EXCEPTION.getMessage());
        }
        ProductDetailDto productDetailDto = productDetailConverter.convert2DetailDto(items.get(0));
        productDetailDto.setProductImageSmall(items.get(0).getImages());
        productDetailDto.setDetail(itemDesc.getItemDesc());
        productDetailResponse.setProductDetailDto(productDetailDto);
        productDetailResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
        productDetailResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return productDetailResponse;
    }

}

