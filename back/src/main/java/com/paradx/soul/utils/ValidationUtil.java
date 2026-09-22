package com.paradx.soul.utils;

import java.util.regex.Pattern;

/**
 * 输入验证工具类
 * 用于验证用户输入的合法性
 */
public final class ValidationUtil {

    private ValidationUtil() {}

    /** 手机号正则 */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    /** 邮箱正则 */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    /** 用户ID（学号/工号）正则 - 纯数字 */
    private static final Pattern USER_ID_PATTERN = Pattern.compile("^\\d{5,15}$");
    
    /** 昵称正则 - 2-20个字符，允许中英文、数字、下划线 */
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9_]{2,20}$");

    /**
     * 验证手机号
     * @param phone 手机号
     * @return 是否合法
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证邮箱
     * @param email 邮箱
     * @return 是否合法
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证用户ID
     * @param userId 用户ID
     * @return 是否合法
     */
    public static boolean isValidUserId(Long userId) {
        return userId != null && USER_ID_PATTERN.matcher(userId.toString()).matches();
    }

    /**
     * 验证昵称
     * @param nickname 昵称
     * @return 是否合法
     */
    public static boolean isValidNickname(String nickname) {
        return nickname != null && NICKNAME_PATTERN.matcher(nickname).matches();
    }

    /**
     * 验证密码强度（6-20位，至少包含字母和数字）
     * @param password 密码
     * @return 是否合法
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6 || password.length() > 20) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }

    /**
     * 检查字符串是否为空或空白
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 安全获取字符串长度（防止超长攻击）
     * @param str 字符串
     * @param maxLength 最大长度
     * @return 是否在安全范围内
     */
    public static boolean isLengthSafe(String str, int maxLength) {
        return str != null && str.length() <= maxLength;
    }
}
