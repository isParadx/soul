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
    public synchronized Result setNewOrder(Consultation consultation) {
        // 系统分配订单号：yyyyMMdd + 4位当日流水（12位长数字）
        // 并发下用 synchronized + 查重重试兜底
        for (int i = 0; i < 3; i++) {
            Long orderId = generateOrderId();
            if (consultationMapper.selectById(orderId) != null) {
                continue; // 极端并发下被占用，重新分配
            }
            consultation.setId(orderId);
            consultationMapper.insertNewOrder(consultation);
            return Result.ok(orderId);
        }
        return Result.build(null, 500, "订单号分配失败，请稍后重试");
    }

    /**
     * 生成预约订单号
     * 规则：yyyyMMdd（8位日期） + 4位当日流水，如 202610090001
     */
    private Long generateOrderId() {
        long datePart = Long.parseLong(
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE));
        long start = datePart * 10000L;
        long end = start + 9999L;
        Long max = consultationMapper.selectMaxOrderIdInRange(start, end);
        long next = (max == null) ? start + 1 : max + 1;
        if (next > end) {
            throw new IllegalStateException("今日订单号已达上限（9999），请次日再试");
        }
        return next;
    }

    @Override
    public Result getThisConsult(String keywords) {
        List<Consultation> consult = consultationMapper.getConsult(keywords);
        return Result.ok(consult);
    }

    @Override
    public Result delOrder(Long id) {
        consultationMapper.delConsult(id);
        return Result.ok(null);
    }

    @Override
    public Result changeInfo(Consultation consultation) {
        consultationMapper.changeInfo(consultation);
        return Result.ok(null);
    }

}




