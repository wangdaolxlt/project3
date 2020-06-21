package com.cskaoyan.gateway.controller.promo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.order.OrderCoreService;
import com.mall.promo.PromoService;
import com.mall.promo.dto.*;
import com.mall.user.annotation.Anoymous;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.intercepter.TokenIntercepter;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @PackgeName: com.cskaoyan.gateway.controller.promo
 * @ClassName: PromoController
 * @Author: Li Haiquan
 * Date: 2020/6/16 23:27
 * project name: cs-mall
 */
@RestController
@RequestMapping("/shopping")
public class PromoController {
    @Reference(timeout = 3000, check = false)
    private PromoService promoService;

    private RateLimiter rateLimiter;

    //声明一个线程池
    private ExecutorService executorService;

    @PostConstruct
    public void init(){
        // 初始化工具类 表示每秒创建100个令牌 变形的令牌桶
        rateLimiter = RateLimiter.create(100);

        // 创建一个固定大小的线程池  new LinkedBlockingQueue<Runnable>()  无界的等待队列
        executorService = Executors.newFixedThreadPool(100);
        // 创建一个单线程的线程池
//      executorService = Executors.newSingleThreadExecutor();
        // 创建一个缓存线程池
//      executorService = Executors.newCachedThreadPool();
        //创建一个执行执行定期任务的线程池
//      executorService = Executors.newScheduledThreadPool();

    }


    @RequestMapping("/seckilllist")
    @Anoymous
    public ResponseData getSeckillList(@RequestParam Integer sessionId){

        String yyyyMMdd = DateFormatUtils.format(new Date(), "yyyyMMdd");

        SeckillListRequest seckillListRequest = new SeckillListRequest();
        seckillListRequest.setSessionId(sessionId);
        seckillListRequest.setYyyymmdd(yyyyMMdd);
        SeckillListResponse seckillListResponse = promoService.getSeckillList(seckillListRequest);

        if(! SysRetCodeConstants.SUCCESS.getCode().equals(seckillListResponse.getCode())) {
            return new ResponseUtil().setErrorMsg(seckillListResponse.getMsg());
        }
        return new ResponseUtil().setData(seckillListResponse);
    }

    @RequestMapping("/promoProductDetail")
    public ResponseData getPromoProductDetail(@RequestBody PromoProductDetailRequest request){
        request.requestCheck();

        PromoProductDetailResponse promoProductDetailResponse = promoService.getPromoProductDetail(request);
        if(! SysRetCodeConstants.SUCCESS.getCode().equals(promoProductDetailResponse.getCode())) {
            return new ResponseUtil().setErrorMsg(promoProductDetailResponse.getMsg());
        }
        return new ResponseUtil().setData(promoProductDetailResponse);
    }

    @RequestMapping("/seckill")
    public ResponseData createSeckillOrder(@RequestBody SeckillOrderRequest request, HttpServletRequest servletRequest) throws ExecutionException, InterruptedException {

        double waitTimes = rateLimiter.acquire();

        String userInfo = (String) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject object = JSON.parseObject(userInfo);
        long uid = Long.parseLong(object.get("uid").toString());
        request.setUserId(uid);
        request.setUniqueKey(UUID.randomUUID().toString());

        //把发给下游请求的任务交给线程池来执行
        Future<SeckillOrderResponse> future = executorService.submit(new Callable<SeckillOrderResponse>() {
            @Override
            public SeckillOrderResponse call() throws Exception {
                SeckillOrderResponse response = promoService.createSeckillOrderInTransaction(request);
                return response;
            }
        });

        SeckillOrderResponse response = future.get();

        if(! SysRetCodeConstants.SUCCESS.getCode().equals(response.getCode())) {
            return new ResponseUtil().setErrorMsg(response.getMsg());
        }
        return new ResponseUtil().setData(response);
    }
}
