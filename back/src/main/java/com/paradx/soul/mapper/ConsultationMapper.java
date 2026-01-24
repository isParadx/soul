package com.paradx.soul.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paradx.soul.pojo.Consultation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.paradx.soul.pojo.Consultation
 */
@Mapper
public interface ConsultationMapper extends BaseMapper<Consultation> {

     List<Consultation> getConsult(String keywords);

    void insertNewOrder(Consultation consultation);

    void delConsult(Integer id);

    void changeInfo(Consultation consultation);
}




