package com.plum.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plum.usercenter.model.vo.LoginUserVO;
import com.plum.usercenter.service.UserService;
import com.plum.usercenter.model.entity.User;
import com.plum.usercenter.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static com.plum.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author qq233
 * @description 针对表【user(用户)】的数据库操作Service实现
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    //盐值
    private static final String SALT = "lilemy";
    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }
        if (userAccount.length() < 4) {
            return -1;
        }
        if (userPassword.length() < 6 || checkPassword.length() < 6) {
            return -1;
        }
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserAccount, userAccount);
            Long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                return -1;
            }
            // 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            int saveResult = userMapper.insert(user);
            if (saveResult != 1) {
                return -1;
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 6) {
            return null;
        }
        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        queryWrapper.eq(User::getUserPassword, encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            return null;
        }
        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return getLoginUserVO(user);
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setUsername(user.getUsername());
        loginUserVO.setAvatarUrl(user.getAvatarUrl());
        loginUserVO.setGender(user.getGender());
        loginUserVO.setPhone(user.getPhone());
        loginUserVO.setEmail(user.getEmail());
        loginUserVO.setUserRole(user.getUserRole());
        loginUserVO.setUpdateTime(user.getUpdateTime());
        loginUserVO.setCreateTime(user.getCreateTime());
        return loginUserVO;
    }
}




