package com.paradx.soul.filter;

import com.paradx.soul.utils.XssUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * XSS请求包装器
 * 自动过滤请求参数中的恶意脚本
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

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
        // 对请求头进行XSS清理（排除Authorization等特殊头）
        if ("authorization".equalsIgnoreCase(name) || "token".equalsIgnoreCase(name)) {
            return value;  // Token不做处理，否则会导致认证失败
        }
        return XssUtil.clean(value);
    }
}
