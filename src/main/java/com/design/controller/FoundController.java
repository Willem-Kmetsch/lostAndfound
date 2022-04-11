package com.design.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.UserInfo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.design.common.dto.AddFoundDto;
import com.design.common.dto.ExceptionalDto;
import com.design.common.dto.FoundDto;
import com.design.common.dto.LoginDto;
import com.design.common.lang.PageData;
import com.design.common.lang.Result;
import com.design.entity.*;
import com.design.service.FoundFinishService;
import com.design.service.FoundService;
import com.design.service.ScoreOperationService;
import com.design.service.UserService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
@RequestMapping("/found")
public class FoundController {
    @Autowired
    FoundService foundService;

    @Autowired
    UserService userService;

    @Autowired
    ScoreOperationService scoreOperationService;

    @Autowired
    FoundFinishService foundFinishService;

    @PostMapping("/aaa")
    public Result aaa(){
        String a = "hello";
        System.out.println(a);
        return Result.succ(a);
    }

    // 获取招领列表
//    @GetMapping("/")
//    public Result getFounds(@RequestParam(defaultValue = "1") int page){
//        Page<Found> p = new Page(page,4); // 每页大小为4， 取出第page页
//        foundService.page(p);
//        System.out.println(Result.succ(p.getRecords()));
//        return Result.succ(p.getRecords());
//    }

    // 获取失物列表
    @PostMapping("/")
    public Result getFounds(@Validated @RequestBody FoundDto foundDto,
                            @RequestParam(defaultValue = "1") int page) throws ParseException {
        LambdaQueryWrapper<Found> queryWrapper = Wrappers.<Found>query().lambda();
        if(foundDto != null) {
            // 学校
            if (StrUtil.isNotBlank(foundDto.getSchool())) {

                queryWrapper.apply("school = '" + foundDto.getSchool() + "'");
            }
            // 类型
            if (foundDto.getType() != null) {
                queryWrapper.apply("type = " + foundDto.getType() + "");
            }
            // 时间
            if (foundDto.getBeginDate() != null) {
                String strBegin = DateFormatUtils.format(foundDto.getBeginDate(), "yyyy-MM-dd HH:mm:ss");
                Calendar c = Calendar.getInstance();
                c.setTime(foundDto.getBeginDate());
                c.add(Calendar.DAY_OF_MONTH,1);
                String strEnd = DateFormatUtils.format(c.getTime(),"yyyy-MM-dd HH:mm:ss");

                queryWrapper.apply("UNIX_TIMESTAMP(foundtime) >= UNIX_TIMESTAMP('" + strBegin + "')");
                queryWrapper.apply("UNIX_TIMESTAMP(foundtime) < UNIX_TIMESTAMP('" + strEnd + "')");
            }
            // 地点
            if (StrUtil.isNotBlank(foundDto.getPlace())) {
                queryWrapper.apply("foundplace = '" + foundDto.getPlace() + "'");
            }
        }
        // 状态为0，未被认领
        queryWrapper.apply("state = 0");

        Page<Found> p = new Page(page,4); // 每页大小为4， 取出第page页
        foundService.page(p,queryWrapper);
        return Result.succ(new PageData(foundService.count(queryWrapper),p.getRecords()));
    }

    // 根据类型查询
    @GetMapping("/type/{type}")
    public Result getFoundsByType(@PathVariable("type") int type,
                                  @RequestParam(defaultValue = "1") int page){
        QueryWrapper<Found> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);

        Page<Found> p = new Page(page,10);

        foundService.page(p,wrapper);
        return Result.succ(p.getRecords());
    }

    // 根据时间段查询
    @GetMapping("/losttime/{begintime}/{endtime}")
    public Result getFoundsByType(@DateTimeFormat(pattern="yyyy-MM-dd") @PathVariable("begintime") Date begintime,
                                  @DateTimeFormat(pattern="yyyy-MM-dd") @PathVariable("endtime") Date endtime,
                                  @RequestParam(defaultValue = "1") int page){
        LambdaQueryWrapper<Found> queryWrapper = Wrappers.<Found>query().lambda();

        String strEnd= DateFormatUtils.format(begintime,"yyyy-MM-dd HH:mm:ss");

        String start = DateFormatUtils.format(endtime,"yyyy-MM-dd HH:mm:ss");

        queryWrapper.apply("UNIX_TIMESTAMP(losttime) <= UNIX_TIMESTAMP('" + start + "')");

        queryWrapper.apply("UNIX_TIMESTAMP(losttime) >= UNIX_TIMESTAMP('" + strEnd + "')");


        Page<Found> p = new Page(page,10);

        foundService.page(p,queryWrapper);
        return Result.succ(p.getRecords());
    }

    // 认领
    @GetMapping("/claim/")
    @RequiresAuthentication
    public Result claim(@RequestParam("foundId") Integer foundId){
        Found found = foundService.getById(foundId);
        User user = userService.getById(found.getFounderid());

        return Result.succ(user);
    }

    // 打赏
    @PostMapping("/exceptional")
    @RequiresAuthentication
    public Result Exceptional(@RequestBody ExceptionalDto exceptional){
        exceptional.setValue(exceptional.getValue() == null ? 0 : exceptional.getValue());

        if(! userService.geScore(exceptional.getUserId(), exceptional.getValue())){
            return Result.succ("积分不足，您当前的积分为：" + userService.getScore(exceptional.getUserId()));
        }
        // 失物状态
        UpdateWrapper<Found> foundUpdateWrapper = new UpdateWrapper<>();
        foundUpdateWrapper.eq("id",exceptional.getFoundId());
        foundUpdateWrapper.set("state",1);
        foundUpdateWrapper.set("ownerid",exceptional.getUserId());
        foundUpdateWrapper.set("score",exceptional.getValue());
        foundService.update(null, foundUpdateWrapper);
        // 用户减
        User user = userService.getById(exceptional.getUserId());
        if(exceptional.getValue() != 0) {
            UpdateWrapper<User> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", user.getId());
            wrapper.set("score", user.getScore() - exceptional.getValue());
            userService.update(null, wrapper);
            // 记录
            ScoreOperation s1 = new ScoreOperation();
            s1.setUserid(exceptional.getUserId());
            s1.setOperationtime(LocalDateTime.now());
            s1.setOperationtype(5);
            s1.setOperationscore(exceptional.getValue());
            s1.setRemainscore(user.getScore());
            scoreOperationService.save(s1);
            // 失主加
            user = userService.getById(exceptional.getFounderId());
            wrapper.clear();
            wrapper.eq("id", user.getId());
            wrapper.set("score", user.getScore() + exceptional.getValue());
            userService.update(null, wrapper);
            // 记录
            ScoreOperation s2 = new ScoreOperation();
            s2.setUserid(exceptional.getFoundId());
            s2.setOperationtime(LocalDateTime.now());
            s2.setOperationtype(3);
            s2.setOperationscore(exceptional.getValue());
            s2.setRemainscore(user.getScore());
            scoreOperationService.save(s2);
        }

        return Result.succ("已打赏" + exceptional.getValue() + "积分");
    }

    // 上传图片
    @PostMapping("/uploadImg")
    public Result uploadImg(@RequestParam MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename() + "图片已传入!!");
        FileOutputStream fos = new FileOutputStream("C:\\Users\\z'z'z\\IdeaProjects\\lostAndfound-vue\\static\\found\\" + file.getOriginalFilename(),false);
        fos.write(file.getBytes());
        fos.flush();
        fos.close();
        return Result.succ("succ");
    }

    // 添加失物招领
    @PostMapping("/addFound")
    public Result addFound(@RequestBody AddFoundDto addFoundDto){
        System.out.println("aaa");
        User user = userService.getById(addFoundDto.getUserId());
        Found found = new Found();
        found.setName(addFoundDto.getName());
        found.setType(addFoundDto.getType());
        found.setSchool(user.getSchool());
        LocalDate localDate = addFoundDto.getDate1().toLocalDate();
        LocalTime localTime = addFoundDto.getDate2().toLocalTime();
        found.setFoundtime(LocalDateTime.of(localDate,localTime));
        found.setFoundplace(addFoundDto.getPlace());
        found.setFounderid(user.getId());
        found.setPicture(addFoundDto.getPicurl());
        found.setItemexplain(addFoundDto.getDesc());
        found.setState(0);
        foundService.save(found);
        System.out.println("addSuccess");
        return Result.succ("succ");
    }


    // 已发布
    @GetMapping("/getFinishingFounds")
    public Result getFinishingFounds(@RequestParam("userId") Integer userId){
        QueryWrapper<Found> wrapper = new QueryWrapper<>();
        wrapper.eq("founderid",userId);
        List<Found> list = foundService.list(wrapper);
        List<String> usernames = new ArrayList<>();
        for(Found found : list){
            if(found.getOwnerid() != null){
                User user = userService.getById(found.getOwnerid());
                usernames.add(user.getUsername());
            }else{
                usernames.add(null);
            }
        }

        return Result.succ(MapUtil.builder()
                .put("founds",list)
                .put("username",usernames)
                .map());
    }

    // 撤销
    @GetMapping("/deleteFound")
    public Result deleteFound(@RequestParam("id") Integer id){
        QueryWrapper<Found> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);
        foundService.remove(wrapper);
        return Result.succ("删除成功！");
    }

    // 完成
    @GetMapping("/finishFound")
    public Result finishFound(@RequestParam("id") Integer id){
        QueryWrapper<Found> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);

        Found found = foundService.getById(id);
        FoundFinish foundFinish = new FoundFinish();
        foundFinish.setName(found.getName());
        foundFinish.setType(found.getType());
        foundFinish.setFoundtime(found.getFoundtime());
        foundFinish.setFoundplace(found.getFoundplace());
        foundFinish.setFounderid(found.getFounderid());
        foundFinish.setPicture(found.getPicture());
        foundFinish.setItemexplain(found.getItemexplain());
        foundFinish.setOwnerid(found.getOwnerid());
        foundFinish.setFinishtime(LocalDateTime.now());
        foundFinish.setScore(found.getScore());
        foundFinishService.save(foundFinish);

        foundService.remove(wrapper);

        return Result.succ("已完成！");
    }


}
