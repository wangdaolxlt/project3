package com.mall.shopping.dal.entitys;

import com.mall.shopping.dto.PanelContentDto;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data

public class PanelContentItemD implements Serializable {
    PanelContent panelContent;
    private String productName;

    private BigDecimal salePrice;

    private String subTitle;
}
