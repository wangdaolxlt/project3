package com.mall.promo.dal.entitys;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author: jia.xue
 * @Email: xuejia@cskaoyan.onaliyun.com
 * @Description
 **/
@Table(name = "tb_promo_session")
@Data
public class PromoSession implements Serializable {

    /**
     * `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
     *   `session_id` int(4) NOT NULL COMMENT '场次 id  1:上午十点场 2:下午四点场',
     *   `start_time` datetime NOT NULL COMMENT '开始时间',
     *   `end_time` datetime NOT NULL COMMENT '结束时间',
     *   `yyyymmdd` varchar(255) NOT NULL COMMENT '场次日期',
     *   PRIMARY KEY (`id`)
     */

    @Id
    private Integer id;
    @Column
    private Integer sessionId;
    @Column
    private Date startTime;
    @Column
    private Date endTime;

    @Column
    private String yyyymmdd;

}