package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.shopping.IProductService;
import com.mall.shopping.dto.RecommendResponse;
import com.mall.user.annotation.Anoymous;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @PackgeName: com.cskaoyan.gateway.controller.shopping
 * @ClassName: RecommendController
 * @Author: Pipboy
 * Date: 2020/6/11 21:54
 * project name: project3
 * @Version:
 * @Description:
 */

@Anoymous
@RestController
@RequestMapping("shopping")
public class RecommendController {

    @Reference(timeout = 3000,check = true)
    private IProductService productService;

    @GetMapping("recommend")
    public ResponseData recommend() {
        RecommendResponse recommendGoods = productService.getRecommendGoods();
        ResponseData<Object> data = new ResponseData<>();
        data.setCode(200);
        data.setMessage("success");
        data.setResult(recommendGoods);
        data.setSuccess(true);
        data.setTimestamp(System.currentTimeMillis());
        return data;
    }
}
