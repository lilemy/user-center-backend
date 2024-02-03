package com.plum.usercenter.model.dto;

import lombok.Data;

/**
 * 用户更新个人数据请求
 */
@Data
public class UserUpdateMyRequest {
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
}
