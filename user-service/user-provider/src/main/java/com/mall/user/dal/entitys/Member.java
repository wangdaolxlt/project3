package com.mall.user.dal.entitys;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_member")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码，加密存储
     */
    private String password;

    /**
     * 注册手机号
     */
    private String phone;

    /**
     * 注册邮箱
     */
    private String email;

    private Date created;

    private Date updated;

    private String sex;

    private String address;

    private Integer state;

    /**
     * 头像
     */
    private String file;

    private String description;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 余额
     */
    private Double balance;

    /**
     *  是否激活,默认值N，激活Y
     */
    @Column(name = "isVerified")
    private String isVerified;


}