package com.design.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.entity.ExchangeGoods;
import com.design.entity.FoundFinish;
import com.design.mapper.ExchangeGoodsMapper;
import com.design.mapper.FoundFinishMapper;
import com.design.service.ExchangeGoodsService;
import com.design.service.FoundFinishService;
import org.springframework.stereotype.Service;

@Service
public class ExchangeGoodsServiceImpl extends ServiceImpl<ExchangeGoodsMapper, ExchangeGoods> implements ExchangeGoodsService {
}
