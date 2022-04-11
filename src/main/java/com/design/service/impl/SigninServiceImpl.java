package com.design.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.entity.FoundFinish;
import com.design.entity.Signin;
import com.design.mapper.FoundFinishMapper;
import com.design.mapper.SigninMapper;
import com.design.service.FoundFinishService;
import com.design.service.SigninService;
import org.springframework.stereotype.Service;

@Service
public class SigninServiceImpl extends ServiceImpl<SigninMapper, Signin> implements SigninService {
}
