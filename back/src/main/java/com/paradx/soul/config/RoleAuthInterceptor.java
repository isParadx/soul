package com.paradx.soul.config;

import com.paradx.soul.annotation.RequireRole;
import com.paradx.soul.utils.JwtHelper;
import com.paradx.soul.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

/**
 * 角色权限拦截器
 * 基于@RequireRole注解进行权限控制
 */
@Component
public class RoleAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理方法级别的处理器
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 检查是否有@RequireRole注解
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            // 检查类级别注解
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        
        // 没有权限注解，放行
        if (requireRole == null) {
            return true;
        }

        // 获取当前用户角色
        Integer userRole = (Integer) request.getAttribute("userRole");
        if (userRole == null) {
            // 尝试从token获取
            String token = getToken(request);
            if (token != null) {
                Long userId = jwtHelper.getUserId(token);
                if (userId != null) {
                    // 从数据库查询用户角色（这里简化处理，实际应从缓存或数据库获取）
                    // 由于拦截器无法直接注入Service，我们依赖JwtAuthInterceptor已设置的角色
                    writeError(response, 403, "无法获取用户角色信息");
                    return false;
                }
            }
            writeError(response, 401, "未登录或登录已过期");
            return false;
        }

        // 检查管理员专属权限
        if (requireRole.adminOnly() && userRole != 2) {
            writeError(response, 403, "需要管理员权限");
            return false;
        }

        // 检查角色是否在允许列表中
        int[] allowedRoles = requireRole.value();
        if (allowedRoles.length > 0) {
            boolean hasPermission = Arrays.stream(allowedRoles).anyMatch(role -> role == userRole);
            if (!hasPermission) {
                writeError(response, 403, "权限不足，需要角色: " + formatRoles(allowedRoles));
                return false;
            }
        }

        return true;
    }

    /**
     * 从请求中获取Token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }
        return token;
    }

    /**
     * 格式化角色数组为可读字符串
     */
    private String formatRoles(int[] roles) {
        return Arrays.stream(roles)
                .mapToObj(r -> switch (r) {
                    case 0 -> "学生";
                    case 1 -> "医生";
                    case 2 -> "管理员";
                    default -> "未知(" + r + ")";
                })
                .reduce((a, b) -> a + "/" + b)
                .orElse("");
    }

    /**
     * 写入错误响应
     */
    private void writeError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"code\":%d,\"message\":\"%s\",\"data\":null}", code, message));
    }
}
