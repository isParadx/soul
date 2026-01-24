package com.paradx.soul.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paradx.soul.pojo.Consultation;
import com.paradx.soul.utils.Result;

/**
 *
 */
public interface ConsultationService extends IService<Consultation> {
    Result getThisConsult(String keywords);
    Result setNewOrder(Consultation consultation);

    Result delOrder(Integer id);

    Result changeInfo(Consultation keywords);
}
