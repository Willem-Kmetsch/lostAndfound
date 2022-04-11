package com.design.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.design.common.dto.LoginDto;
import com.design.common.lang.Result;
import com.design.entity.Admin;
import com.design.entity.User;
import com.design.service.AdminService;
import com.design.service.UserService;
//import com.design.shiro.JwtFilter;
//import com.design.util.JwtUtils;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authz.annotation.RequiresAuthentication;
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
 * @since 2022-02-21
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AdminService adminService;



    // 登录接口
    @CrossOrigin
    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto loginDto,
                        HttpServletResponse response){
        Admin admin = adminService.getOne(new QueryWrapper<Admin>().eq("username",loginDto.getUsername()));
        Assert.notNull(admin, "用户不存在");
        //        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))){
        if(!admin.getPassword().equals(loginDto.getPassword())){
            return Result.fail("密码错误");
        }
        String jwt = jwtUtils.generateToken(admin.getId());
        response.setHeader("Authorization",jwt);
        response.setHeader("Access-Control-Expose-Headers","Authorization");
        System.out.println("login success!");
        return Result.succ(MapUtil.builder()
                .put("id",admin.getId())
                .put("username",admin.getUsername())
                .map());
    }

        @GetMapping("/logout")
        @RequiresAuthentication //登录之后
        public Result logout(){
            SecurityUtils.getSubject().logout();
            return Result.succ(null);
        }

}
