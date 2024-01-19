package com.plum.usercenter.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 已登录用户（脱敏）
 */
@Data
public class LoginUserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8507118560811269749L;
    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;


    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
