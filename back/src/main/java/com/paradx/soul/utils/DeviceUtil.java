package com.paradx.soul.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 设备检测工具类
 * 用于识别客户端设备类型（PC/移动端）
 */
public final class DeviceUtil {

    private DeviceUtil() {}

    // 移动端User-Agent关键词
    private static final String MOBILE_AGENT_REGEX = 
        "(?i)(android|iphone|ipad|ipod|windows\\s+phone|mobile|blackberry|webos|opera\\s*(mini|mobi)|iemobile)";

    private static final Pattern MOBILE_PATTERN = Pattern.compile(MOBILE_AGENT_REGEX);

    // 平板设备关键词（可归类为移动端或单独处理）
    private static final String TABLET_AGENT_REGEX = 
        "(?i)(ipad|tablet|playbook|silk|(android(?!.*mobile)))";

    private static final Pattern TABLET_PATTERN = Pattern.compile(TABLET_AGENT_REGEX);

    /**
     * 设备类型枚举
     */
    public enum DeviceType {
        PC("pc"),           // PC端（桌面浏览器）
        MOBILE("mobile");   // 移动端（手机/平板）

        // 移动端别名胜识别码（如uni-app客户端使用"app"标识）
        private static final java.util.Set<String> MOBILE_ALIASES =
            java.util.Set.of("app", "android", "ios", "mobile");

        private final String code;

        DeviceType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static DeviceType fromCode(String code) {
            if (code == null) return PC;
            for (DeviceType type : values()) {
                if (type.code.equalsIgnoreCase(code)) {
                    return type;
                }
            }
            // 移动端别名识别（app/android/ios等）
            if (MOBILE_ALIASES.contains(code.toLowerCase())) {
                return MOBILE;
            }
            return PC;
        }
    }

    /**
     * 从HttpServletRequest检测设备类型
     * @param request HTTP请求对象
     * @return 设备类型
     */
    public static DeviceType detectDevice(HttpServletRequest request) {
        if (request == null) {
            return DeviceType.PC;
        }

        String userAgent = request.getHeader("User-Agent");
        
        // 优先检查请求头中的自定义设备标识
        String deviceHeader = request.getHeader("X-Device-Type");
        if (deviceHeader != null && !deviceHeader.isEmpty()) {
            return DeviceType.fromCode(deviceHeader);
        }

        // 通过User-Agent判断
        return detectFromUserAgent(userAgent);
    }

    /**
     * 从User-Agent字符串检测设备类型
     * @param userAgent User-Agent字符串
     * @return 设备类型
     */
    public static DeviceType detectFromUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return DeviceType.PC;
        }

        // 检测是否为移动设备
        Matcher mobileMatcher = MOBILE_PATTERN.matcher(userAgent);
        if (mobileMatcher.find()) {
            return DeviceType.MOBILE;
        }

        return DeviceType.PC;
    }

    /**
     * 检查是否为移动端设备
     * @param request HTTP请求对象
     * @return true-移动端，false-PC端
     */
    public static boolean isMobile(HttpServletRequest request) {
        return detectDevice(request) == DeviceType.MOBILE;
    }

    /**
     * 检查是否为PC端设备
     * @param request HTTP请求对象
     * @return true-PC端，false-移动端
     */
    public static boolean isPC(HttpServletRequest request) {
        return detectDevice(request) == DeviceType.PC;
    }

    /**
     * 验证用户角色是否允许在指定设备上登录
     * @param role 用户角色（0-学生, 1-医生, 2-管理员）
     * @param deviceType 设备类型
     * @return true-允许登录，false-不允许
     */
    public static boolean isRoleAllowedOnDevice(Integer role, DeviceType deviceType) {
        if (role == null || deviceType == null) {
            return false;
        }

        switch (role) {
            case 0: // 学生 - 仅允许移动端
                return deviceType == DeviceType.MOBILE;
            case 1: // 医生 - 允许PC端
                return deviceType == DeviceType.PC;
            case 2: // 管理员 - 允许PC端
                return deviceType == DeviceType.PC;
            default:
                return false;
        }
    }

    /**
     * 获取设备的友好名称
     * @param deviceType 设备类型
     * @return 友好名称
     */
    public static String getDeviceName(DeviceType deviceType) {
        if (deviceType == null) return "未知";
        switch (deviceType) {
            case PC: return "电脑端";
            case MOBILE: return "移动端";
            default: return "未知";
        }
    }

    /**
     * 获取角色允许的设备类型描述
     * @param role 用户角色
     * @return 允许的设备描述
     */
    public static String getAllowedDeviceDescription(Integer role) {
        if (role == null) return "";
        switch (role) {
            case 0: return "学生用户请使用手机端（App）登录";
            case 1: return "医生用户请使用电脑端（Web）登录";
            case 2: return "管理员请使用电脑端（Web）登录";
            default: return "";
        }
    }
}
