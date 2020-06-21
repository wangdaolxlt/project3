package com.mall.pay.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;


/**
 * @PackgeName: com.mall.pay.dto
 * @ClassName: PaymentResponse
 * @Author: Li Haiquan
 * Date: 2020/6/20 23:06
 * project name: cs-mall
 */
@Data
public class PaymentResponse extends AbstractResponse{

    private String picName;
}
