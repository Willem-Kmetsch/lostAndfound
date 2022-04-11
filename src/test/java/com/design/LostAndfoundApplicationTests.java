package com.design;

import com.design.controller.FoundController;
import com.design.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LostAndfoundApplicationTests {

    @Autowired
    private FoundController controller;

    @Autowired
    UserMapper mapper;
    @Test
    void contextLoads() {
        System.out.println(mapper.selectById(1));
//        System.out.println(controller.getFounds(1));
    }

}
