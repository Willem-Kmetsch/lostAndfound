package com.design.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.design.common.lang.Result;
import com.design.entity.FoundFinish;
import com.design.entity.LostFinish;
import com.design.entity.User;
import com.design.service.FoundFinishService;
import com.design.service.LostFinishService;
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
@RequestMapping("/lost-finish")
public class LostFinishController {
    @Autowired
    LostFinishService service;

    @Autowired
    UserService userService;

    // 已完成
    @GetMapping("/getFinishedLosts")
    public Result getFinishedFounds(@RequestParam("userId") Integer userId){
        QueryWrapper<LostFinish> wrapper = new QueryWrapper<>();
        wrapper.eq("ownerid",userId);
        List<LostFinish> list = service.list(wrapper);
        List<String> usernames = new ArrayList<>();
        for(LostFinish lostFinish : list){
            User user = userService.getById(lostFinish.getFounderid());
            usernames.add(user.getUsername());
        }
        return Result.succ(MapUtil.builder()
                .put("losts",list)
                .put("username",usernames)
                .map());
    }

}
