package com.paradx.soul.controller;

import com.paradx.soul.pojo.Doctor;
import com.paradx.soul.service.DoctorService;
import com.paradx.soul.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("doctor")
@CrossOrigin
public class DoctorController {
    @Autowired
    DoctorService doctorService;
    /**
     * 查询所有医师/搜索功能实现（首页+详细信息）
     * @return
     */
    @GetMapping("getAllDocter")
    public Result getAllDocter(@RequestParam(value = "keywords", required = false) String keywords){
        Result result = doctorService.getAllDocter(keywords);
        return result;
    }

    /**
     *
     *  添加医生信息接口
     *  @return
     */
    @PostMapping("addDoctorInfo")
    public Result addDoctorInfo(@RequestBody Doctor doctor){
        Result result = doctorService.addDoctor(doctor);
        return result;
    }

    /**
     *
     *  修改医生信息接口
     *  @return
     */
    @PostMapping("changeDoc")
    public Result change(@RequestBody Doctor doctor){
        Result result =doctorService.changeDoctor(doctor);
        return  result;
    }

    /**
     *
     *  删除医生信息接口
     *  @return
     */
    @PostMapping("delDocter")
    public Result delDoc(@RequestBody Long id){
        Result result =doctorService.delDoc(id);
        return  result;
    }


}