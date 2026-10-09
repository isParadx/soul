package com.paradx.soul.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paradx.soul.mapper.TagMapper;
import com.paradx.soul.pojo.Tag;
import com.paradx.soul.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签控制器
 * 提供系统默认标签字典（医生擅长领域）
 */
@RestController
@RequestMapping("tag")
public class TagController {

    @Autowired
    private TagMapper tagMapper;

    /**
     * 系统默认标签列表（公开接口）
     */
    @GetMapping("list")
    public Result list() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Tag::getSortOrder).orderByAsc(Tag::getId);
        List<Tag> tags = tagMapper.selectList(wrapper);
        return Result.ok(tags);
    }
}
