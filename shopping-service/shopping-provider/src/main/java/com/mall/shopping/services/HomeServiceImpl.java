package com.mall.shopping.services;

import com.mall.shopping.IHomeService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.converter.ProductConverter;
import com.mall.shopping.dal.entitys.*;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dal.persistence.PanelContentMapper;
import com.mall.shopping.dal.persistence.PanelMapper;
import com.mall.shopping.dto.*;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
@Component
public class HomeServiceImpl implements IHomeService {

    @Autowired
    private PanelMapper panelMapper;

    @Autowired
    private PanelContentMapper panelContentMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ContentConverter contentConverter;

    @Autowired
    private ProductConverter productConverter;

    //panel-->panel_content-->item
    //TODO 活动板块中productName为null,会报空指针异常，因此要先做判断
    @Override
    public HomePageResponse homepage() {
        HomePageResponse homePageResponse = new HomePageResponse();
        Set<PanelDto> set = new HashSet<>();
        Example example = new Example(Panel.class);
        example.createCriteria().andEqualTo("position",0);
        List<Panel> panels = panelMapper.selectByExample(example);
        for (Panel panel : panels) {
            PanelDto panelDto = contentConverter.panen2Dto(panel);
            List<PanelContentItem> panelContentItems = panelContentMapper.selectPanelContentAndProductWithPanelId(panel.getId());
            List<PanelContentItemDto> panelContentItemDtos = contentConverter.panelContentItem2Dto(panelContentItems);
            for (PanelContentItemDto panelContentItemDto : panelContentItemDtos) {
                ProductDto productDto = null;
                Item item = null;
                //TODO 注意连接item表的时候，如果productId=null,item返回null
                if(panelContentItemDto.getProductId() != null){
                    item = itemMapper.selectByPrimaryKey(panelContentItemDto.getProductId());
//                    System.out.println(item);
                    //productName  salePrice subTitle 只要这三个字段
                    productDto = productConverter.item2Dto(item);
                }else {
                    productDto = new ProductDto(null, null, null, null, null);
                }
                panelContentItemDto.setProductName(productDto.getProductName());
                panelContentItemDto.setSalePrice(productDto.getSalePrice());
                panelContentItemDto.setSubTitle(productDto.getSubTitle());
            }
            panelDto.setPanelContentItems(panelContentItemDtos);
            set.add(panelDto);
        }
        if(CollectionUtils.isEmpty(set)){
            homePageResponse.setCode(ShoppingRetCode.DB_EXCEPTION.getCode());
            homePageResponse.setMsg(ShoppingRetCode.DB_EXCEPTION.getMessage());
        }
        homePageResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
        homePageResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        homePageResponse.setPanelContentItemDtos(set);
        return  homePageResponse;
    }
}
