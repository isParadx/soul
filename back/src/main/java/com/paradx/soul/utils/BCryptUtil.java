package com.paradx.soul.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCrypt密码加密工具类
 * 替代原来的MD5加密，更安全
 */
public final class BCryptUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     * @param plainPassword 明文密码
     * @return 加密后的密码
     */
    public static String encrypt(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    /**
     * 验证密码
     * @param plainPassword 明文密码
     * @param encryptedPassword 密文密码
     * @return 是否匹配
     */
    public static boolean matches(String plainPassword, String encryptedPassword) {
        return encoder.matches(plainPassword, encryptedPassword);
    }
}
