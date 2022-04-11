package com.design.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.design.common.lang.Result;
import com.design.entity.Found;
import com.design.entity.FoundFinish;
import com.design.entity.User;
import com.design.service.FoundFinishService;
import com.design.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
@RequestMapping("/found-finish")
public class FoundFinishController {
    @Autowired
    FoundFinishService service;

    @Autowired
    UserService userService;

    // 已完成
    @GetMapping("/getFinishedFounds")
    public Result getFinishedFounds(@RequestParam("userId") Integer userId){
        QueryWrapper<FoundFinish> wrapper = new QueryWrapper<>();
        wrapper.eq("founderid",userId);
        List<FoundFinish> list = service.list(wrapper);
        List<String> usernames = new ArrayList<>();
        for(FoundFinish foundFinish : list){
            User user = userService.getById(foundFinish.getOwnerid());
            usernames.add(user.getUsername());
        }
        return Result.succ(MapUtil.builder()
                .put("founds",list)
                .put("username",usernames)
                .map());
    }

}
