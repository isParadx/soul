package com.paradx.soul.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paradx.soul.pojo.Advice;
import com.paradx.soul.service.AdviceService;
import com.paradx.soul.mapper.AdviceMapper;
import com.paradx.soul.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 *
 */
@Service
public class AdviceServiceImpl extends ServiceImpl<AdviceMapper, Advice>
    implements AdviceService{
    @Autowired
    private  AdviceMapper adviceMapper;
    @Override
    public Result putadvice(String advice) {
        Date nowtime=new Date();
        int rows = adviceMapper.insertadvice(advice, nowtime);
        return Result.ok(rows);
    }

    @Override
    public Result getadvice() {
      List<Advice> list= adviceMapper.getadvice();
        return Result.ok(list);
    }
}




