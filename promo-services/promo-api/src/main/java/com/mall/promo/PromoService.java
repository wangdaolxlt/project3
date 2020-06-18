package com.mall.promo;

import com.mall.promo.dto.SeckillListRequest;
import com.mall.promo.dto.SeckillListResponse;

/**
 * @author lenovo
 * @PackgeName: PromoService
 * @date: 2020/6/18
 * @Description:
 */
public interface PromoService {
    SeckillListResponse getSeckillList(SeckillListRequest request);
}
