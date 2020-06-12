package com.mall.shopping.dal.entitys;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *  ciggar
 * create-date: 2019/7/23-18:03
 */
@Data

@Table(name = "tb_panel_content_item")
public class PanelContentItem implements Serializable {

    private static final long serialVersionUID = 2917508435515179027L;
    @Id
    @KeySql(useGeneratedKeys = true)

    private Integer id;

    private Integer panelId;

    private Integer type;

    private Long productId;

    private Integer sortOrder;

    private String fullUrl;

    private String picUrl;

    private String picUrl2;

    private String picUrl3;

    private Date created;

    private Date updated;

    private String productName;

    private BigDecimal salePrice;

    private String subTitle;
}
