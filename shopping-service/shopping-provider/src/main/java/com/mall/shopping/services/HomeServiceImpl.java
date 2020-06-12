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
    PanelMapper panelMapper;

    @Autowired
    PanelContentMapper panelContentMapper;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    ContentConverter contentConverter;

    @Autowired
    ProductConverter productConverter;

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
//            panelDto.setPanelContentItems();
            Example example1 = new Example(PanelContent.class);
            example1.createCriteria().andEqualTo("panelId",panel.getId());
            List<PanelContent> panelContents = panelContentMapper.selectByExample(example1);
//            List<PanelContentDto> panelContentDtos = contentConverter.panelContents2Dto(panelContents);//不用转换
            //先获取PanelContentItem
            List<PanelContentItemDto> panelContentItemDtos = new ArrayList<>();
            for (PanelContent panelContent : panelContents) {
                PanelContentItemDto panelContentItemDto = new PanelContentItemDto();
//                Example example2 = new Example(Item.class);
//                example2.createCriteria().andEqualTo("id",panelContentDto.getProductId());
//                List<Item> items = itemMapper.selectByExample(example2);
                //TODO 注意连接item表的时候，如果productId=null,item返回null
                ProductDto productDto = null;
                if(panelContent.getProductId() != null){
                    Item item = itemMapper.selectByPrimaryKey(panelContent.getProductId());
                    //productName  salePrice subTitle 只要这三个字段
                    productDto = productConverter.item2Dto(item);
                }
                productDto= new ProductDto(null,null,null,null,null);
//                System.out.println("**************************************");
//                System.out.println(productDto);
                panelContentItemDto.setProductName(productDto.getProductName());
                panelContentItemDto.setSalePrice(productDto.getSalePrice());
                panelContentItemDto.setSubTitle(productDto.getSubTitle());
                panelContentItemDto.setId(panelContent.getId());
                panelContentItemDto.setPanelId(panelContent.getPanelId());
                panelContentItemDto.setType(panelContent.getType());
                panelContentItemDto.setProductId(panelContent.getProductId());
                panelContentItemDto.setSortOrder(panelContent.getSortOrder());
                panelContentItemDto.setFullUrl(panelContent.getFullUrl());
                panelContentItemDto.setPicUrl(panelContent.getPicUrl());
                panelContentItemDto.setPicUrl2(panelContent.getPicUrl2());
                panelContentItemDto.setPicUrl3(panelContent.getPicUrl3());
                panelContentItemDto.setCreated(panelContent.getCreated());
                panelContentItemDto.setUpdated(panelContent.getUpdated());
                panelContentItemDtos.add(panelContentItemDto);
//                System.out.println(panelContentItemDto);
//                System.out.println("**************************************");
            }
            panelDto.setPanelContentItems(panelContentItemDtos);
            set.add(panelDto);
        }
        if(CollectionUtils.isEmpty(set)){
            homePageResponse.setCode(ShoppingRetCode.DB_EXCEPTION.getCode());
            homePageResponse.setMsg(ShoppingRetCode.DB_EXCEPTION.getMessage());;
        }
        homePageResponse.setPanelContentItemDtos(set);
        return  homePageResponse;
    }
}
