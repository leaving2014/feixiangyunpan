package com.fx.pan.advice;

/**
 * @author leaving
 * @date 2022/1/19 12:43
 * @version 1.0
 */

import com.fx.pan.common.AppHttpCodeEnum;
import com.fx.pan.domain.ResponseResult;
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
 * @author leaving
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    /**
     * -------- 通用异常处理方法 --------
     **/
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult error(Exception e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return ResponseResult.error(500, e.getMessage());    // 通用异常结果
    }

    /**
     * -------- 指定异常处理方法 --------
     **/
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult error(NullPointerException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return ResponseResult.error((int) ResultCodeEnum.NULL_POINT.getCode(), ResultCodeEnum.NULL_POINT.getMessage());
    }

    /**
     * -------- 下标越界处理方法 --------
     **/
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult error(IndexOutOfBoundsException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return ResponseResult.error(ResultCodeEnum.INDEX_OUT_OF_BOUNDS);
    }

    @ExceptionHandler(UploadException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ResponseResult error(UploadException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return ResponseResult.error(ResultCodeEnum.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult error(NotLoginException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return ResponseResult.error(ResultCodeEnum.NOT_LOGIN_ERROR);
    }


    /**
     * -------- 自定义定异常处理方法 --------
     **/
    @ExceptionHandler(FxException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult error(FxException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return ResponseResult.error(AppHttpCodeEnum.valueOf(e.getMessage()));
        // return ResponseResult.error(e.getCode(), e.getMessage());
        // message(e.getMessage()).code(e.getCode());
    }

}
