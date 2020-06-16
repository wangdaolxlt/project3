package com.mall.promo.dal.entitys;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: jia.xue
 * @Email: xuejia@cskaoyan.onaliyun.com
 * @Description
 **/

@Table(name = "tb_promo_item")
@Data
public class PromoItem implements Serializable {
    private static final long serialVersionUID = 4584482481285066371L;

    /**
     *   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
     *   `ps_id` int(11) NOT NULL COMMENT '秒杀场次id',
     *   `item_id` int(11) NOT NULL COMMENT '商品id',
     *   `seckill_price` decimal(10,2) NOT NULL COMMENT '商品秒杀价格',
     *   `item_stock` int(11) NOT NULL COMMENT '商品秒杀库存',
     */

    @Id
    private Integer id;

    @Column
    private Integer psId;

    @Column
    private Integer itemId;

    @Column
    private BigDecimal seckillPrice;

    @Column
    private Integer itemStock;


}