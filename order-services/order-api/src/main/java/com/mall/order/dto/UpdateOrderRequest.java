package com.mall.order.dto;

import com.mall.commons.result.AbstractRequest;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.order.constant.OrderRetCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @PackgeName: com.mall.order.dto
 * @ClassName: UpdateOrderRequest
 * @Author: Li Haiquan
 * Date: 2020/6/21 10:31
 * project name: cs-mall
 */
@Data
public class UpdateOrderRequest extends AbstractRequest {
    private String orderId;

    @Override
    public void requestCheck() {
        if(StringUtils.isBlank(orderId)){
            throw new ValidateException(OrderRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode(),OrderRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
        }
    }
}
