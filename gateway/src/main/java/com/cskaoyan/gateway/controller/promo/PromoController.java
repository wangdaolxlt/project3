package com.cskaoyan.gateway.controller.promo;

import com.mall.commons.result.ResponseData;

import com.mall.commons.result.ResponseUtil;
import com.mall.promo.PromoService;
import com.mall.promo.dto.SeckillListRequest;
import com.mall.promo.dto.SeckillListResponse;
import com.mall.user.constants.SysRetCodeConstants;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author lenovo
 * @PackgeName: PromoController
 * @date: 2020/6/18
 * @Description:
 */
@RestController
@RequestMapping("/shopping")
public class PromoController {

    @Reference(check = false)
    private PromoService promoService;


    @RequestMapping("/seckilllist")
    public ResponseData getSeckillList(@RequestParam Integer sessionId){
        String yyyyMMdd = DateFormatUtils.format(new Date(), "yyyyMMdd");

        SeckillListRequest seckillListRequest = new SeckillListRequest();
        seckillListRequest.setSessionId(sessionId);
        seckillListRequest.setYyyymmdd(yyyyMMdd);
        SeckillListResponse seckillListResponse = promoService.getSeckillList(seckillListRequest);
        if ( SysRetCodeConstants.SUCCESS.getCode() != seckillListResponse.getCode()){
            return new ResponseUtil().setErrorMsg(seckillListResponse.getMsg());
        }

        return new ResponseUtil().setData(seckillListResponse);

    }

    public static void main(String[] args) {
        String yyyyMMdd = DateFormatUtils.format(new Date(), "yyyyMMdd");

        System.out.println(yyyyMMdd);
    }
}
