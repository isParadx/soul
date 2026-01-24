package com.paradx.soul.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paradx.soul.pojo.User;
import com.paradx.soul.utils.Result;

/**
 *
 */
public interface UserService extends IService<User> {
    Result regist(User user);

    Result login(User user);

    Result getUserInfo(String token);

    Result changeUserInfo(User user);

    Result changePassword(String token, String oldPassword, String newPassword);

    Result getAllUser(String keywords);

    Result delUser(Long id);

    Result resetPassword(Long id);
}
