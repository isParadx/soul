package com.paradx.soul.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求参数
 * 支持"账号 / 手机号 / 邮箱"三种方式登录
 */
@Data
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 登录标识：账号(学号/工号)、手机号或邮箱 */
    private String account;

    /** 兼容旧前端字段名（与account等价） */
    private String userid;

    /** 密码（明文，服务端BCrypt校验） */
    private String password;

    /**
     * 获取实际使用的登录标识
     */
    public String resolveAccount() {
        if (account != null && !account.trim().isEmpty()) {
            return account.trim();
        }
        return userid == null ? null : userid.trim();
    }
}
