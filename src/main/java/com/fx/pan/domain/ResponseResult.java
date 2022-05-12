package com.fx.pan.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fx.pan.common.AppHttpCodeEnum;
import com.fx.pan.common.ResultCodeEnum;

import java.io.Serializable;
import java.util.List;

/**
 * @author leaving
 * @date 2021/10/31 20:58
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T>  implements Serializable {

    private Integer code;
    private String msg;
    private T data;
    // private Map<String,Object> mapData = new HashMap<String, Object>();

    // private Map<String,Object> mapData = new HashMap<String, Object>();


    public ResponseResult() {
        this.code = AppHttpCodeEnum.SUCCESS.getCode();
        this.msg = AppHttpCodeEnum.SUCCESS.getMsg();
    }

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public static ResponseResult error(int code, String msg) {
        ResponseResult result = new ResponseResult();
        return result.errorResult(code, msg);
        // return result.ok(code, null, msg);
        // return error(code, msg);
    }
    public static ResponseResult success() {
        ResponseResult result = new ResponseResult();
        return result;
    }
    public static ResponseResult success(String msg) {
        ResponseResult result = new ResponseResult();
        return result.ok(0, null, msg);
    }

    public static ResponseResult success(String msg,Object data) {
        ResponseResult result =  new ResponseResult();
        return result.ok(0, data, msg);
    }

    public static ResponseResult success(List list) {
        ResponseResult result =  new ResponseResult();
        return result.ok(0, list);
    }


    public static ResponseResult success(int code,Object data) {
        ResponseResult result = new ResponseResult();
        return result.ok(code, data);
    }

    public static ResponseResult success(int code, String msg) {
        ResponseResult result = new ResponseResult();
        return result.ok(code, null, msg);
    }

    public static ResponseResult success(Object data) {
        ResponseResult result = setAppHttpCodeEnum(AppHttpCodeEnum.SUCCESS, AppHttpCodeEnum.SUCCESS.getMsg());
        if(data!=null) {
            result.setData(data);
        }
        return result;
    }

    // public ResponseResult put(String key,Object data){
    //     ResponseResult result = setAppHttpCodeEnum(AppHttpCodeEnum.SUCCESS, AppHttpCodeEnum.SUCCESS.getMsg());
    //         Map map= new HashMap<String,Object>();
    //         map.put(key,data);
    //         this.mapData.put(key,data);
    //         result.setData(map);
    //     return this;
    // }

    public static ResponseResult error(AppHttpCodeEnum enums){
        return setAppHttpCodeEnum(enums,enums.getMsg());
    }

    public static ResponseResult error(AppHttpCodeEnum enums, String msg){
        return setAppHttpCodeEnum(enums,msg);
    }

    public static ResponseResult setAppHttpCodeEnum(AppHttpCodeEnum enums){
        return success(enums.getCode(),enums.getMsg());
    }

    private static ResponseResult setAppHttpCodeEnum(AppHttpCodeEnum enums, String msg){
        return success(enums.getCode(),msg);
    }

    public static ResponseResult error(ResultCodeEnum emums){
        return error(emums.getCode(), emums.getMessage());
    }

    public ResponseResult<T> errorResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    public ResponseResult<T> ok(Integer code, T data) {
        this.code = code;
        this.data = data;
        return this;
    }

    public ResponseResult<T> ok(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        return this;
    }

    public ResponseResult<T> ok(T data) {
        this.data = data;
        return this;
    }

    public ResponseResult setResultCode(ResultCodeEnum resultCodeEnum) {
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMessage();
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    //
    // private int code;
    //
    // private String msg;
    //
    // // private T data;
    //
    // private Map<String,T> data = new HashMap<>();
    //
    // public ResponseResult(){
    //     this.code = AppHttpCodeEnum.SUCCESS.getCode();;
    //     this.msg = AppHttpCodeEnum.SUCCESS.getMsg();
    // }
    //
    // public ResponseResult(int code, String msg) {
    //     this.code = code;
    //     this.msg = msg;
    // }
    //
    // public static ResponseResult msg(int code, String msg){
    //     ResponseResult result = new ResponseResult(code,msg);
    //     return result;
    // }
    //
    // /**
    //  * 处理成功时返回的数据
    //  * @return
    //  */
    // public static ResponseResult success(){
    //     ResponseResult result = new ResponseResult(0,"success");
    //     return result;
    // }
    //
    // public static ResponseResult success(String msg){
    //     ResponseResult result = new ResponseResult(0,msg);
    //     return result;
    // }
    //
    // public static ResponseResult success(Object data){
    //     ResponseResult result = new ResponseResult();
    //     // result.setData(data);
    //     return result;
    // }
    //
    //
    // // 通用返回失败，未知错误
    // public static ResponseResult fail() {
    //     ResponseResult msg = new ResponseResult();
    //     msg.setCode(ResultCodeEnum.UNKNOWN_ERROR.getCode());
    //     msg.setMsg(ResultCodeEnum.UNKNOWN_ERROR.getMessage());
    //     return msg;
    // }
    //
    // // 设置结果，形参为结果枚举
    // public static ResponseResult setResult(ResultCodeEnum result) {
    //
    //     ResponseResult msg = new ResponseResult();
    //     msg.setCode(result.getCode());
    //     msg.setMsg(result.getMessage());
    //     return msg;
    // }
    //
    // // public static ResponseResult error(){
    // //     ResponseResult result = new ResponseResult(400,"处理失败");
    // //     return result;
    // // }
    // // public static ResponseResult error(String msg){
    // //     ResponseResult result = new ResponseResult(400,msg);
    // //     return result;
    // // }
    //
    // public static ResponseResult error(int code, String msg){
    //     ResponseResult result = new ResponseResult(code,msg);
    //     return result;
    // }
    //
    // /**
    //  * 添加封装的数据，实现链式编程
    //  * @param key
    //  * @param value
    //  * @return
    //  */
    // public ResponseResult put(String key, Object value){
    //     if (key.equals("data")){
    //         this.data = (Map<String, T>) value;
    //     } else {
    //         this.data.put(key, (T) value);
    //     }
    //     return this;
    // }
    //
    // public int getCode() {
    //     return code;
    // }
    //
    // public void setCode(int code) {
    //     this.code = code;
    // }
    //
    // public String getMsg() {
    //     return msg;
    // }
    //
    // public void setMsg(String msg) {
    //     this.msg = msg;
    // }
    //
    // public Map<String, T> getData() {
    //     return data;
    // }
    //
    // public void setData(Map<String, T> data) {
    //     this.data = data;
    // }
    //
    // @Override
    // public String toString() {
    //     return "ResponseResult{" +
    //             "code=" + code +
    //             ", msg='" + msg + '\'' +
    //             ", data=" + data +
    //             '}';
    // }
}
