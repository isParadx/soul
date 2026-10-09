package com.paradx.soul.controller;

import com.paradx.soul.annotation.RequireRole;
import com.paradx.soul.pojo.User;
import com.paradx.soul.pojo.vo.LoginRequest;
import com.paradx.soul.pojo.vo.PasswordChangeRequest;
import com.paradx.soul.service.UserService;
import com.paradx.soul.utils.DeviceUtil;
import com.paradx.soul.utils.JwtHelper;
import com.paradx.soul.utils.Result;
import com.paradx.soul.utils.ResultCodeEnum;
import com.paradx.soul.utils.ValidationUtil;
import com.paradx.soul.utils.XssUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户控制器
 * 处理用户相关的HTTP请求
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper jwtHelper;

    /**
     * 注册接口
     * 账号由系统按角色规则分配（学生=入学年+6位序号，医生=2+5位序号），前端不再传入
     * 手机号必填唯一，邮箱选填唯一
     */
    @PostMapping("regist")
    public Result regist(@RequestBody User user) {
        // 参数校验
        if (!ValidationUtil.isValidNickname(user.getNickname())) {
            return Result.build(null, 400, "昵称格式不正确（2-20位，支持中英文、数字、下划线）");
        }
        if (!ValidationUtil.isValidPassword(user.getPassword())) {
            return Result.build(null, 400, "密码格式不正确（6-20位，需包含字母和数字）");
        }
        if (user.getPhone() == null || !ValidationUtil.isValidPhone(user.getPhone())) {
            return Result.build(null, 400, "手机号格式不正确");
        }
        if (StringUtils.hasText(user.getEmail()) && !ValidationUtil.isValidEmail(user.getEmail())) {
            return Result.build(null, 400, "邮箱格式不正确");
        }
        // 角色校验：仅允许自助注册学生(0)和医生(1)
        if (user.getRole() == null || (user.getRole() != 0 && user.getRole() != 1)) {
            user.setRole(0);
        }
        // 性别默认值
        if (user.getSex() == null) {
            user.setSex(0);
        }
        // 邮箱空串归一为null，避免唯一索引冲突
        if (!StringUtils.hasText(user.getEmail())) {
            user.setEmail(null);
        }
        // XSS过滤
        user.setNickname(XssUtil.clean(user.getNickname()));
        if (user.getEmail() != null) {
            user.setEmail(XssUtil.clean(user.getEmail()));
        }

        return userService.regist(user);
    }

    /**
     * 手机号/邮箱唯一性校验接口（供注册页实时校验）
     * @param field phone 或 email
     */
    @GetMapping("checkUnique")
    public Result checkUnique(@RequestParam String field, @RequestParam String value) {
        return userService.checkUnique(field, value);
    }

    /**
     * 登录接口
     * 支持账号（学号/工号）、手机号、邮箱 + 密码登录
     * 支持设备类型检测，限制不同角色在指定设备上登录
     */
    @PostMapping("login")
    public Result login(@RequestBody LoginRequest loginRequest,
                        @RequestHeader(value = "X-Device-Type", required = false) String deviceType) {
        // 基础校验
        String account = loginRequest.resolveAccount();
        if (account == null || account.isEmpty() || loginRequest.getPassword() == null) {
            return Result.build(null, 400, "账号和密码不能为空");
        }
        // XSS清理
        String password = XssUtil.clean(loginRequest.getPassword());

        // 检测设备类型
        DeviceUtil.DeviceType detectedDevice;
        if (deviceType != null && !deviceType.isEmpty()) {
            // 优先使用请求头指定的设备类型
            detectedDevice = DeviceUtil.DeviceType.fromCode(deviceType);
        } else {
            // 通过User-Agent自动检测
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                detectedDevice = DeviceUtil.detectDevice(attributes.getRequest());
            } else {
                detectedDevice = DeviceUtil.DeviceType.PC;
            }
        }

        // 调用登录服务（传入登录标识与设备类型）
        return userService.login(account, password, detectedDevice);
    }

    /**
     * 获取所有用户信息接口（支持关键词搜索）
     * 权限：仅管理员可访问
     */
    @RequireRole(adminOnly = true)
    @GetMapping("getAllUserInfo")
    public Result getAllUser(@RequestParam(value = "keywords", required = false) String keywords) {
        // XSS过滤搜索关键词
        if (keywords != null && !keywords.isEmpty()) {
            keywords = XssUtil.clean(keywords);
            // 防止超长输入
            if (keywords.length() > 50) {
                keywords = keywords.substring(0, 50);
            }
        }
        return userService.getAllUser(keywords);
    }

    /**
     * 获取登录用户信息接口
     * token支持三种来源：token请求头、Authorization: Bearer、请求参数
     */
    @GetMapping("getUserInfo")
    public Result userInfo(HttpServletRequest request,
                           @RequestHeader(value = "token", required = false) String token) {
        return userService.getUserInfo(resolveToken(request, token));
    }

    /**
     * 修改用户信息接口
     */
    @PostMapping("changeUserInfo")
    public Result changeUserInfo(HttpServletRequest request,
                                @RequestHeader(value = "token", required = false) String token,
                                @RequestBody User user) {
        // 安全约束：只能修改当前登录用户自己的信息
        String realToken = resolveToken(request, token);
        Long userId = (realToken != null && !realToken.isEmpty()) ? jwtHelper.getUserId(realToken) : null;
        if (userId == null) {
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }
        user.setUserid(userId);
        // 校验昵称格式
        if (user.getNickname() != null && !ValidationUtil.isValidNickname(user.getNickname())) {
            return Result.build(null, 400, "昵称格式不正确");
        }
        // 校验手机号格式（必填字段若传了就校验）
        if (user.getPhone() != null && !ValidationUtil.isValidPhone(user.getPhone())) {
            return Result.build(null, 400, "手机号格式不正确");
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty() && !ValidationUtil.isValidEmail(user.getEmail())) {
            return Result.build(null, 400, "邮箱格式不正确");
        }
        // 不允许通过该接口修改角色/密码
        user.setRole(null);
        user.setPassword(null);
        // XSS过滤
        if (user.getNickname() != null) {
            user.setNickname(XssUtil.clean(user.getNickname()));
        }
        if (user.getEmail() != null) {
            user.setEmail(XssUtil.clean(user.getEmail()));
        }
        return userService.changeUserInfo(user);
    }

    /**
     * 修改用户密码接口
     */
    @PostMapping("changePassword")
    public Result changePassword(HttpServletRequest httpRequest,
                                 @RequestHeader(value = "token", required = false) String token,
                                 @RequestBody PasswordChangeRequest request) {
        token = resolveToken(httpRequest, token);
        // 参数校验
        if (request.getOldPassword() == null || request.getNewPassword() == null) {
            return Result.build(null, 400, "旧密码和新密码不能为空");
        }
        if (!ValidationUtil.isValidPassword(request.getNewPassword())) {
            return Result.build(null, 400, "新密码格式不正确（6-20位，需包含字母和数字）");
        }
        return userService.changePassword(token, request.getOldPassword(), request.getNewPassword());
    }

    /**
     * 重置用户密码接口（管理员）
     * 权限：仅管理员可访问
     */
    @RequireRole(adminOnly = true)
    @PostMapping("ResetPassword")
    public Result resetPassword(@RequestBody Long id) {
        if (id == null) {
            return Result.build(null, 400, "用户ID不能为空");
        }
        return userService.resetPassword(id);
    }

    /**
     * 删除用户（管理员）
     * 权限：仅管理员可访问
     */
    @RequireRole(adminOnly = true)
    @PostMapping("delUser")
    public Result delUser(@RequestBody Long id) {
        if (id == null) {
            return Result.build(null, 400, "用户ID不能为空");
        }
        return userService.delUser(id);
    }

    /**
     * 上传用户头像
     * 支持JPG/PNG/GIF/WebP格式，最大5MB
     */
    @PostMapping("uploadAvatar")
    public Result uploadAvatar(HttpServletRequest httpRequest,
                               @RequestHeader(value = "token", required = false) String token,
                               @RequestParam("file") MultipartFile file) {
        // 校验文件是否为空
        if (file == null || file.isEmpty()) {
            return Result.build(null, 400, "请选择要上传的头像文件");
        }
        token = resolveToken(httpRequest, token);
        return userService.uploadAvatar(token, file);
    }

    /**
     * 统一解析Token：优先请求头，其次Authorization: Bearer，最后拦截器写入的请求属性
     * @param request HTTP请求
     * @param token 已从token头获取的值（可能为空）
     * @return 解析出的Token，解析失败返回原值
     */
    private String resolveToken(HttpServletRequest request, String token) {
        if (token != null && !token.isEmpty()) {
            return token;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        Object attr = request.getAttribute("token");
        return attr != null ? attr.toString() : token;
    }
}
