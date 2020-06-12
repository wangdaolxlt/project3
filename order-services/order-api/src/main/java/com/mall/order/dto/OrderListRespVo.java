package com.mall.order.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @PackgeName: com.mall.order.dto
 * @ClassName: OrderListRespVo
 * @Author: Li Haiquan
 * Date: 2020/6/12 15:17
 * project name: cs-mall
 */
public class OrderListRespVo implements Serializable {
    private static final long serialVersionUID = -1964699516197874847L;
    private List<OrderDetailInfo> detailInfoList;

    /**
     * 总记录数
     */
    private Long total;
}
