package com.chen.pojo;

import com.chen.constant.ResultConstant;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    private Result(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(ResultConstant.HTTPSTATUS.SUCCESS, ResultConstant.SUCCESS, data);
    }

    public static <T> Result<T> success() {
        return new Result<T>( ResultConstant.SUCCESS,ResultConstant.HTTPSTATUS.SUCCESS);
    }

    public static <T> Result<T> fil(String msg, int code) {
        return new Result<T>(msg, code);
    }

}
