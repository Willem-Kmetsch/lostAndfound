package com.design.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.design.common.dto.AddFoundDto;
import com.design.common.dto.AddLostDto;
import com.design.common.dto.FoundDto;
import com.design.common.dto.LostDto;
import com.design.common.lang.PageData;
import com.design.common.lang.Result;
import com.design.entity.*;
import com.design.service.LostFinishService;
import com.design.service.LostService;
import com.design.service.ScoreOperationService;
import com.design.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
@RequestMapping("/lost")
public class LostController {

    @Autowired
    LostService lostService;

    @Autowired
    UserService userService;

    @Autowired
    ScoreOperationService scoreOperationService;

    @Autowired
    LostFinishService lostFinishService;

    // 获取寻物启事列表
    @PostMapping("/")
    public Result getFounds(@Validated @RequestBody LostDto lostDto,
                            @RequestParam(defaultValue = "1") int page) throws ParseException {
        LambdaQueryWrapper<Lost> queryWrapper = Wrappers.<Lost>query().lambda();
        if(lostDto != null) {
            // 学校
            if (StrUtil.isNotBlank(lostDto.getSchool())) {
                queryWrapper.apply("school = '" + lostDto.getSchool() + "'");
            }
            // 失物类型
            if (lostDto.getType() != null) {
                queryWrapper.apply("type = " + lostDto.getType() + "");
            }
            // 丢失时间
            if (lostDto.getBeginDate() != null) {
                String strBegin = DateFormatUtils.format(lostDto.getBeginDate(), "yyyy-MM-dd HH:mm:ss");
                Calendar c = Calendar.getInstance();
                c.setTime(lostDto.getBeginDate());
                c.add(Calendar.DAY_OF_MONTH,1);
                String strEnd = DateFormatUtils.format(c.getTime(),"yyyy-MM-dd HH:mm:ss");

                queryWrapper.apply("UNIX_TIMESTAMP(losttime) >= UNIX_TIMESTAMP('" + strBegin + "')");
                queryWrapper.apply("UNIX_TIMESTAMP(losttime) < UNIX_TIMESTAMP('" + strEnd + "')");
            }
            // 丢失地点
            if (StrUtil.isNotBlank(lostDto.getPlace())) {
                queryWrapper.apply("lostplace = '" + lostDto.getPlace() + "'");
            }
        }
        // 状态为0
        queryWrapper.apply("state = 0");

        Page<Lost> p = new Page(page,4); // 每页大小为4， 取出第page页
        lostService.page(p,queryWrapper);
        return Result.succ(new PageData(lostService.count(queryWrapper),p.getRecords()));
    }


    // 根据类型查询
    @GetMapping("/type/{type}")
    public Result getFoundsByType(@PathVariable("type") int type,
                                  @RequestParam(defaultValue = "1") int page){
        QueryWrapper<Lost> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);

        Page<Lost> p = new Page(page,10);

        lostService.page(p,wrapper);
        return Result.succ(p.getRecords());
    }

    // 根据时间段查询
    @GetMapping("/losttime/{begintime}/{endtime}")
    public Result getFoundsByType(@DateTimeFormat(pattern="yyyy-MM-dd") @PathVariable("begintime") Date begintime,
                                  @DateTimeFormat(pattern="yyyy-MM-dd") @PathVariable("endtime") Date endtime,
                                  @RequestParam(defaultValue = "1") int page){
        LambdaQueryWrapper<Lost> queryWrapper = Wrappers.<Lost>query().lambda();

        String strEnd= DateFormatUtils.format(begintime,"yyyy-MM-dd HH:mm:ss");

        String start = DateFormatUtils.format(endtime,"yyyy-MM-dd HH:mm:ss");

        queryWrapper.apply("UNIX_TIMESTAMP(losttime) <= UNIX_TIMESTAMP('" + start + "')");

        queryWrapper.apply("UNIX_TIMESTAMP(losttime) >= UNIX_TIMESTAMP('" + strEnd + "')");


        Page<Lost> p = new Page(page,10);

        lostService.page(p,queryWrapper);
        return Result.succ(p.getRecords());
    }

    // 根据失物地点查询
    @GetMapping("/lostplace/{lostplace}")
    public Result getFoundsByLostPlace(@PathVariable("lostplace") String lostplace,
                                       @RequestParam(defaultValue = "1") int page){
        QueryWrapper<Lost> wrapper = new QueryWrapper<>();
        wrapper.eq("lostplace",lostplace);

        Page<Lost> p = new Page(page,10);

        lostService.page(p,wrapper);
        return Result.succ(p.getRecords());
    }

    // 根据失主查询
    @GetMapping("/ownerid/{ownerid}")
    public Result getFoundsByOwnerid(@PathVariable("ownerid") int ownerid){
        QueryWrapper<Lost> wrappers = new QueryWrapper<>();
        wrappers.eq("ownerid",ownerid);
        List<Lost> list = lostService.list(wrappers);
        return Result.succ(list);
    }

    // 归还
    @GetMapping("/returnLost/")
    @RequiresAuthentication
    public Result returnLost(@RequestParam("lostId") Integer lostId){
        Lost lost = lostService.getById(lostId);
        User user = userService.getById(lost.getOwnerid());

        return Result.succ(user);
    }

    @GetMapping("/confirmclaim")
    public Result confirmClaim(@RequestParam("lostId") Integer lostId,
                               @RequestParam("userId") Integer userId){
        UpdateWrapper<Lost> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",lostId);
        wrapper.set("state",1);
        wrapper.set("founderid",userId);
        lostService.update(null,wrapper);

        return Result.succ("成功！");
    }



    // 上传图片
    @PostMapping("/uploadImg")
    public Result uploadImg(@RequestParam MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename() + "图片已传入!!");
        FileOutputStream fos = new FileOutputStream("C:\\Users\\z'z'z\\IdeaProjects\\lostAndfound-vue\\static\\lost\\" + file.getOriginalFilename(),false);
        fos.write(file.getBytes());
        fos.flush();
        fos.close();
        return Result.succ("succ");
    }

    // 添加寻物启事
    @PostMapping("/addLost")
    public Result addLost(@RequestBody AddLostDto addLostDto){
        addLostDto.setScore(addLostDto.getScore() == null ? 0 : addLostDto.getScore());
        User user = userService.getById(addLostDto.getUserId());
        if(user.getScore() < addLostDto.getScore()){
            return Result.succ("积分不足！");
        }
        Lost lost = new Lost();
        lost.setName(addLostDto.getName());
        lost.setType(addLostDto.getType());
        lost.setSchool(user.getSchool());
        LocalDate localDate = addLostDto.getDate1().toLocalDate();
        LocalTime localTime = addLostDto.getDate2().toLocalTime();
        lost.setLosttime(LocalDateTime.of(localDate,localTime));
        lost.setLostplace(addLostDto.getPlace());
        lost.setOwnerid(user.getId());
        lost.setPicture(addLostDto.getPicurl());
        lost.setItemexplain(addLostDto.getDesc());
        lost.setState(0);
        lost.setScore(addLostDto.getScore());
        lostService.save(lost);
        System.out.println("addSuccess");

        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",user.getId());
        wrapper.set("score",user.getScore() - addLostDto.getScore());
        userService.update(wrapper);

        return Result.succ("succ");
    }



    // 已发布
    @GetMapping("/getFinishingLosts")
    public Result getFinishingLosts(@RequestParam("userId") Integer userId){
        QueryWrapper<Lost> wrapper = new QueryWrapper<>();
        wrapper.eq("ownerid",userId);
        List<Lost> list = lostService.list(wrapper);
        List<String> usernames = new ArrayList<>();
        for(Lost lost : list){
            if(lost.getFounderid() != null){
                User user = userService.getById(lost.getFounderid());
                usernames.add(user.getUsername());
            }else{
                usernames.add(null);
            }
        }

        return Result.succ(MapUtil.builder()
                .put("losts",list)
                .put("username",usernames)
                .map());
    }

    // 撤销
    @GetMapping("/deleteLost")
    public Result deleteLost(@RequestParam("id") Integer id){
        QueryWrapper<Lost> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);
        lostService.remove(wrapper);
        return Result.succ("删除成功！");
    }

    // 完成
    @GetMapping("/finishLost")
    public Result finishLost(@RequestParam("id") Integer id
                             ){
        QueryWrapper<Lost> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        Lost lost = lostService.getById(id);
        LostFinish lostFinish = new LostFinish();
        lostFinish.setName(lost.getName());
        lostFinish.setType(lost.getType());
        lostFinish.setLosttime(lost.getLosttime());
        lostFinish.setLostplace(lost.getLostplace());
        lostFinish.setOwnerid(lost.getOwnerid());
        lostFinish.setPicture(lost.getPicture());
        lostFinish.setItemexplain(lost.getItemexplain());
        lostFinish.setScore(lost.getScore());
        lostFinish.setFounderid(lost.getFounderid());
        lostFinish.setFinishtime(LocalDateTime.now());
        lostFinishService.save(lostFinish);
        lostService.remove(wrapper);

        // 拾物者加积分
        User user = userService.getById(lost.getFounderid());
        UpdateWrapper<User> wrapper1 = new UpdateWrapper<>();
        wrapper1.eq("id",lost.getFounderid());
        wrapper1.set("score",user.getScore() + lost.getScore());
        userService.update(wrapper1);

        // 操作
        ScoreOperation scoreOperation = new ScoreOperation();
        scoreOperation.setUserid(user.getId());
        scoreOperation.setOperationtime(LocalDateTime.now());
        scoreOperation.setOperationtype(2);
        scoreOperation.setOperationscore(lost.getScore());
        scoreOperation.setRemainscore(user.getScore());
        scoreOperationService.save(scoreOperation);


        return Result.succ("已完成！");
    }




}
