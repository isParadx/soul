package com.paradx.soul.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * @TableName consultation
 */
@TableName(value ="consultation")
@Data
public class Consultation implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "预约单号")
    private Integer id;
    @Schema(description = "医生ID")
    private Long docId;
    @Schema(description = "学生ID")
    private Long stuId;

    @Schema(description = "预约时间")
    private String appointmentTime;

    @Schema(description = "预约状态")
    private String status="未开始";

    @Schema(description = "反馈信息")
    private String feedback;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public Consultation() {
    }

    public Consultation(Integer id, Long docId, Long stuId, String appointmentTime, String status, String feedback) {
        this.id = id;
        this.docId = docId;
        this.stuId = stuId;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.feedback = feedback;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public Long getStuId() {
        return stuId;
    }

    public void setStuId(Long stuId) {
        this.stuId = stuId;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}