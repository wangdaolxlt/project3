package com.mall.pay.dal.entitys;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @PackgeName: com.mall.pay.dal.entitys
 * @ClassName: Payment
 * @Author: Li Haiquan
 * Date: 2020/6/20 21:01
 * project name: cs-mall
 */
@Table(name = "tb_payment")
@Data
public class Payment implements Serializable {
    private static final long serialVersionUID = -3787049283479054254L;

    /**
     * `id` int(50) NOT NULL AUTO_INCREMENT,
     * `status` varchar(20) NOT NULL COMMENT '支付状态',
     * `order_id` varchar(50) NOT NULL COMMENT '订单id',
     * `product_name` varchar(80) DEFAULT NULL COMMENT '产品名称',
     * `pay_no` varchar(80) DEFAULT NULL COMMENT '第三方返回单号',
     * `trade_no` varchar(80) DEFAULT NULL COMMENT '支付流水号',
     * `payer_uid` int(20) NOT NULL COMMENT '付款人id',
     * `payer_name` varchar(50) DEFAULT NULL COMMENT '付款人姓名(用户名)',
     * `payer_amount` decimal(10,2) NOT NULL COMMENT '付款方支付金额',
     * `order_amount` decimal(10,2) NOT NULL COMMENT '订单金金额',
     * `pay_way` varchar(10) NOT NULL COMMENT '支付方式',
     * `pay_success_time` datetime DEFAULT NULL COMMENT '支付成功时间',
     * `complete_time` datetime DEFAULT NULL COMMENT '支付完成时间',
     * `remark` varchar(500) DEFAULT NULL COMMENT '备注',
     * `create_time` datetime DEFAULT NULL,
     * `update_time` datetime DEFAULT NULL,
     */
    @Id
    private Integer id;

    @Column
    private String status;

    @Column
    private String orderId;

    @Column
    private String productName;

    @Column
    private String payNo;

    @Column
    private String tradeNo;

    @Column(name = "payer_uid")
    private Integer payerUid;

    @Column
    private String payerName;

    @Column
    private BigDecimal payerAmount;

    @Column
    private BigDecimal orderAmount;

    @Column
    private String payWay;

    @Column(name = "pay_success_time")
    private Date paySuccessTime;

    @Column
    private Date completeTime;

    @Column
    private String remark;

    @Column
    private Date createTime;

    @Column
    private Date updateTime;
}
