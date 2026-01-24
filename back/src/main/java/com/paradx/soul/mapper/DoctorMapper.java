package com.paradx.soul.mapper;

import com.paradx.soul.pojo.Doctor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.paradx.soul.pojo.Doctor
 */
@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
    List<Doctor> getAllDoctor(String keywords);

    void addDoctorInfo(@Param("doctor") Doctor doctor);

    void deldoc(Long id);

    void ChangeDoctor(Doctor doctor);
}




