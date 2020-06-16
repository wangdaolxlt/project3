package com.mall.promo.service;

import com.mall.order.OrderCoreService;
import com.mall.promo.PromoService;
import com.mall.promo.dal.persistence.PromoItemMapper;
import com.mall.promo.dto.SeckillListRequest;
import com.mall.promo.dto.SeckillListResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @PackgeName: com.mall.promo.service
 * @ClassName: PromoServiceImpl
 * @Author: Li Haiquan
 * Date: 2020/6/16 21:33
 * project name: cs-mall
 */
@Service
@Component
@Slf4j
public class PromoServiceImpl implements PromoService{
    @Autowired
    private PromoItemMapper promoItemMapper;
    @Autowired
    private OrderCoreService orderCoreService;

    /**
     * 获取秒杀商品列表接口
     * @param seckillListRequest
     * @return
     */
    @Override
    public SeckillListResponse getSeckillList(SeckillListRequest seckillListRequest) {
        return null;
    }
}
