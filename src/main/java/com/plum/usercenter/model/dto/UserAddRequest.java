package com.plum.usercenter.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建用户请求
 */
@Data
public class UserAddRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5439035398677972510L;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户角色: user, admin
     */
    private String userRole;
}
