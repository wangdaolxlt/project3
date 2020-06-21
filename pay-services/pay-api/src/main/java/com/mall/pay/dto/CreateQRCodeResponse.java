package com.mall.pay.dto;

import lombok.Data;

/**
 * @PackgeName: com.mall.pay.dto
 * @ClassName: CreateQRCodeResponse
 * @Author: Li Haiquan
 * Date: 2020/6/20 23:48
 * project name: cs-mall
 */
@Data
public class CreateQRCodeResponse {
    private String code;
    private String msg;
    private String QRCode;
}
