package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.IContentService;
import com.mall.shopping.IHomeService;
import com.mall.shopping.IProductCateService;
import com.mall.shopping.IProductService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.*;
import com.mall.user.annotation.Anoymous;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@Component
@RequestMapping("/shopping")
@Api(tags="HomepageController",description = "首页显示商品控制层")
public class HomepageController {

    //主页
    @Reference(check = false)
    IHomeService homeService;

    //导航栏
    @Reference(check = false)
    IContentService contentService;

    //商品类目
    @Reference(check = false)
    IProductCateService productCateService;

    //查看商品详情
    @Reference(check = false)
    IProductService productService;
    /**
     * 主页显示接口
     *查询板块表position=0，根据板块ID找panel_content对应得商品，再去查商品表
     * @return
     */
    @GetMapping("/homepage")
    @ApiOperation("主页")
    @Anoymous
    public ResponseData homePage(){
        HomePageResponse homepage = homeService.homepage();
        //成功
        if(homepage.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil<>().setData(homepage.getPanelContentItemDtos());
        }
        //失败
        return new ResponseUtil<>().setErrorMsg(homepage.getMsg());
    }

    /**
     * 导航栏显示
     * 查询表panel_content，panel_id=0都在导航栏显示
     * @return
     */
    @GetMapping("/navigation")
    @Anoymous
    @ApiOperation("导航栏显示")
    public ResponseData navigation(){
//        ResponseData<List<PanelContentDto>> responseData = new ResponseData<>();
        NavListResponse navListResponse = contentService.queryNavList();
//        responseData.setCode(200);
//        responseData.setMessage("success");
//        responseData.setSuccess(true);
//        responseData.setResult(navListResponse.getPannelContentDtos());
        //成功
        if(navListResponse.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil<>().setData(navListResponse.getPannelContentDtos());
        }
       //失败
        return new ResponseUtil<>().setErrorMsg(navListResponse.getMsg());
    }

    /**
     * 列举所有商品种类
     * 查询商品类目表item_cat并按照sort_order进行排序
     * @return
     */
     @GetMapping("/categories")
     @ApiOperation("列举商品种类")
     @Anoymous
     public ResponseData categories(){
         AllProductCateResponse allProductCate = productCateService.getAllProductCategory();
         if(allProductCate.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
             return new ResponseUtil<>().setData(allProductCate.getProductCateDtoList());
         }
         return new ResponseUtil<>().setErrorMsg(allProductCate.getMsg());
     }

    /**
     * 查看商品详情
     * 查询item表和
     * @return
     */
     @GetMapping("/product/{id}")
     @ApiOperation("查看商品详情")
     @Anoymous
     public ResponseData productDetail(@PathVariable(value = "id")Integer id){
         ProductDetailResponse productDetail = productService.getProductDetails(id);
         if(productDetail.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
             return new ResponseUtil<>().setData(productDetail.getProductDetailDto());
         }
         return new ResponseUtil<>().setErrorMsg(productDetail.getMsg());
    }
}
