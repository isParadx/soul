package com.paradx.soul.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户实体类
 */
@TableName(value = "user")
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID（学号/工号） */
    @TableId(type = IdType.INPUT)
    private Long userid;
    
    /** 昵称 */
    private String nickname;
    
    /** 性别（0-女，1-男） */
    private Integer sex;
    
    /** 密码（BCrypt加密） */
    private String password;
    
    /** 手机号 */
    private Long phone;
    
    /** 邮箱 */
    private String email;
    
    /** 角色（0-学生，1-医生，2-管理员） */
    private Integer role;
    
    /** 头像路径 */
    private String img;

    public User() {
    }

    public User(Long userid, String nickname, Integer sex, String password, 
                Long phone, String email, Integer role, String img) {
        this.userid = userid;
        this.nickname = nickname;
        this.sex = sex;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.img = img;
    }
}
