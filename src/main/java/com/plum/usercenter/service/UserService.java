package com.plum.usercenter.service;

import com.plum.usercenter.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plum.usercenter.model.vo.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author qq233
 * @description 针对表【user(用户)】的数据库操作Service
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      用户登录态
     * @return 用户信息（脱敏）
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取脱敏用户信息
     *
     * @param user 用户信息
     * @return 脱敏用户
     */
    LoginUserVO getLoginUserVO(User user);
}
