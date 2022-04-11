package com.design.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.design.common.lang.Result;
import com.design.entity.ScoreOperation;
import com.design.entity.Signin;
import com.design.entity.User;
import com.design.service.ScoreOperationService;
import com.design.service.SigninService;
import com.design.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/signin")
public class SigninController {
    @Autowired
    SigninService signinService;

    @Autowired
    UserService userService;

    @Autowired
    ScoreOperationService scoreOperationService;

    @GetMapping("/")
    public Result signin(@RequestParam("userId") Integer userId){
        LocalDate now = LocalDate.now();
        QueryWrapper<Signin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid",userId);
        queryWrapper.eq("time",now);
        List<Signin> list  = signinService.list(queryWrapper);
        // 签到
        if(list.isEmpty() || list.size() == 0){
            // 签到表
            Signin signin = new Signin();
            signin.setUserid(userId);
            signin.setTime(LocalDateTime.of(now, LocalTime.of(0,0)));
            signinService.save(signin);
            // 用户表
            User user = userService.getById(userId);
            UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
            userUpdateWrapper.eq("id",userId);
            userUpdateWrapper.set("score",user.getScore() + 1);
            userService.update(userUpdateWrapper);

            // 积分表
            user = userService.getById(userId);
            ScoreOperation s1 = new ScoreOperation();
            s1.setUserid(userId);
            s1.setOperationtime(LocalDateTime.now());
            s1.setOperationtype(1);
            s1.setOperationscore(1);
            s1.setRemainscore(user.getScore());
            scoreOperationService.save(s1);

            return Result.succ("签到成功，积分+1");

        }
        //已签到
        return Result.succ("今日已签到!");
    }
}
