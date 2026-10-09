package com.paradx.soul.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paradx.soul.pojo.User;
import com.paradx.soul.service.UserService;
import com.paradx.soul.mapper.ConsultationMapper;
import com.paradx.soul.mapper.DoctorMapper;
import com.paradx.soul.mapper.UserMapper;
import com.paradx.soul.utils.BCryptUtil;
import com.paradx.soul.utils.DeviceUtil;
import com.paradx.soul.utils.FileUploadUtil;
import com.paradx.soul.utils.JwtHelper;
import com.paradx.soul.utils.Result;
import com.paradx.soul.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private ConsultationMapper consultationMapper;
    @Autowired
    private DoctorMapper doctorMapper;

    @Value("${file.upload.path:uploads/avatars/}")
    private String uploadPath;

    /**
     * 用户注册
     * 账号由系统按角色规则分配，密码使用BCrypt加密存储，
     * 手机号必填唯一、邮箱选填唯一
     */
    @Override
    public synchronized Result regist(User user) {
        // 手机号唯一性校验
        if (userMapper.selectByPhone(user.getPhone()) != null) {
            return Result.build(null, 400, "该手机号已被注册");
        }
        // 邮箱唯一性校验（选填：填了才校验）
        if (StringUtils.hasText(user.getEmail()) && userMapper.selectByEmail(user.getEmail()) != null) {
            return Result.build(null, 400, "该邮箱已被注册");
        }

        // 系统分配账号（重试防止并发冲突）
        for (int i = 0; i < 3; i++) {
            Long userid = generateUserId(user.getRole());
            if (userMapper.selectById(userid) != null) {
                continue; // 极端并发下被占用，重新分配
            }
            user.setUserid(userid);
            // 使用BCrypt加密密码
            user.setPassword(BCryptUtil.encrypt(user.getPassword()));
            userMapper.insert(user);

            Map<String, Object> data = new HashMap<>();
            data.put("userid", userid);
            data.put("assigned", true);
            return Result.ok(data);
        }
        return Result.build(null, 500, "账号分配失败，请稍后重试");
    }

    /**
     * 按角色规则生成用户账号
     * 学生：入学年(4位) + 6位序号，如 2026000001
     * 医生：2 + 5位序号，如 200001
     */
    private Long generateUserId(Integer role) {
        if (role != null && role == 1) {
            long start = 200000L;
            long end = 299999L;
            Long max = userMapper.selectMaxUserIdInRange(start, end);
            long next = (max == null) ? start + 1 : max + 1;
            if (next > end) {
                throw new IllegalStateException("医生账号号段已用尽");
            }
            return next;
        }
        // 学生（默认）
        long base = (long) java.time.LocalDate.now().getYear() * 1000000L;
        long start = base;
        long end = base + 999999L;
        Long max = userMapper.selectMaxUserIdInRange(start, end);
        long next = (max == null) ? start + 1 : max + 1;
        if (next > end) {
            throw new IllegalStateException("本年度学生账号号段已用尽");
        }
        return next;
    }

    /**
     * 校验手机号/邮箱是否可注册
     */
    @Override
    public Result checkUnique(String field, String value) {
        if (!StringUtils.hasText(field) || !StringUtils.hasText(value)) {
            return Result.build(null, 400, "参数不能为空");
        }
        boolean available;
        if ("phone".equalsIgnoreCase(field)) {
            available = userMapper.selectByPhone(value.trim()) == null;
        } else if ("email".equalsIgnoreCase(field)) {
            available = userMapper.selectByEmail(value.trim()) == null;
        } else {
            return Result.build(null, 400, "不支持的校验字段");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("available", available);
        return Result.ok(data);
    }

    /**
     * 用户登录
     * 支持账号（学号/工号）、手机号、邮箱三种标识
     */
    @Override
    public Result login(String account, String password, DeviceUtil.DeviceType deviceType) {
        if (!StringUtils.hasText(account)) {
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
        }
        String identifier = account.trim();

        // 按格式识别登录标识：含@为邮箱，11位手机号为手机号，其余为账号
        User loginUser;
        if (identifier.contains("@")) {
            loginUser = userMapper.selectByEmail(identifier);
        } else if (identifier.matches("^1[3-9]\\d{9}$")) {
            // 兼容：手机号可能同时命中账号（纯数字），优先按账号精确匹配
            loginUser = userMapper.selectByPhone(identifier);
        } else {
            loginUser = null;
        }
        if (loginUser == null) {
            try {
                loginUser = userMapper.selectById(Long.parseLong(identifier));
            } catch (NumberFormatException ignored) {
                // 非数字且非手机号/邮箱，视为账号不存在
            }
        }

        // 账号判断
        if (loginUser == null) {
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
        }

        // 使用BCrypt验证密码
        if (StringUtils.hasText(password)
                && BCryptUtil.matches(password, loginUser.getPassword()))
        {
            // 设备类型验证：检查角色是否允许在当前设备上登录
            if (!DeviceUtil.isRoleAllowedOnDevice(loginUser.getRole(), deviceType)) {
                String deviceName = DeviceUtil.getDeviceName(deviceType);
                String allowedDesc = DeviceUtil.getAllowedDeviceDescription(loginUser.getRole());
                return Result.build(null, 403, "访问受限：" + allowedDesc + "（当前为" + deviceName + "）");
            }

            // 生成token（包含角色信息）
            String token = jwtHelper.createToken(loginUser.getUserid(), loginUser.getRole());
            Map<String, Object> data = new HashMap<>();
            data.put("userId", loginUser.getUserid());
            data.put("nickname", loginUser.getNickname());
            // 设置角色信息
            switch (loginUser.getRole()) {
                case 0: data.put("role", "学生"); break;
                case 1: data.put("role", "医生"); break;
                case 2: data.put("role", "管理员"); break;
            }
            data.put("token", token);
            data.put("deviceType", deviceType.getCode()); // 返回设备类型信息
            return Result.ok(data);
        }

        // 密码错误
        return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
    }

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
     * 手机号/邮箱需保持全局唯一
     */
    @Override
    public Result changeUserInfo(User user) {
        // 手机号唯一性校验（排除自己）
        if (StringUtils.hasText(user.getPhone())) {
            User exist = userMapper.selectByPhone(user.getPhone());
            if (exist != null && !exist.getUserid().equals(user.getUserid())) {
                return Result.build(null, 400, "该手机号已被其他账号使用");
            }
        }
        // 邮箱唯一性校验（排除自己）
        if (StringUtils.hasText(user.getEmail())) {
            User exist = userMapper.selectByEmail(user.getEmail());
            if (exist != null && !exist.getUserid().equals(user.getUserid())) {
                return Result.build(null, 400, "该邮箱已被其他账号使用");
            }
        }
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
     * 删除用户（注销账户）
     * 级联清理该用户的全部关联数据，避免残留脏数据：
     * 1. 该用户相关的所有预约订单（作为学生预约的 + 作为医生接诊的）
     * 2. 若为医生，同步删除其医生档案（否则首页会出现"无账号医生"）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result delUser(Long id) {
        if (id == null) {
            return Result.build(null, 400, "用户ID不能为空");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.build(null, 404, "用户不存在");
        }
        // 管理员账户受保护，不允许通过该接口注销
        if (user.getRole() != null && user.getRole() == 2) {
            return Result.build(null, 400, "管理员账户不可注销");
        }
        // 1) 级联删除该用户相关的所有订单
        consultationMapper.deleteByUser(id);
        // 2) 医生账号：同步删除医生档案
        if (user.getRole() != null && user.getRole() == 1) {
            doctorMapper.deldoc(id);
        }
        // 3) 删除用户本身
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
