package com.paradx.soul.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统默认标签（医生擅长领域可选标签）
 */
@TableName(value = "tag")
@Data
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 标签ID */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 标签名称 */
    private String name;

    /** 排序 */
    private Integer sortOrder;
}
