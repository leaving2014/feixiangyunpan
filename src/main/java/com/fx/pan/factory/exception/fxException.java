package com.fx.pan.factory.exception;

/**
 * @author leaving
 * @date 2022/3/4 11:43
 * @version 1.0
 */

public class fxException extends RuntimeException{
    public fxException(Throwable cause) {
        super("统一文件操作平台（UFOP）出现异常", cause);
    }

    public fxException(String message) {
        super(message);
    }

    public fxException(String message, Throwable cause) {
        super(message, cause);
    }
}
