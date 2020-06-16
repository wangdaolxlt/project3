package com.mall.promo.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

import java.util.List;

@Data
public class SeckillListResponse extends AbstractResponse {


    private static final long serialVersionUID = -4756921735031775177L;

    private Integer sessionId;

    private Integer psId;

    private List<SeckillProductDTO> productList;

}
