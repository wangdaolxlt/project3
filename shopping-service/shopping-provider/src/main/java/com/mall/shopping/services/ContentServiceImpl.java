package com.mall.shopping.services;

import com.mall.shopping.IContentService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.dal.entitys.PanelContent;
import com.mall.shopping.dal.persistence.PanelContentMapper;
import com.mall.shopping.dto.NavListResponse;
import com.mall.shopping.dto.PanelContentDto;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
@Component
public class ContentServiceImpl implements IContentService {

    @Autowired
    private PanelContentMapper panelContentMapper;

    @Autowired
    ContentConverter contentConverter;

    //导航栏
    @Override
    public NavListResponse queryNavList() {
        NavListResponse navListResponse = new NavListResponse();
        Example example = new Example(PanelContent.class);
        example.createCriteria().andEqualTo("panelId", 0);
        List<PanelContent> panelContents = panelContentMapper.selectByExample(example);
        //要进行转换
        List<PanelContentDto> panelContentDtos = contentConverter.panelContents2Dto(panelContents);
        navListResponse.setPannelContentDtos(panelContentDtos);
        if(CollectionUtils.isEmpty(panelContentDtos)){
            navListResponse.setCode(ShoppingRetCode.DB_EXCEPTION.getCode());
            navListResponse.setMsg(ShoppingRetCode.DB_EXCEPTION.getMessage());
        }
        navListResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
        navListResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        navListResponse.setPannelContentDtos(panelContentDtos);
        return navListResponse;
    }
}
