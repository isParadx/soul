package com.paradx.soul.controller;

import com.paradx.soul.annotation.RequireRole;
import com.paradx.soul.pojo.Doctor;
import com.paradx.soul.service.DoctorService;
import com.paradx.soul.utils.Result;
import com.paradx.soul.utils.XssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 医生控制器
 * 处理医生/咨询师相关的HTTP请求
 */
@RestController
@RequestMapping("doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    /**
     * 查询所有医师/搜索功能（公开接口）
     */
    @GetMapping("getAllDocter")
    public Result getAllDocter(@RequestParam(value = "keywords", required = false) String keywords) {
        // XSS过滤
        if (keywords != null && !keywords.isEmpty()) {
            keywords = XssUtil.clean(keywords);
            if (keywords.length() > 50) {
                keywords = keywords.substring(0, 50);
            }
        }
        return doctorService.getAllDocter(keywords);
    }

    /**
     * 添加医生信息（管理员）
     * 权限：仅管理员可访问
     */
    @RequireRole(adminOnly = true)
    @PostMapping("addDoctorInfo")
    public Result addDoctorInfo(@RequestBody Doctor doctor) {
        // 参数校验
        if (doctor.getId() == null || doctor.getName() == null || doctor.getName().isEmpty()) {
            return Result.build(null, 400, "医生ID和姓名不能为空");
        }
        // XSS过滤
        doctor.setName(XssUtil.clean(doctor.getName()));
        if (doctor.getIntruduce() != null) {
            doctor.setIntruduce(XssUtil.clean(doctor.getIntruduce()));
        }
        if (doctor.getSay() != null) {
            doctor.setSay(XssUtil.clean(doctor.getSay()));
        }
        return doctorService.addDoctor(doctor);
    }

    /**
     * 修改医生信息（管理员）
     * 权限：仅管理员可访问
     */
    @RequireRole(adminOnly = true)
    @PostMapping("changeDoc")
    public Result change(@RequestBody Doctor doctor) {
        // 参数校验
        if (doctor.getId() == null) {
            return Result.build(null, 400, "医生ID不能为空");
        }
        // XSS过滤
        if (doctor.getName() != null) {
            doctor.setName(XssUtil.clean(doctor.getName()));
        }
        if (doctor.getIntruduce() != null) {
            doctor.setIntruduce(XssUtil.clean(doctor.getIntruduce()));
        }
        return doctorService.changeDoctor(doctor);
    }

    /**
     * 删除医生信息（管理员）
     * 权限：仅管理员可访问
     */
    @RequireRole(adminOnly = true)
    @PostMapping("delDocter")
    public Result delDoc(@RequestBody Long id) {
        if (id == null) {
            return Result.build(null, 400, "医生ID不能为空");
        }
        return doctorService.delDoc(id);
    }
}
