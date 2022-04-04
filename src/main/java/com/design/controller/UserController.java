package com.design.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.design.common.dto.LoginDto;
import com.design.common.lang.Result;
import com.design.entity.Admin;
import com.design.entity.User;
import com.design.service.UserService;
import com.design.util.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author god
 * @since 2022-02-25
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/{id}")
    public Result test(@PathVariable("id") int id){
        return Result.succ(userService.getById(id));
    }

//    // 登录接口
//    @PostMapping("/login")
//    public Result login(@RequestBody LoginDto loginDto){
//        User user = userService.getOne(new QueryWrapper<User>().eq("username",loginDto.getUsername()));
//        Assert.notNull(user, "用户不存在");
//        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))){
//            return Result.fail("密码错误");
//        }
//        return Result.succ(user);
//    }

    // 注册接口
    public Result register(@RequestBody User user){
        Boolean flag = userService.save(user);
        if(!flag){
            return Result.fail("注册失败!");
        }
        return Result.succ("注册成功！",null);
    }


    @CrossOrigin
    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto loginDto,
                        HttpServletResponse response){
        User user = userService.getOne(new QueryWrapper<User>().eq("username",loginDto.getUsername()));
        Assert.notNull(user, "用户不存在");
//        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))){
        if(!user.getPassword().equals(loginDto.getPassword())){
            return Result.fail("密码错误");
        }
        String jwt = jwtUtils.generateToken(user.getId());
        response.setHeader("Authorization",jwt);
        response.setHeader("Access-Control-Expose-Headers","Authorization");

        System.out.println("login success!");
        return Result.succ(MapUtil.builder()
                .put("id",user.getId())
                .put("username",user.getUsername())
                .map());
    }



    @GetMapping("/logout")
    @RequiresAuthentication //登录之后
    public Result logout(){
        SecurityUtils.getSubject().logout();
        return Result.succ(null);
    }

    @PostMapping("/save")
    public Result save(@RequestBody User user){
        return Result.succ(user);
    }

}
