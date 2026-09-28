package com.paradx.soul.filter;

import com.paradx.soul.utils.XssUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * XSS请求包装器
 * 自动过滤请求参数中的恶意脚本
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 不做XSS清理的请求头白名单
     * 这些头具有固定格式（如URL、MIME类型），转义会破坏其语义，
     * 且它们由浏览器/客户端生成，不直接用于页面渲染，无XSS风险
     */
    private static final java.util.Set<String> SKIP_CLEAN_HEADERS = java.util.Set.of(
            "origin",           // CORS来源，转义会导致Spring CORS解析失败
            "referer",          // 来源页面URL
            "host",             // 主机头
            "content-type",     // MIME类型
            "user-agent",       // 浏览器标识（设备检测依赖）
            "accept",           // 内容协商
            "x-device-type",    // 设备类型标识
            "authorization",    // 认证令牌
            "token"             // 认证令牌
    );

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }
        // 对参数值进行XSS清理
        return XssUtil.clean(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        // 对每个参数值进行XSS清理
        String[] cleanValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleanValues[i] = XssUtil.clean(values[i]);
        }
        return cleanValues;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        // 结构性/认证类请求头不做清理，避免破坏Origin、Token等格式
        if (name != null && SKIP_CLEAN_HEADERS.contains(name.toLowerCase())) {
            return value;
        }
        return XssUtil.clean(value);
    }
}
