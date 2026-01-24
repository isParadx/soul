package com.paradx.soul.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paradx.soul.pojo.Consultation;
import com.paradx.soul.service.ConsultationService;
import com.paradx.soul.mapper.ConsultationMapper;
import com.paradx.soul.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class ConsultationServiceImpl extends ServiceImpl<ConsultationMapper, Consultation>
    implements ConsultationService{

    @Autowired
    private ConsultationMapper consultationMapper;


    @Override
    public Result setNewOrder(Consultation consultation) {
        consultationMapper.insertNewOrder(consultation);
        return Result.ok(null);
    }

    @Override
    public Result getThisConsult(String keywords) {
        List<Consultation> consult = consultationMapper.getConsult(keywords);
        return Result.ok(consult);
    }

    @Override
    public Result delOrder(Integer id) {
        consultationMapper.delConsult(id);
        return Result.ok(null);
    }

    @Override
    public Result changeInfo(Consultation consultation) {
        consultationMapper.changeInfo(consultation);
        return Result.ok(null);
    }

}




