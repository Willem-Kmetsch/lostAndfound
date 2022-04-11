package com.design.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.design.common.lang.Result;
import com.design.entity.ExchangeGoods;
import com.design.entity.Goods;
import com.design.entity.ScoreOperation;
import com.design.entity.User;
import com.design.service.ExchangeGoodsService;
import com.design.service.GoodsService;
import com.design.service.ScoreOperationService;
import com.design.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
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
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    GoodsService goodsService;

    @Autowired
    UserService userService;

    @Autowired
    ScoreOperationService scoreOperationService;

    @Autowired
    ExchangeGoodsService exchangeGoodsService;

    @GetMapping("/")
    public Result getGoods(){
        List<Goods> goods = goodsService.list();
        return Result.succ(goods);

    }

    @GetMapping("/exchange")
    @RequiresAuthentication
    public Result Exchage(@RequestParam("userId") Integer userId,
                          @RequestParam("goodsId") Integer goodsId,
                          @RequestParam("number") Integer number){
        User user = userService.getById(userId);
        Goods goods = goodsService.getById(goodsId);
        // 消耗积分
        Integer value = goods.getScore() * number;
        // 更新用户积分
        UpdateWrapper<User> userQueryWrapper = new UpdateWrapper<User>();
        userQueryWrapper.eq("id",userId);
        userQueryWrapper.set("score",user.getScore() - value);
        userService.update(userQueryWrapper);
        // 积分操作表
        ScoreOperation so = new ScoreOperation();
        so.setUserid(userId);
        so.setOperationtime(LocalDateTime.now());
        so.setOperationtype(6);
        so.setOperationscore(value);
        so.setRemainscore(user.getScore());
        scoreOperationService.save(so);

        //商品
        UpdateWrapper<Goods> goodsUpdateWrapper = new UpdateWrapper<>();
        goodsUpdateWrapper.eq("id",goodsId);
        goodsUpdateWrapper.set("number",goods.getNumber() - number);
        goodsService.update(goodsUpdateWrapper);


        //兑换记录
        ExchangeGoods exchangeGoods = new ExchangeGoods();
        exchangeGoods.setUserid(userId);
        exchangeGoods.setGoodsid(goodsId);
        exchangeGoods.setNumber(number);
        exchangeGoods.setScore(value);
        exchangeGoods.setExchangetime(LocalDateTime.now());
        exchangeGoodsService.save(exchangeGoods);

        return Result.succ("兑换成功！");
    }

    @GetMapping("/getGoods")
    public Result getGoods(@RequestParam("goodsId") Integer goodsId){
        Goods goods = goodsService.getById(goodsId);
        return Result.succ(goods);
    }

    @PostMapping("/updateGoods")
    public Result updateGoods(@RequestBody Goods goods){
        UpdateWrapper<Goods> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",goods.getId());
        wrapper.set("name",goods.getName());
        wrapper.set("score",goods.getScore());
        wrapper.set("number",goods.getNumber());
        goodsService.update(wrapper);
        return Result.succ("修改完成！");
    }

    @GetMapping("/deleteGoods")
    public Result deleteGoods(@RequestParam("goodsId")Integer goodsId){
        goodsService.removeById(goodsId);
        return Result.succ("删除成功！");
    }

    @PostMapping("/addGoods")
    public Result addGoods(@RequestBody Goods goods){
        goodsService.save(goods);
        return Result.succ("添加成功！");
    }

    // 上传图片
    @PostMapping("/uploadImg")
    public Result uploadImg(@RequestParam MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename() + "图片已传入!!");
        FileOutputStream fos = new FileOutputStream("C:\\Users\\z'z'z\\IdeaProjects\\lostAndfound-vue\\static\\goods\\" + file.getOriginalFilename(),false);
        fos.write(file.getBytes());
        fos.flush();
        fos.close();
        return Result.succ("succ");
    }


}
