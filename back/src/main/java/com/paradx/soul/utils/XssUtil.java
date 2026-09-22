package com.paradx.soul.utils;

import java.util.regex.Pattern;

/**
 * XSS防护工具类
 * 用于过滤用户输入中的恶意脚本
 */
public final class XssUtil {

    private XssUtil() {}

    /**
     * 危险的HTML标签和属性正则
     */
    private static final Pattern[] XSS_PATTERNS = {
            // 脚本标签
            Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE),
            // 事件属性
            Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE),
            // javascript:协议
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            // 表达式
            Pattern.compile("expression\\s*\\(", Pattern.CASE_INSENSITIVE),
            // iframe
            Pattern.compile("<iframe[^>]*>.*?</iframe>", Pattern.CASE_INSENSITIVE),
            // object/embed/applet
            Pattern.compile("<(object|embed|applet)[^>]*>", Pattern.CASE_INSENSITIVE),
            // style中的expression
            Pattern.compile("style\\s*=\\s*['\"]?[^']*expression", Pattern.CASE_INSENSITIVE),
            // vbscript
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE)
    };

    /**
     * 清理XSS攻击字符串
     * @param value 待清理的字符串
     * @return 清理后的安全字符串
     */
    public static String clean(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // 移除危险内容
        String cleanValue = value;
        for (Pattern pattern : XSS_PATTERNS) {
            cleanValue = pattern.matcher(cleanValue).replaceAll("");
        }

        // HTML转义
        cleanValue = htmlEncode(cleanValue);

        return cleanValue.trim();
    }

    /**
     * HTML特殊字符转义
     * @param input 输入字符串
     * @return 转义后的字符串
     */
    public static String htmlEncode(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    /**
     * 检查字符串是否包含XSS攻击特征
     * @param value 待检查的字符串
     * @return true-包含XSS攻击，false-安全
     */
    public static boolean containsXss(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        
        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(value).find()) {
                return true;
            }
        }
        return false;
    }
}
