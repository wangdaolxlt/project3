package com.mall.shopping.bootstrap;

import com.mall.shopping.IContentService;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.dto.NavListResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HomePageTest extends ShoppingProviderApplicationTests{

    @Autowired
    private IContentService contentService;

    @Test
    public void test(){
        NavListResponse navListResponse = contentService.queryNavList();
        System.out.println(navListResponse);
    }

}
