package com.mall.promo.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

import java.util.List;

/**
 * @author lenovo
 * @PackgeName: SeckillListResponse
 * @date: 2020/6/18
 * @Description:
 */
@Data
public class SeckillListResponse extends AbstractResponse {
    private static final long serialVersionUID = 4920782936245207032L;

    private Integer sessionId;

    private Integer psId;

    private List<SeckillProductDTO> productList;
}
