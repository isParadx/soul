package com.paradx.soul.controller;

import com.paradx.soul.annotation.RequireRole;
import com.paradx.soul.pojo.Consultation;
import com.paradx.soul.service.ConsultationService;
import com.paradx.soul.utils.Result;
import com.paradx.soul.utils.XssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 预约管理控制器
 * 处理心理咨询预约相关的HTTP请求
 */
@RestController
@RequestMapping("consult")
@Tag(name = "预约管理", description = "提供心理咨询预约相关接口")
public class ConsultController {

    @Autowired
    private ConsultationService consultationService;

    /**
     * 查询预约（模糊查询）
     * 权限：医生和管理员可访问
     */
    @RequireRole(value = {1, 2})
    @GetMapping("checkOrder")
    @Operation(summary = "查询预约记录", description = "根据关键词模糊查询咨询预约信息")
    public Result getThisConsult(
            @Parameter(description = "查询关键词（可选）")
            @RequestParam(value = "keywords", required = false) String keywords) {
        // XSS过滤
        if (keywords != null && !keywords.isEmpty()) {
            keywords = XssUtil.clean(keywords);
            if (keywords.length() > 50) {
                keywords = keywords.substring(0, 50);
            }
        }
        return consultationService.getThisConsult(keywords);
    }

    /**
     * 添加预约
     */
    @PostMapping("setOrder")
    @Operation(summary = "添加预约记录", description = "学生预约一条咨询")
    public Result setorder(
            @Parameter(description = "预约信息")
            @RequestBody Consultation consultation) {
        // 参数校验
        if (consultation.getStuId() == null || consultation.getDocId() == null) {
            return Result.build(null, 400, "学生ID和医生ID不能为空");
        }
        if (consultation.getAppointmentTime() == null || consultation.getAppointmentTime().isEmpty()) {
            return Result.build(null, 400, "预约时间不能为空");
        }
        // XSS过滤
        if (consultation.getFeedback() != null) {
            consultation.setFeedback(XssUtil.clean(consultation.getFeedback()));
        }
        return consultationService.setNewOrder(consultation);
    }

    /**
     * 删除预约（管理员）
     * 权限：仅管理员可访问
     */
    @RequireRole(adminOnly = true)
    @PostMapping("delOrder")
    @Operation(summary = "删除预约记录", description = "管理员删除一条预约咨询记录")
    public Result delOrder(
            @Parameter(description = "目标记录的单号")
            @RequestBody Integer id) {
        if (id == null) {
            return Result.build(null, 400, "预约ID不能为空");
        }
        return consultationService.delOrder(id);
    }

    /**
     * 修改预约（医生和管理员）
     * 权限：医生和管理员可访问
     */
    @RequireRole(value = {1, 2})
    @PostMapping("changeInfo")
    @Operation(summary = "修改预约记录", description = "修改一条预约咨询记录")
    public Result changeInfo(
            @Parameter(description = "修改后的预约信息")
            @RequestBody Consultation consultation) {
        // 参数校验
        if (consultation.getId() == null) {
            return Result.build(null, 400, "预约ID不能为空");
        }
        // XSS过滤
        if (consultation.getFeedback() != null) {
            consultation.setFeedback(XssUtil.clean(consultation.getFeedback()));
        }
        return consultationService.changeInfo(consultation);
    }
}
