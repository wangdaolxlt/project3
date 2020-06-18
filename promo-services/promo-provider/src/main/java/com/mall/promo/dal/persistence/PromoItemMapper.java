package com.mall.promo.dal.persistence;

import com.mall.commons.tool.tkmapper.TkMapper;
import com.mall.promo.dal.entitys.PromoItem;
import org.apache.ibatis.annotations.Param;

public interface PromoItemMapper extends TkMapper<PromoItem> {

    void updatePromoItemStock(@Param("id") Integer id);

    PromoItem findPromoItemStockForUpdate(@Param("psId") Long psId, @Param("itemId") Long itemId);

    Integer decreaseStock(@Param(value = "productId") Long productId, @Param(value = "psId") Long psId);
}
