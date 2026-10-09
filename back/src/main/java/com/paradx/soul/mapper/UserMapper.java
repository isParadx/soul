package com.paradx.soul.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paradx.soul.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.paradx.soul.pojo.User
 */
public interface UserMapper extends BaseMapper<User> {
    int insert(User user);
    User selectById(Long userId);

    int changeUserInfo(User user);

    List<User> getAllUser(String keywords);

    void delUser(Long id);

    void resetPwd(String password, Long id);

    /** 按手机号查询用户（登录/唯一校验用） */
    User selectByPhone(String phone);

    /** 按邮箱查询用户（登录/唯一校验用） */
    User selectByEmail(String email);

    /** 查询指定区间内最大的用户ID（系统分配账号用） */
    Long selectMaxUserIdInRange(@Param("start") Long start, @Param("end") Long end);
}




