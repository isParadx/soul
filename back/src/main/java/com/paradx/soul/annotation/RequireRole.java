package com.paradx.soul.annotation;

import java.lang.annotation.*;

/**
 * 角色权限注解
 * 标注在Controller方法上，限制只有特定角色的用户才能访问
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /**
     * 允许访问的角色列表
     * 0-学生, 1-医生, 2-管理员
     */
    int[] value() default {};

    /**
     * 是否需要管理员权限（快捷方式）
     */
    boolean adminOnly() default false;
}
