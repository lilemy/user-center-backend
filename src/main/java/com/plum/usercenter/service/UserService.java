package com.plum.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.plum.usercenter.model.dto.UserAddRequest;
import com.plum.usercenter.model.dto.UserQueryRequest;
import com.plum.usercenter.model.dto.UserUpdateMyRequest;
import com.plum.usercenter.model.dto.UserUpdateRequest;
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
     * 用户注销
     *
     * @param request 用户登录态
     * @return 注销状态
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 用户更新个人信息
     *
     * @param userUpdateMyRequest 修改信息
     * @param request             用户登录态
     * @return 修改情况
     */
    boolean userUpdate(UserUpdateMyRequest userUpdateMyRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request 用户登录态
     * @return 脱敏用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取脱敏用户信息
     *
     * @param user 用户信息
     * @return 脱敏用户
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 管理员创建用户
     *
     * @param userAddRequest 创建用户请求
     * @param request        用户权限
     * @return 新用户 id
     */
    long addUser(UserAddRequest userAddRequest, HttpServletRequest request);

    /**
     * 管理员删除用户
     *
     * @param id      删除用户id
     * @param request 用户权限
     * @return 删除结果
     */
    int deleteUser(Long id, HttpServletRequest request);

    /**
     * 管理员修改用户
     *
     * @param updateRequest 更新用户信息
     * @param request       用户权限
     * @return 更新结果
     */
    int updateUser(UserUpdateRequest updateRequest, HttpServletRequest request);

    /**
     * 管理员查询用户
     *
     * @param id      需要查询用户id
     * @param request 用户权限
     * @return 查询用户信息
     */
    User getUser(long id, HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    Wrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 是否为管理员
     *
     * @param request 登录用户
     * @return true -> 管理员
     */
    boolean isAdmin(HttpServletRequest request);

}
