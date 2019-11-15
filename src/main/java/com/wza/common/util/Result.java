package com.wza.common.util;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Api
@Data
public class Result<T> {
    @ApiModelProperty("实际传输的数据")
    private T data;

    //1，表示成功，其它失败；10000：表示需要重新登录
    private int code = 1;

    @ApiModelProperty
    private String msg = "操作完成";

    public Result() {
    }

    public static Result buildMsg(String msg) {
        return (new Result()).setCode(1).setMsg(msg);
    }

    public static Result buildSucc(Object data) {
        return (new Result()).setData(data).setCode(1);
    }

    public static Result buildFail(String msg) {
        return (new Result()).setCode(0).setMsg(msg);
    }

    public static Result buildFail(int code, String msg) {
        return (new Result()).setCode(code).setMsg(msg);
    }

    public Object getData() {
        return this.data;
    }

    public Result setData(T data) {
        this.data = data;
        return this;
    }

    public String getMsg() {
        return this.msg;
    }

    public Result setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Result setCode(int code) {
        this.code = code;
        return this;
    }

    public boolean isSucc() {
        return this.code == 1;
    }
}