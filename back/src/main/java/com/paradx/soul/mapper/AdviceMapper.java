package com.paradx.soul.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paradx.soul.pojo.Advice;

import java.util.Date;
import java.util.List;

/**
 * @Entity com.paradx.soul.pojo.Advice
 */
public interface AdviceMapper extends BaseMapper<Advice> {

    int insertadvice(String advice, Date nowtime);

    List<Advice> getadvice();
}




