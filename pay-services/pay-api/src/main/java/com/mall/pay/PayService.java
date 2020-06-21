package com.mall.pay;

import com.mall.pay.dto.PaymentRequest;
import com.mall.pay.dto.PaymentResponse;
import com.mall.pay.dto.QueryAlipayStatusResponse;

/**
 * @PackgeName: com.mall.pay
 * @ClassName: PayService
 * @Author: Li Haiquan
 * Date: 2020/6/20 20:55
 * project name: cs-mall
 */
public interface PayService {

    PaymentResponse aliPay(PaymentRequest request);

    QueryAlipayStatusResponse queryAlipayStatus(PaymentRequest request);
}
