package com.design.common.dto;

import com.design.entity.ExchangeGoods;
import com.design.entity.Goods;
import com.design.entity.User;
import lombok.Data;

@Data
public class ExchangeMsgDto {
    private User user;
    private ExchangeGoods exchangeGoods;
    private Goods goods;
}
