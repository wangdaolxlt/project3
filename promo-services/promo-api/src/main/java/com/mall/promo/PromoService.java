package com.mall.promo;

import com.mall.promo.dto.SeckillListRequest;
import com.mall.promo.dto.SeckillListResponse;

/**
 * @PackgeName: com.mall.promo
 * @ClassName: PromoService
 * @Author: Li Haiquan
 * Date: 2020/6/16 21:23
 * project name: cs-mall
 */
public interface PromoService {

    SeckillListResponse getSeckillList(SeckillListRequest seckillListRequest);
}
