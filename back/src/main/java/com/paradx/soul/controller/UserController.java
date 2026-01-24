package com.paradx.soul.controller;

import com.alibaba.druid.util.StringUtils;
import com.paradx.soul.pojo.User;
import com.paradx.soul.pojo.vo.PasswordChangeRequest;
import com.paradx.soul.service.UserService;
import com.paradx.soul.utils.JwtHelper;
import com.paradx.soul.utils.MD5Util;
import com.paradx.soul.utils.Result;
import com.paradx.soul.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    JwtHelper jwtHelper;
    /**
     * 注册接口
     * 实现步骤:
     *   1. 将密码加密
     *   2. 将数据插入
     *   3. 判断结果,成 返回200 失败 505
     */
    @PostMapping("regist")
    public Result regist(@RequestBody User user){
        Result result = userService.regist(user);
        return result;
    }

    /**
     * 登录接口
     * @param user
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody User user){
        Result result = userService.login(user);
        return result;
    }

    /**
     * 获取所有用户信息接口
     * @return
     */
    @GetMapping("getAllUserInfo")
    public Result getAllUser(@RequestParam(value = "keywords", required = false) String keywords){
      Result result = userService.getAllUser(keywords);
        return result;
    }

    /**
     * 获取登录用户信息接口
     * @param token
     * @return
     */
    @GetMapping("getUserInfo")
    public Result userInfo(@RequestHeader String token){
        Result result = userService.getUserInfo(token);
        return result;
    }

    /**
     * 修改用户信息接口
     * @return
     */
    @PostMapping("changeUserInfo")
    public Result changeUserInfo(@RequestBody User user){
        Result result = userService.changeUserInfo(user);
        return result;
    }


    /**
     * 修改用户密码接口
     * @return
     */
    @PostMapping("changePassword")
    public Result changePassword(@RequestHeader String token,@RequestBody PasswordChangeRequest request ){
         Result result = userService.changePassword(token,request.getOldPassword(),request.getNewPassword());
        return result;
    }

    /**
     * 重置用户密码接口
     * @return
     */
    @PostMapping("ResetPassword")
    public Result ResetPassword(@RequestBody Long id){
        Result result = userService.resetPassword(id);
        return result;
    }

    /**
     * 删除用户
     */
    @PostMapping("delUser")
    public Result delOrder(@RequestBody Long id){
        Result result =userService.delUser(id);
        return  result;
    }
}