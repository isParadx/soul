package com.paradx.soul.controller;

import com.paradx.soul.pojo.Advice;

import com.paradx.soul.service.AdviceService;
import com.paradx.soul.utils.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("advice")
@CrossOrigin
@Tag(name="意见管理",description = "提供匿名意见建议相关接口")
public class adviceController {

    @Autowired
    private AdviceService adviceService;

    /**学生/医生提交意见接口*/
    @PostMapping("putAdvice")
    @Operation(summary = "学生提交匿名建议", description = "学生提交匿名建议")
    public Result putadvice(
            @Parameter(description = "学生匿名建议内容")
            @RequestBody String advice){

        Result result = adviceService.putadvice(advice);
        return result;
    }
    /**管理员查询意见接口*/
    @GetMapping("getAdvice")
    @Operation(summary = "管理员查询匿名建议", description = "管理员查询匿名建议")
    public Result getadvice(){
        Result result = adviceService.getadvice();
        return result;
    }
}
