package com.design.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.design.common.lang.Result;
import com.design.entity.Found;
import com.design.entity.Lost;
import com.design.service.FoundService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author god
 * @since 2022-02-25
 */
@RestController
@RequestMapping("/found")
public class FoundController {
    @Autowired
    FoundService foundService;

    @PostMapping("/aaa")
    public Result aaa(){
        String a = "hello";
        System.out.println(a);
        return Result.succ(a);
    }

    // 获取招领列表
    @GetMapping("/")
    public Result getFounds(@RequestParam(defaultValue = "1") int page){
        Page<Found> p = new Page(page,4); // 每页大小为4， 取出第page页
        foundService.page(p);
        System.out.println(Result.succ(p.getRecords()));
        return Result.succ(p.getRecords());
    }

    // 根据类型查询
    @GetMapping("/type/{type}")
    public Result getFoundsByType(@PathVariable("type") int type,
                                  @RequestParam(defaultValue = "1") int page){
        QueryWrapper<Found> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);

        Page<Found> p = new Page(page,10);

        foundService.page(p,wrapper);
        return Result.succ(p.getRecords());
    }

    // 根据时间段查询
    @GetMapping("/losttime/{begintime}/{endtime}")
    public Result getFoundsByType(@DateTimeFormat(pattern="yyyy-MM-dd") @PathVariable("begintime") Date begintime,
                                  @DateTimeFormat(pattern="yyyy-MM-dd") @PathVariable("endtime") Date endtime,
                                  @RequestParam(defaultValue = "1") int page){
        LambdaQueryWrapper<Found> queryWrapper = Wrappers.<Found>query().lambda();

        String strEnd= DateFormatUtils.format(begintime,"yyyy-MM-dd HH:mm:ss");

        String start = DateFormatUtils.format(endtime,"yyyy-MM-dd HH:mm:ss");

        queryWrapper.apply("UNIX_TIMESTAMP(losttime) <= UNIX_TIMESTAMP('" + start + "')");

        queryWrapper.apply("UNIX_TIMESTAMP(losttime) >= UNIX_TIMESTAMP('" + strEnd + "')");


        Page<Found> p = new Page(page,10);

        foundService.page(p,queryWrapper);
        return Result.succ(p.getRecords());
    }
}
