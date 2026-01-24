package com.paradx.soul.controller;

import com.paradx.soul.pojo.Consultation;
import com.paradx.soul.service.ConsultationService;
import com.paradx.soul.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("consult")
@CrossOrigin
@Tag(name="预约管理",description = "提供心理咨询预约相关接口")
public class ConsultController {
    @Autowired
    ConsultationService consultationService;
    //查询预约（模糊查询）
    @GetMapping("checkOrder")
    @Operation(summary = "查询预约记录", description = "根据关键词模糊查询咨询预约信息")
    public Result getThisConsult(
            @Parameter(description = "查询关键词（可选）")
            @RequestParam(value = "keywords", required = false) String keywords){
        Result result = consultationService.getThisConsult(keywords);
        return result;
    }

    //添加预约
    @PostMapping("setOrder")
    @Operation(summary = "添加预约记录", description = "学生预约一条咨询")
    public Result setorder(
            @Parameter(description = "预约信息")
            @RequestBody Consultation consultation){
        Result result = consultationService.setNewOrder(consultation);
        return result;
    }

    //删除预约
    @PostMapping("delOrder")
    @Operation(summary = "删除预约记录", description = "管理员删除一条预约咨询记录")
    public Result delOrder(
            @Parameter(description = "目标记录的单号")
            @RequestBody Integer id){
        Result result =consultationService.delOrder(id);
        return  result;
    }

//    修改预约
    @PostMapping("changeInfo")
    @Operation(summary = "修改预约记录", description = "修改一条预约咨询记录")
    public Result changeInfo(
            @Parameter(description = "修改后的预约信息")
            @RequestBody Consultation consultation){
        Result result = consultationService.changeInfo(consultation);
        return result;
    }
}
