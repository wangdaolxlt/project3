package com.mall.promo.service;

import com.mall.order.OrderCoreService;
import com.mall.promo.PromoService;
import com.mall.promo.dal.persistence.PromoItemMapper;
import com.mall.promo.dto.SeckillListRequest;
import com.mall.promo.dto.SeckillListResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lenovo
 * @PackgeName: PromoServiceImpl
 * @date: 2020/6/18
 * @Description:
 */

@Service
@Component
@Slf4j
public class PromoServiceImpl implements PromoService {
    @Autowired
    private PromoItemMapper promoItemMapper;

    @Reference
    private OrderCoreService orderCoreService;
    @Override
    public SeckillListResponse getSeckillList(SeckillListRequest request) {
        return null;
    }
}
