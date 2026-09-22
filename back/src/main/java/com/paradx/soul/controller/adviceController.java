package com.paradx.soul.controller;

import com.paradx.soul.pojo.Advice;
import com.paradx.soul.service.AdviceService;
import com.paradx.soul.utils.Result;
import com.paradx.soul.utils.XssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 意见建议控制器
 * 处理匿名意见建议相关的HTTP请求
 */
@RestController
@RequestMapping("advice")
@Tag(name = "意见管理", description = "提供匿名意见建议相关接口")
public class adviceController {

    @Autowired
    private AdviceService adviceService;

    /**
     * 学生/医生提交意见接口
     */
    @PostMapping("putAdvice")
    @Operation(summary = "提交匿名建议", description = "用户提交匿名建议")
    public Result putadvice(
            @Parameter(description = "匿名建议内容")
            @RequestBody String advice) {
        // 参数校验
        if (advice == null || advice.trim().isEmpty()) {
            return Result.build(null, 400, "意见内容不能为空");
        }
        // 长度限制
        if (advice.length() > 500) {
            return Result.build(null, 400, "意见内容不能超过500字");
        }
        // XSS过滤
        advice = XssUtil.clean(advice);
        return adviceService.putadvice(advice);
    }

    /**
     * 管理员查询意见接口
     */
    @GetMapping("getAdvice")
    @Operation(summary = "查询匿名建议", description = "管理员查询所有匿名建议")
    public Result getadvice() {
        return adviceService.getadvice();
    }
}
