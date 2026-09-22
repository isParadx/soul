package com.paradx.soul.controller;

import com.paradx.soul.pojo.User;
import com.paradx.soul.pojo.vo.PasswordChangeRequest;
import com.paradx.soul.service.UserService;
import com.paradx.soul.utils.JwtHelper;
import com.paradx.soul.utils.Result;
import com.paradx.soul.utils.ResultCodeEnum;
import com.paradx.soul.utils.ValidationUtil;
import com.paradx.soul.utils.XssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     */
    @PostMapping("regist")
    public Result regist(@RequestBody User user) {
        // 参数校验
        if (user.getUserid() == null) {
            return Result.build(null, 400, "用户ID不能为空");
        }
        if (!ValidationUtil.isValidNickname(user.getNickname())) {
            return Result.build(null, 400, "昵称格式不正确（2-20位，支持中英文、数字、下划线）");
        }
        if (!ValidationUtil.isValidPassword(user.getPassword())) {
            return Result.build(null, 400, "密码格式不正确（6-20位，需包含字母和数字）");
        }
        
        // XSS过滤
        user.setNickname(XssUtil.clean(user.getNickname()));
        if (user.getEmail() != null) {
            user.setEmail(XssUtil.clean(user.getEmail()));
        }

        return userService.regist(user);
    }

    /**
     * 登录接口
     */
    @PostMapping("login")
    public Result login(@RequestBody User user) {
        // 基础校验
        if (user.getUserid() == null || user.getPassword() == null) {
            return Result.build(null, 400, "用户名或密码不能为空");
        }
        // XSS清理
        if (user.getPassword() != null) {
            user.setPassword(XssUtil.clean(user.getPassword()));
        }
        return userService.login(user);
    }

    /**
     * 获取所有用户信息接口（支持关键词搜索）
     */
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
     */
    @GetMapping("getUserInfo")
    public Result userInfo(@RequestHeader String token) {
        return userService.getUserInfo(token);
    }

    /**
     * 修改用户信息接口
     */
    @PostMapping("changeUserInfo")
    public Result changeUserInfo(@RequestBody User user) {
        // 校验昵称格式
        if (user.getNickname() != null && !ValidationUtil.isValidNickname(user.getNickname())) {
            return Result.build(null, 400, "昵称格式不正确");
        }
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
    public Result changePassword(@RequestHeader String token, @RequestBody PasswordChangeRequest request) {
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
     */
    @PostMapping("ResetPassword")
    public Result resetPassword(@RequestBody Long id) {
        if (id == null) {
            return Result.build(null, 400, "用户ID不能为空");
        }
        return userService.resetPassword(id);
    }

    /**
     * 删除用户（管理员）
     */
    @PostMapping("delUser")
    public Result delUser(@RequestBody Long id) {
        if (id == null) {
            return Result.build(null, 400, "用户ID不能为空");
        }
        return userService.delUser(id);
    }
}
