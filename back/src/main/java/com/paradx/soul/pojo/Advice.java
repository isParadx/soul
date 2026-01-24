package com.paradx.soul.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * @TableName advice
 */
@TableName(value ="advice")
@Data
public class Advice implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "匿名意见序号")
    private Integer id;
    @Schema(description = "匿名意见内容")
    private String context;
    @Schema(description = "匿名意见提交时间")
    private Date time;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public Advice() {
    }

    public Advice(Integer id, String context, Date time) {
        this.id = id;
        this.context = context;
        this.time = time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}