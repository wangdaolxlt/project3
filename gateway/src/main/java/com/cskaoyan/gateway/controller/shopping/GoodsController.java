package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.shopping.IProductService;
import com.mall.shopping.dto.AllProductRequest;
import com.mall.shopping.dto.AllProductResponse;
import com.mall.user.annotation.Anoymous;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @PackgeName: com.cskaoyan.gateway.controller.shopping
 * @ClassName: GoodsController
 * @Author: Pipboy
 * Date: 2020/6/11 12:50
 * project name: project3
 * @Version:
 * @Description:
 */
@Anoymous
@RestController
@RequestMapping("shopping")
@Api(tags = "GoodsController",description = "分页显示商品总数")
public class GoodsController {

    @Reference(timeout = 3000,check = false)
    IProductService productService;

    /**
     * 通过不同的参数显示商品总数
     * @param page
     * @param size
     * @param sort
     * @param priceGt
     * @param priceLte
     * @param cid
     * @return
     */
    @GetMapping("goods")
    public ResponseData allGoods(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam(value = "sort", defaultValue = "") String sort,
            @RequestParam(value = "priceGt", defaultValue = "") Integer priceGt,
            @RequestParam(value = "priceLte", defaultValue = "") Integer priceLte,
            @RequestParam(value = "cid",required = false) Long cid
    ) {
        AllProductRequest allProductRequest = new AllProductRequest();
        allProductRequest.setPage(page);
        allProductRequest.setSize(size);
        allProductRequest.setSort(sort);
        allProductRequest.setPriceGt(priceGt);
        allProductRequest.setPriceLte(priceLte);
        allProductRequest.setCid(cid);

        AllProductResponse allProduct = productService.getAllProduct(allProductRequest);

        ResponseData<Object> data = new ResponseData<>();
        data.setCode(200);
        data.setMessage("success");
        data.setResult(allProduct);
        data.setSuccess(true);
        data.setTimestamp(System.currentTimeMillis());
        return data;
    }

}
