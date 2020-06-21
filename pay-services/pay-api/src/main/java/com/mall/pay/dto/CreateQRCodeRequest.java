package com.mall.pay.dto;

import lombok.Data;

/**
 * @PackgeName: com.mall.pay.dto
 * @ClassName: CreateQRCodeRequest
 * @Author: Li Haiquan
 * Date: 2020/6/20 23:38
 * project name: cs-mall
 */
@Data
public class CreateQRCodeRequest {
    private String orderId;
    private String totalAmount;
}
