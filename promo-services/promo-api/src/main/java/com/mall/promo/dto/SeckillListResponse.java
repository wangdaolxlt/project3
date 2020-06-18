package com.mall.promo.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

import java.util.List;

@Data
public class SeckillListResponse extends AbstractResponse {

    private Integer sessionId;

    private Integer psId;

    private List<SeckillProductDTO> productList;

}
