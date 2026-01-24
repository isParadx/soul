package com.paradx.soul.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paradx.soul.pojo.User;

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
}




