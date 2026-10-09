package com.paradx.soul.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paradx.soul.pojo.Consultation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.paradx.soul.pojo.Consultation
 */
@Mapper
public interface ConsultationMapper extends BaseMapper<Consultation> {

     List<Consultation> getConsult(String keywords);

    void insertNewOrder(Consultation consultation);

    void delConsult(Long id);

    void changeInfo(Consultation consultation);

    /** 查询指定区间内最大的预约单号（系统分配订单号用） */
    Long selectMaxOrderIdInRange(@Param("start") Long start, @Param("end") Long end);

    /** 删除与某用户相关的全部预约（学生本人预约 + 医生接诊记录） */
    int deleteByUser(@Param("userId") Long userId);
}




