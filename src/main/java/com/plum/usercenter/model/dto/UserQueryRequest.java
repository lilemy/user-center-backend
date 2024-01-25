package com.plum.usercenter.model.dto;

import com.plum.usercenter.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -1806942463107908765L;
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
}
