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
import com.mall.shopping.dto.*;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Override
    public ProductDetailResponse getProductDetail(ProductDetailRequest request) {
        return null;
    }

    /**
     * 获得所有条目
     * @param request
     * @return
     */
    @Override
    public AllProductResponse getAllProduct(AllProductRequest request) {
        PageHelper.startPage(request.getPage(), request.getSize());
//        List<PanelContentItemDto> panelContentItems = panelContentMapper.selectAllItems();
        List<Item> items = itemMapper.selectAll();
//        Long num = panelContentMapper.selectAllItemsNum();
        Long num = itemMapper.selectAllNum();

        List<ProductDto> dtoList = productConverter.items2Dto(items);
        AllProductResponse response = new AllProductResponse();

        response.setProductDtoList(dtoList);
        response.setTotal(num);
        return response;
    }


    @Override
    public RecommendResponse getRecommendGoods() {
        List<PanelContentItemDto> panelContentItems = panelContentMapper.selectPanelContentAndProductWithPanelId(6);
        List<Panel> panels = panelMapper.selectPanelContentById(6);
        List<PanelDto> dtoList = recommendConverter.panels2Dto(panels);
        dtoList.get(0).setPanelContentItems(panelContentItems);
        RecommendResponse response = new RecommendResponse();
        response.setPanelContentItemDtos(dtoList);
        return response;
    }
}
