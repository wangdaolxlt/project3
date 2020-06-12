package com.mall.order.dto;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @PackgeName: com.mall.order.dto
 * @ClassName: OrderDetailRespVo
 * @Author: Li Haiquan
 * Date: 2020/6/12 15:13
 * project name: cs-mall
 */
@Data
public class OrderDetailRespVo implements Serializable {

    private static final long serialVersionUID = -2104347789074795876L;

    private String userName;

    private BigDecimal orderTotal;

    private Long userId;

    private List<OrderItemDto> goodsList;

    private String tel;

    private String streetName;

}
