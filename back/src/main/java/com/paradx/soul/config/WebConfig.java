package com.paradx.soul.config;

import com.paradx.soul.filter.XssFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 * 配置拦截器、跨域、静态资源、安全过滤器等
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthInterceptor jwtAuthInterceptor;

    @Autowired
    private RoleAuthInterceptor roleAuthInterceptor;

    /**
     * 添加拦截器
     * JWT认证拦截器优先执行，角色权限拦截器其次
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT认证拦截器（第一层：验证用户身份）
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/regist",
                        "/doctor/getAllDocter",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/webjars/**",
                        "/error"
                );

        // 角色权限拦截器（第二层：验证用户权限）
        registry.addInterceptor(roleAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/regist",
                        "/doctor/getAllDocter",
                        "/user/getUserInfo",  // 所有登录用户可访问
                        "/user/changeUserInfo",
                        "/user/changePassword",
                        "/consult/checkOrder",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/webjars/**",
                        "/error"
                );
    }

    /**
     * 跨域配置
     * 安全加固：限制允许的来源，生产环境应配置具体域名
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:5173",   // Vite开发服务器
                    "http://localhost:8080",   // Spring Boot
                    "http://127.0.0.1:5173",
                    "http://127.0.0.1:8080"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 静态资源映射（用于头像等上传文件）
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    /**
     * 注册XSS过滤器
     * 全局自动过滤请求中的恶意脚本
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(1);  // 高优先级
        return registration;
    }
}
