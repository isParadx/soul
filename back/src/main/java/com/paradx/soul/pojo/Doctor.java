package com.paradx.soul.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName doctor
 */
@TableName(value ="doctor")
@Data
public class Doctor implements Serializable {
    @TableId
    private Long id;
    private String intruduce;
    private String say;
    private String name;
    private String type;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public Doctor() {
    }

    public Doctor(Long id, String intruduce,String say, String name, String type) {
        this.id = id;
        this.intruduce = intruduce;
        this.say=say;
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSay() {
        return say;
    }

    public void setSay(String say) {
        this.say = say;
    }

    public String getIntruduce() {
        return intruduce;
    }

    public void setIntruduce(String intruduce) {
        this.intruduce = intruduce;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}