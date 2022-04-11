package com.design.controller;

import com.design.common.dto.ExchangeMsgDto;
import com.design.common.lang.Result;
import com.design.entity.ExchangeGoods;
import com.design.entity.Goods;
import com.design.entity.User;
import com.design.service.ExchangeGoodsService;
import com.design.service.GoodsService;
import com.design.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/exchangeGoods")
public class ExchangeGoodsController {
    @Autowired
    ExchangeGoodsService exchangeGoodsService;

    @Autowired
    UserService userService;

    @Autowired
    GoodsService goodsService;

    @GetMapping("/list")
    public Result list(){
        List<ExchangeGoods> exchangeGoodsList = exchangeGoodsService.list();
        List<ExchangeMsgDto> dtoList = new ArrayList<>();
        for(ExchangeGoods exchangeGoods : exchangeGoodsList){
            User user = userService.getById(exchangeGoods.getUserid());
            Goods goods = goodsService.getById(exchangeGoods.getGoodsid());
            ExchangeMsgDto dto = new ExchangeMsgDto();
            dto.setUser(user);
            dto.setGoods(goods);
            dto.setExchangeGoods(exchangeGoods);
            dtoList.add(dto);
        }
        return Result.succ(dtoList);
    }
}
