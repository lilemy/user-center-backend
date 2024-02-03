package com.plum.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plum.usercenter.common.DeleteRequest;
import com.plum.usercenter.common.ResultCode;
import com.plum.usercenter.constant.PageConstant;
import com.plum.usercenter.exception.BusinessException;
import com.plum.usercenter.model.dto.UserAddRequest;
import com.plum.usercenter.model.dto.UserQueryRequest;
import com.plum.usercenter.model.dto.UserUpdateMyRequest;
import com.plum.usercenter.model.dto.UserUpdateRequest;
import com.plum.usercenter.model.vo.LoginUserVO;
import com.plum.usercenter.service.UserService;
import com.plum.usercenter.model.entity.User;
import com.plum.usercenter.mapper.UserMapper;
import com.plum.usercenter.utils.SqlUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static com.plum.usercenter.constant.UserConstant.ADMIN_ROLE;
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
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "用户账户过短");
        }
        if (userPassword.length() < 6 || checkPassword.length() < 6) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserAccount, userAccount);
            Long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ResultCode.PARAMS_ERROR, "账号重复");
            }
            // 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            int saveResult = userMapper.insert(user);
            if (saveResult != 1) {
                throw new BusinessException(ResultCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "用户账户过短");
        }
        if (userPassword.length() < 6) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "用户密码过短");
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
            throw new BusinessException(ResultCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return getLoginUserVO(user);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ResultCode.NOT_LOGIN_ERROR);
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public boolean userUpdate(UserUpdateMyRequest userUpdateMyRequest, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        int result = userMapper.updateById(user);
        if (result != 1) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        return true;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ResultCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询
        long userId = currentUser.getId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_LOGIN_ERROR);
        }
        return user;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "参数为空");
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

    @Override
    public long addUser(UserAddRequest userAddRequest, HttpServletRequest request) {
        boolean admin = isAdmin(request);
        if (!admin) {
            throw new BusinessException(ResultCode.NO_AUTH_ERROR);
        }
        String userAccount = userAddRequest.getUserAccount();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "账号重复");
        }
        String userPassword = "123456";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        userAddRequest.setUserPassword(encryptPassword);
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        int insert = userMapper.insert(user);
        if (insert != 1) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "添加失败，数据库错误");
        }
        return user.getId();
    }

    @Override
    public int deleteUser(Long id, HttpServletRequest request) {
        boolean admin = isAdmin(request);
        if (!admin || id == null) {
            throw new BusinessException(ResultCode.NO_AUTH_ERROR);
        }
        return userMapper.deleteById(id);
    }

    @Override
    public int updateUser(UserUpdateRequest updateRequest, HttpServletRequest request) {
        boolean admin = isAdmin(request);
        if (!admin || updateRequest == null) {
            throw new BusinessException(ResultCode.NO_AUTH_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(updateRequest, user);
        return userMapper.updateById(user);
    }

    @Override
    public User getUser(long id, HttpServletRequest request) {
        boolean admin = isAdmin(request);
        if (!admin || id <= 0) {
            throw new BusinessException(ResultCode.NO_AUTH_ERROR);
        }
        return userMapper.selectById(id);
    }

    @Override
    public Wrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            return null;
        }
        Long id = userQueryRequest.getId();
        String username = userQueryRequest.getUsername();
        String phone = userQueryRequest.getPhone();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "user_role", userRole);
        queryWrapper.like(StringUtils.isNotBlank(username), "username", username);
        queryWrapper.like(StringUtils.isNotBlank(phone), "phone", phone);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(PageConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }
}




