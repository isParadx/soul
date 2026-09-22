package com.paradx.soul.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paradx.soul.pojo.User;
import com.paradx.soul.utils.Result;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 上传用户头像
     * @param token 用户Token
     * @param file 头像文件
     * @return 头像访问路径
     */
    Result uploadAvatar(String token, MultipartFile file);
}
