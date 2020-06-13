package com.mall.user.dal.entitys;

import lombok.*;
import org.checkerframework.checker.units.qual.C;

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
    @Column
    private String username;

    /**
     * 密码，加密存储
     */
    @Column
    private String password;

    /**
     * 注册手机号
     */
    @Column
    private String phone;

    /**
     * 注册邮箱
     */
    @Column
    private String email;

    @Column
    private Date created;

    @Column
    private Date updated;

    @Column
    private String sex;
    @Column
    private String address;
    @Column
    private Integer state;

    /**
     * 头像
     */
    @Column
    private String file;
    @Column
    private String description;

    /**
     * 积分
     */
    @Column
    private Integer points;

    /**
     * 余额
     */
    @Column
    private Double balance;

    /**
     *  是否激活,默认值N，激活Y
     */
    @Column(name = "isverified")
    private String isVerified;


}