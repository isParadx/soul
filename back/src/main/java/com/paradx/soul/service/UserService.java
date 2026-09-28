package com.paradx.soul.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paradx.soul.pojo.User;
import com.paradx.soul.utils.DeviceUtil;
import com.paradx.soul.utils.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    Result regist(User user);

    /**
     * 用户登录（支持设备类型验证）
     * @param user 用户登录信息
     * @param deviceType 设备类型（PC/移动端）
     * @return 登录结果
     */
    Result login(User user, DeviceUtil.DeviceType deviceType);

    /**
     * 用户登录（兼容旧调用，默认PC端）
     */
    default Result login(User user) {
        return login(user, DeviceUtil.DeviceType.PC);
    }

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
