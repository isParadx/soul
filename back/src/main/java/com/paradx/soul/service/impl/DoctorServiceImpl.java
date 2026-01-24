package com.paradx.soul.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paradx.soul.pojo.Doctor;
import com.paradx.soul.service.DoctorService;
import com.paradx.soul.mapper.DoctorMapper;
import com.paradx.soul.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor>
    implements DoctorService{
    @Autowired
    private DoctorMapper doctorMapper;
    @Override
    public Result getAllDocter(String keywords){
        List<Doctor> doc=doctorMapper.getAllDoctor(keywords);
        return Result.ok(doc);
    }

    @Override
    public Result addDoctor(Doctor doctor) {
        doctorMapper.addDoctorInfo(doctor);
        return Result.ok(0);
    }

    @Override
    public Result delDoc(Long id) {
        doctorMapper.deldoc(id);
        return Result.ok(0);
    }

    @Override
    public Result changeDoctor(Doctor doctor) {
        doctorMapper.ChangeDoctor(doctor);
        return Result.ok(0);
    }
}





