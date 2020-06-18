package com.mall.promo.dto;

import com.mall.commons.result.AbstractRequest;
import lombok.Data;

/**
 * @author lenovo
 * @PackgeName: SeckillListRequest
 * @date: 2020/6/18
 * @Description:
 */
@Data
public class SeckillListRequest extends AbstractRequest {
    private Integer sessionId;
    private String yyyymmdd;

    @Override
    public void requestCheck() {

    }
}
