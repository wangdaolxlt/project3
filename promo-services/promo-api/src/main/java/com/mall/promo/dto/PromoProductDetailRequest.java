package com.mall.promo.dto;

import com.mall.commons.result.AbstractRequest;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.promo.constants.PromoRetCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @PackgeName: com.mall.promo.dto
 * @ClassName: PromoProductDetailRequest
 * @Author: Li Haiquan
 * Date: 2020/6/17 12:21
 * project name: cs-mall
 */
@Data
public class PromoProductDetailRequest extends AbstractRequest {

    private Long psId;
    private Long productId;

    @Override
    public void requestCheck() {
        if(StringUtils.isBlank(psId.toString()) || StringUtils.isBlank(productId.toString())){
            throw new ValidateException(PromoRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode(),PromoRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
        }
    }
}
