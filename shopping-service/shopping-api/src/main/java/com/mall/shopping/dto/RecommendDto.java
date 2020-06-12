package com.mall.shopping.dto;/**
 * Created by ciggar on 2019/7/29.
 */

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *  ciggar
 * create-date: 2019/7/29-下午11:14
 */
@Data
public class RecommendDto implements Serializable{

    private static final long serialVersionUID = 6015856530008472448L;
    private Integer id;

    private String name;

    private Integer type;

    private Integer sortOrder;

    private Integer position;

    private String remark;

    private Integer status;

    private Integer limitNum;

}
