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
    /**
     * 用户注册
     * 账号由系统按角色规则分配（学生=入学年+6位序号，医生=2+5位序号）
     * 手机号必填且唯一，邮箱选填且唯一
     */
    Result regist(User user);

    /**
     * 校验手机号/邮箱是否已被占用
     * @param field phone 或 email
     * @param value 待校验的值
     * @return data.available=true 表示可用
     */
    Result checkUnique(String field, String value);

    /**
     * 用户登录（支持账号/手机号/邮箱 + 设备类型验证）
     * @param account 登录标识：账号、手机号或邮箱
     * @param password 明文密码
     * @param deviceType 设备类型（PC/移动端）
     * @return 登录结果
     */
    Result login(String account, String password, DeviceUtil.DeviceType deviceType);

    /**
     * 用户登录（兼容旧调用，默认PC端）
     */
    default Result login(String account, String password) {
        return login(account, password, DeviceUtil.DeviceType.PC);
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
