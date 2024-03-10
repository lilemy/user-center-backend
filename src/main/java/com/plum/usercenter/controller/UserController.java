package com.plum.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plum.usercenter.common.BaseResponse;
import com.plum.usercenter.common.DeleteRequest;
import com.plum.usercenter.common.ResultCode;
import com.plum.usercenter.common.ResultUtils;
import com.plum.usercenter.exception.BusinessException;
import com.plum.usercenter.model.dto.*;
import com.plum.usercenter.model.entity.User;
import com.plum.usercenter.model.vo.LoginUserVO;
import com.plum.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    //用户注册
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    //用户登录
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数为空");
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    // 用户注销
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数为空");
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    // 获取当前登录用户
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    // 更新个人信息
    @PostMapping("/update/my")
    public BaseResponse<Boolean> userUpdate(@RequestBody UserUpdateMyRequest userUpdateMyRequest, HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数为空");
        }
        boolean result = userService.userUpdate(userUpdateMyRequest, request);
        return ResultUtils.success(result);
    }

    // 管理员对用户进行增删改查

    // 增加用户
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数为空");
        }
        long result = userService.addUser(userAddRequest, request);
        return ResultUtils.success(result);
    }

    // 根据id删除用户
    @PostMapping("/delete")
    public BaseResponse<Integer> deleteUserById(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数为空");
        }
        Long id = deleteRequest.getId();
        int result = userService.deleteUser(id, request);
        return ResultUtils.success(result);
    }

    // 根据id修改用户
    @PostMapping("/update")
    public BaseResponse<Integer> updateUserById(@RequestBody UserUpdateRequest updateRequest, HttpServletRequest request) {
        if (updateRequest == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数为空");
        }
        int result = userService.updateUser(updateRequest, request);
        return ResultUtils.success(result);
    }

    // 根据id获取用户
    @GetMapping("/get")
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数错误");
        }
        User user = userService.getUser(id, request);
        return ResultUtils.success(user);
    }

    // 分页获取用户
    @PostMapping("/list/page")
    public BaseResponse<Page<User>> getUserByPage(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {
        boolean admin = userService.isAdmin(request);
        if (!admin) {
            throw new BusinessException(ResultCode.NO_AUTH_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }
}
