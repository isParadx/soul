package com.paradx.soul.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paradx.soul.pojo.User;
import com.paradx.soul.service.UserService;
import com.paradx.soul.mapper.UserMapper;
import com.paradx.soul.utils.JwtHelper;
import com.paradx.soul.utils.MD5Util;
import com.paradx.soul.utils.Result;
import com.paradx.soul.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService{

    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private  UserMapper userMapper;

    @Override
    public Result regist(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserid,user.getUserid());
        Long count = userMapper.selectCount(queryWrapper);

        if (count > 0){
            return Result.build(null, ResultCodeEnum.USERNAME_USED);
        }

        user.setPassword(MD5Util.encrypt(user.getPassword()));
        int rows = userMapper.insert(user);
        System.out.println("rows = " + rows);
        return Result.ok(null);
    }

    @Override
    public Result login(User user) {
        /**
         * 大概流程:
         *    1. 账号进行数据库查询 返回用户对象
         *    2. 对比用户密码(md5加密)
         *    3. 成功,根据userId生成token -> map key=token value=token值 - result封装
         *    4. 失败,判断账号还是密码错误,封装对应的枚举错误即可
         */
        //根据账号查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserid,user.getUserid());
        User loginUser = userMapper.selectOne(queryWrapper);

        //账号判断
        if (loginUser == null) {
            //账号错误
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
        }

        //判断密码
        if (!StringUtils.isEmpty(user.getPassword())
                && loginUser.getPassword().equals(MD5Util.encrypt(user.getPassword())))
        {
            //账号密码正确
            //根据用户唯一标识生成token
            //获取用户的角色
            String token = jwtHelper.createToken(loginUser.getUserid());
            Map data = new HashMap();
            data.put("userId",loginUser.getUserid());
            switch (loginUser.getRole()){
                case 0 :data.put("role","学生");
                    break;
                case 1 :data.put("role","医生");
                    break;
                case 2 :data.put("role","管理员");
                    break;
            }
            data.put("token",token);
            data.put("token",token);
            return Result.ok(data);
        }

        //密码错误
        return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
    }

    @Override
    public Result getUserInfo(String token) {
        //1.获取token对应的用户
        Long userId = jwtHelper.getUserId(token).longValue();
        //2.查询数据
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
            Map data = new HashMap();
            data.put("loginUser",user);
            return Result.ok(data);
        }

        return Result.build(null,ResultCodeEnum.NOTLOGIN);
    }

    @Override
    public Result changeUserInfo(User user) {
        userMapper.changeUserInfo(user);
        return Result.ok(null);
    }

    @Override
    public Result changePassword(String token, String oldPassword, String newPassword) {
        // 1.获取token对应的用户ID
        Long userId = jwtHelper.getUserId(token);
        if (userId == null) {
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }

        // 2.查询数据
        User user = userMapper.selectById(userId);
        //3.比较旧密码
        if (!user.getPassword().equals(MD5Util.encrypt(oldPassword))) {
            return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
        }

        // 4.更新密码
        user.setPassword(MD5Util.encrypt(newPassword));
        int updatedRows = userMapper.changeUserInfo(user);
        // 5.返回结果
        return Result.ok(null);
    }



    @Override
    public Result getAllUser(String keywords) {
        List<User> list =userMapper.getAllUser(keywords);
        return Result.ok(list);
    }

    @Override
    public Result delUser(Long id) {
        userMapper.delUser(id);
        return null;
    }

    @Override
    public Result resetPassword(Long id) {

        String password = MD5Util.encrypt("123");
        userMapper.resetPwd(password,id);
        return null;
    }
}





