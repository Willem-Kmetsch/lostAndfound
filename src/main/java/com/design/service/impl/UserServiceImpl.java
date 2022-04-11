package com.design.service.impl;

import com.design.entity.User;
import com.design.mapper.UserMapper;
import com.design.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author god
 * @since 2022-02-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper mapper;

    public boolean geScore(Integer userId, Integer score){
        User user = mapper.selectById(1);
        return user.getScore() >= score;
    }

    @Override
    public Integer getScore(Integer userId) {
        User user = mapper.selectById(userId);
        return user.getScore();
    }
}
