package com.example.architectureimageanalyzer.common;



import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;

@Builder
@Getter
public class Result<T> extends HashMap<String, Object> {
    //结果类
    /*private Integer code; //编码：0成功，1和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private String token; //token*/

    private Integer code;

    private String message;

    private T data;

    public Result() {
    }

    //Result继承了HashMap类 可以使用put方法往里面添加键值对
    //其中包含了三个属性 code message 和 data，分别表示编码、错误信息和数据
    public Result(Integer code, String message, Object data) {
        put("code", code);
        put("message", message);
        put("data", data);
    }

    //private Map map = new HashMap(); //动态数据

    public static <T> Result<T> success(T object) {
        return new Result<>(0, "成功", object);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(1, message, null);
    }

    public static Result<String> setToken(String token) {
        //调用Result 的 无参构造函数
        Result r = new Result();
        r.put("token", token);
        r.put("code", 0);
        r.put("message", "成功");
        return r;
    }

    public static <T> Result<T> ok(T data) {
        return (Result<T>) Result.builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static <T> Result<T> fail(T data) {
        return (Result<T>) builder()
                .code(ResultCode.FAIL.getCode())
                .message(ResultCode.FAIL.getMessage())
                .data(data)
                .build();
    }
    //这个是用来操作动态数据的
    public Result<T> add(String key, Object value) {
        put(key, value);
        return this;
    }

}
