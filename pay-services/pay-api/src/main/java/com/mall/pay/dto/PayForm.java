package com.mall.pay.dto;

import com.mall.commons.result.AbstractRequest;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @PackgeName: com.mall.pay.dto
 * @ClassName: PayForm
 * @Author: Li Haiquan
 * Date: 2020/6/20 22:36
 * project name: cs-mall
 */
@Data
public class PayForm extends AbstractRequest {
    private String nickName;

    private BigDecimal money;

    private String info;

    private String orderId;

    private String payType;

    @Override
    public void requestCheck() {

    }
}
