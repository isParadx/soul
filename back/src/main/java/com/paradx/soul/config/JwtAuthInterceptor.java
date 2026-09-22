package com.paradx.soul.config;

import com.paradx.soul.utils.JwtHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT认证拦截器
 * 用于验证用户身份
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取Token（从header或parameter中）
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }

        // 验证Token
        if (token != null && !token.isEmpty() && jwtHelper.validateToken(token)) {
            Long userId = jwtHelper.getUserId(token);
            if (userId != null) {
                // 将用户ID存入request属性，供后续使用
                request.setAttribute("userId", userId);
                return true;
            }
        }

        // 未登录或Token无效
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":504,\"message\":\"未登录或登录已过期\",\"data\":null}");
        return false;
    }
}
