package com.plum.usercenter.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录请求
 */
@Data
public class UserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -122731030354427078L;

    private String userAccount;

    private String userPassword;
}
