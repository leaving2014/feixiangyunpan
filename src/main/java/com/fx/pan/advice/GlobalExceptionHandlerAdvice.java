package com.fx.pan.advice;

/**
 * @Author leaving
 * @Date 2022/1/19 12:43
 * @Version 1.0
 */

import com.fx.pan.common.Msg;
import com.fx.pan.common.ResultCodeEnum;
import com.fx.pan.exception.NotLoginException;
import com.fx.pan.exception.UploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 该注解为统一异常处理的核心
 *
 * 是一种作用于控制层的切面通知（Advice），该注解能够将通用的@ExceptionHandler、@InitBinder和@ModelAttributes
 * 方法收集到一个类型，并应用到所有控制器上
 */
@Slf4j
// @RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    /**
     * -------- 通用异常处理方法 --------
     **/
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Msg error(Exception e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return Msg.fail();    // 通用异常结果
    }

    /**
     * -------- 指定异常处理方法 --------
     **/
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Msg error(NullPointerException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return Msg.error((int) ResultCodeEnum.NULL_POINT.getCode(), ResultCodeEnum.NULL_POINT.getMessage());
    }

    /**
     * -------- 下标越界处理方法 --------
     **/
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Msg error(IndexOutOfBoundsException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return Msg.setResult(ResultCodeEnum.INDEX_OUT_OF_BOUNDS);
    }

    @ExceptionHandler(UploadException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public Msg error(UploadException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return Msg.setResult(ResultCodeEnum.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Msg error(NotLoginException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return Msg.setResult(ResultCodeEnum.NOT_LOGIN_ERROR);
    }


    /**
     * -------- 自定义定异常处理方法 --------
     **/
    @ExceptionHandler(FxException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Msg error(FxException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return Msg.error(e.getCode(), e.getMessage());
        // message(e.getMessage()).code(e.getCode());
    }

}
