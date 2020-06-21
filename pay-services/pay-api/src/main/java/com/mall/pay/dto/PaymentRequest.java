package com.mall.pay.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @PackgeName: com.mall.pay.dto
 * @ClassName: PaymentRequest
 * @Author: Li Haiquan
 * Date: 2020/6/20 22:46
 * project name: cs-mall
 */
@Data
public class PaymentRequest implements Serializable {
    private static final long serialVersionUID = -2139010449434575745L;
    private String payerName;

    private BigDecimal money;

    private String productName;

    private String orderId;

    private String payWay;

    private String remark;

    private Integer payerUid;
}
