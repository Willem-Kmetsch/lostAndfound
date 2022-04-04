package com.design.common.lang;

import lombok.Data;

@Data
public class Result {
    private int code; // 200 正常
    private String msg;
    private Object data;

    public static Result succ(Object data){
        return succ(200,"成功",data);
    }

    public static Result succ(String msg, Object data){
        return succ(200, msg, data);
    }

    public static Result succ(int code, String msg, Object data){
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static Result fail(String msg){
        return fail(400,msg,null);
    }

    public static Result fail(String msg, Object data){
        return fail(400, msg, data);
    }

    public static Result fail(int code, String msg, Object data){
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
}
