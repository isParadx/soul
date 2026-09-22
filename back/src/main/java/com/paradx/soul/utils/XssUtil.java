package com.paradx.soul.utils;

import java.util.regex.Pattern;

/**
 * XSS防护工具类
 * 用于过滤用户输入中的恶意脚本
 * 安全加固：增加更多XSS攻击模式检测
 */
public final class XssUtil {

    private XssUtil() {}

    /**
     * 危险的HTML标签和属性正则（增强版）
     */
    private static final Pattern[] XSS_PATTERNS = {
            // 脚本标签
            Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // 事件属性（onclick, onerror, onload等）
            Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE),
            // javascript:协议
            Pattern.compile("javascript\\s*:", Pattern.CASE_INSENSITIVE),
            // vbscript:协议
            Pattern.compile("vbscript\\s*:", Pattern.CASE_INSENSITIVE),
            // expression() CSS注入
            Pattern.compile("expression\\s*\\(", Pattern.CASE_INSENSITIVE),
            // url() JavaScript注入
            Pattern.compile("url\\s*\\(\\s*['\"]?\\s*javascript:", Pattern.CASE_INSENSITIVE),
            // iframe
            Pattern.compile("<iframe[^>]*>.*?</iframe>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // object/embed/applet
            Pattern.compile("<(object|embed|applet)[^>]*>", Pattern.CASE_INSENSITIVE),
            // form标签
            Pattern.compile("<form[^>]*>", Pattern.CASE_INSENSITIVE),
            // SVG脚本注入
            Pattern.compile("<svg[^>]*>.*?</svg>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            // Data URI
            Pattern.compile("data:\\s*text/html", Pattern.CASE_INSENSITIVE),
            // style中的expression
            Pattern.compile("style\\s*=\\s*['\"]?[^']*expression", Pattern.CASE_INSENSITIVE),
            // HTML注释（可能包含脚本）
            Pattern.compile("<!--.*?-->", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
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

        String cleanValue = value;
        for (Pattern pattern : XSS_PATTERNS) {
            cleanValue = pattern.matcher(cleanValue).replaceAll("");
        }
        
        // HTML转义特殊字符
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
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
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
