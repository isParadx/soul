package com.paradx.soul.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paradx.soul.pojo.Doctor;
import com.paradx.soul.utils.Result;

/**
 *
 */
public interface DoctorService extends IService<Doctor> {
    Result getAllDocter(String keywords);

    Result addDoctor(Doctor doctor);

    Result delDoc(Long id);

    Result changeDoctor(Doctor doctor);
}
