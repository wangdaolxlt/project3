package com.mall.promo;

import com.mall.promo.dto.*;

/**
 * @PackgeName: com.mall.promo
 * @ClassName: PromoService
 * @Author: Li Haiquan
 * Date: 2020/6/16 21:23
 * project name: cs-mall
 */
public interface PromoService {

    SeckillListResponse getSeckillList(SeckillListRequest seckillListRequest);

    PromoProductDetailResponse getPromoProductDetail(PromoProductDetailRequest request);

    SeckillOrderResponse createSeckillOrder(SeckillOrderRequest request);
    /**
     * 事务型秒杀下单
     * @param request
     * @return
     */
    SeckillOrderResponse createSeckillOrderInTransaction(SeckillOrderRequest request);
}
