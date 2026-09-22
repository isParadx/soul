package com.paradx.soul.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paradx.soul.pojo.User;
import com.paradx.soul.service.UserService;
import com.paradx.soul.mapper.UserMapper;
import com.paradx.soul.utils.BCryptUtil;
import com.paradx.soul.utils.FileUploadUtil;
import com.paradx.soul.utils.JwtHelper;
import com.paradx.soul.utils.Result;
import com.paradx.soul.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务实现类
 * 使用BCrypt进行密码加密
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService{

    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private UserMapper userMapper;

    @Value("${file.upload.path:uploads/avatars/}")
    private String uploadPath;

    /**
     * 用户注册
     * 密码使用BCrypt加密存储
     */
    @Override
    public Result regist(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserid, user.getUserid());
        Long count = userMapper.selectCount(queryWrapper);

        if (count > 0) {
            return Result.build(null, ResultCodeEnum.USERNAME_USED);
        }

        // 使用BCrypt加密密码
        user.setPassword(BCryptUtil.encrypt(user.getPassword()));
        int rows = userMapper.insert(user);
        return Result.ok(null);
    }

    /**
     * 用户登录
     * 使用BCrypt验证密码
     */
    @Override
    public Result login(User user) {
        // 根据账号查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserid, user.getUserid());
        User loginUser = userMapper.selectOne(queryWrapper);

        // 账号判断
        if (loginUser == null) {
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
        }

        // 使用BCrypt验证密码
        if (StringUtils.hasText(user.getPassword())
                && BCryptUtil.matches(user.getPassword(), loginUser.getPassword()))
        {
            // 生成token（包含角色信息）
            String token = jwtHelper.createToken(loginUser.getUserid(), loginUser.getRole());
            Map<String, Object> data = new HashMap<>();
            data.put("userId", loginUser.getUserid());
            // 设置角色信息
            switch (loginUser.getRole()) {
                case 0: data.put("role", "学生"); break;
                case 1: data.put("role", "医生"); break;
                case 2: data.put("role", "管理员"); break;
            }
            data.put("token", token);
            return Result.ok(data);
        }

        // 密码错误
        return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
    }

    /**
     * 获取当前登录用户信息
     */
    @Override
    public Result getUserInfo(String token) {
        Long userId = jwtHelper.getUserId(token);
        if (userId == null) {
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }
        
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
            Map<String, Object> data = new HashMap<>();
            data.put("loginUser", user);
            return Result.ok(data);
        }
        return Result.build(null, ResultCodeEnum.NOTLOGIN);
    }

    /**
     * 修改用户信息
     */
    @Override
    public Result changeUserInfo(User user) {
        userMapper.changeUserInfo(user);
        return Result.ok(null);
    }

    /**
     * 修改密码
     * 使用BCrypt验证旧密码，加密新密码
     */
    @Override
    public Result changePassword(String token, String oldPassword, String newPassword) {
        // 获取用户ID
        Long userId = jwtHelper.getUserId(token);
        if (userId == null) {
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }

        // 查询用户
        User user = userMapper.selectById(userId);
        
        // 验证旧密码（使用BCrypt）
        if (!BCryptUtil.matches(oldPassword, user.getPassword())) {
            return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
        }

        // 更新密码（使用BCrypt加密）
        user.setPassword(BCryptUtil.encrypt(newPassword));
        userMapper.changeUserInfo(user);
        return Result.ok(null);
    }

    /**
     * 获取所有用户列表（支持关键词搜索）
     */
    @Override
    public Result getAllUser(String keywords) {
        List<User> list = userMapper.getAllUser(keywords);
        return Result.ok(list);
    }

    /**
     * 删除用户
     */
    @Override
    public Result delUser(Long id) {
        userMapper.delUser(id);
        return Result.ok(null);
    }

    /**
     * 重置用户密码为默认值
     * 使用BCrypt加密默认密码
     */
    @Override
    public Result resetPassword(Long id) {
        String password = BCryptUtil.encrypt("123456");
        userMapper.resetPwd(password, id);
        return Result.ok(null);
    }

    /**
     * 上传用户头像
     * 包含文件安全校验和存储
     */
    @Override
    public Result uploadAvatar(String token, MultipartFile file) {
        // 获取用户ID
        Long userId = jwtHelper.getUserId(token);
        if (userId == null) {
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }

        // 校验文件安全性
        String validateError = FileUploadUtil.validateImageFile(file);
        if (validateError != null) {
            return Result.build(null, 400, validateError);
        }

        // 查询用户现有信息（用于删除旧头像）
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }

        try {
            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 删除旧头像文件
            if (StringUtils.hasText(user.getImg())) {
                String oldFilePath = uploadPath + user.getImg().substring(user.getImg().lastIndexOf("/") + 1);
                File oldFile = new File(oldFilePath);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            // 生成安全的文件名并保存
            String safeFileName = FileUploadUtil.generateSafeFileName(file.getOriginalFilename());
            File destFile = new File(uploadPath + safeFileName);
            file.transferTo(destFile);

            // 更新用户头像路径
            user.setImg("/uploads/avatars/" + safeFileName);
            userMapper.changeUserInfo(user);

            Map<String, Object> data = new HashMap<>();
            data.put("imgUrl", user.getImg());
            return Result.ok(data);

        } catch (IOException e) {
            return Result.build(null, 500, "头像上传失败：" + e.getMessage());
        }
    }
}
