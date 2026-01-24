package com.paradx.soul.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paradx.soul.pojo.Advice;
import com.paradx.soul.utils.Result;
import org.apache.ibatis.annotations.Param;

/**
 *
 */
public interface AdviceService extends IService<Advice> {

    Result putadvice(String advice);

    Result getadvice();
}
