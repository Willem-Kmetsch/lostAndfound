package com.design.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.design.common.lang.Result;
import com.design.entity.Found;
import com.design.entity.Lost;
import com.design.service.LostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import org.apache.commons.lang3.time.DateFormatUtils;

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
@RequestMapping("/lost")
public class LostController {

    @Autowired
    LostService lostService;

    // 获取失物列表
    // weiwan
    @GetMapping("/")
    public Result getFounds(Integer type,
                            @DateTimeFormat(pattern="yyyy-MM-dd") Date begintime,
                            @DateTimeFormat(pattern="yyyy-MM-dd") Date endtime,
                            String lostplace,
                            @RequestParam(defaultValue = "1") int page){
        LambdaQueryWrapper<Lost> queryWrapper = Wrappers.<Lost>query().lambda();
        if(type != null){
            queryWrapper.apply("type = " + type + "");
        }
        if(begintime != null){
            String strEnd= DateFormatUtils.format(begintime,"yyyy-MM-dd HH:mm:ss");
            queryWrapper.apply("UNIX_TIMESTAMP(losttime) >= UNIX_TIMESTAMP('" + strEnd + "')");
        }
        if(endtime != null){
            String start = DateFormatUtils.format(endtime,"yyyy-MM-dd HH:mm:ss");
            queryWrapper.apply("UNIX_TIMESTAMP(losttime) <= UNIX_TIMESTAMP('" + start + "')");
        }
        if(lostplace != null){
            queryWrapper.apply("lostplace = " + lostplace);
        }

        Page<Lost> p = new Page(page,10); // 每页大小为10， 取出第page页
        lostService.page(p);
        return Result.succ(p.getRecords());
    }


    // 根据类型查询
    @GetMapping("/type/{type}")
    public Result getFoundsByType(@PathVariable("type") int type,
                                  @RequestParam(defaultValue = "1") int page){
        QueryWrapper<Lost> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);

        Page<Lost> p = new Page(page,10);

        lostService.page(p,wrapper);
        return Result.succ(p.getRecords());
    }

    // 根据时间段查询
    @GetMapping("/losttime/{begintime}/{endtime}")
    public Result getFoundsByType(@DateTimeFormat(pattern="yyyy-MM-dd") @PathVariable("begintime") Date begintime,
                                  @DateTimeFormat(pattern="yyyy-MM-dd") @PathVariable("endtime") Date endtime,
                                  @RequestParam(defaultValue = "1") int page){
        LambdaQueryWrapper<Lost> queryWrapper = Wrappers.<Lost>query().lambda();

        String strEnd= DateFormatUtils.format(begintime,"yyyy-MM-dd HH:mm:ss");

        String start = DateFormatUtils.format(endtime,"yyyy-MM-dd HH:mm:ss");

        queryWrapper.apply("UNIX_TIMESTAMP(losttime) <= UNIX_TIMESTAMP('" + start + "')");

        queryWrapper.apply("UNIX_TIMESTAMP(losttime) >= UNIX_TIMESTAMP('" + strEnd + "')");


        Page<Lost> p = new Page(page,10);

        lostService.page(p,queryWrapper);
        return Result.succ(p.getRecords());
    }

    // 根据失物地点查询
    @GetMapping("/lostplace/{lostplace}")
    public Result getFoundsByLostPlace(@PathVariable("lostplace") String lostplace,
                                       @RequestParam(defaultValue = "1") int page){
        QueryWrapper<Lost> wrapper = new QueryWrapper<>();
        wrapper.eq("lostplace",lostplace);

        Page<Lost> p = new Page(page,10);

        lostService.page(p,wrapper);
        return Result.succ(p.getRecords());
    }

    // 根据失主查询
    @GetMapping("/ownerid/{ownerid}")
    public Result getFoundsByOwnerid(@PathVariable("ownerid") int ownerid){
        QueryWrapper<Lost> wrappers = new QueryWrapper<>();
        wrappers.eq("ownerid",ownerid);
        List<Lost> list = lostService.list(wrappers);
        return Result.succ(list);
    }


}
