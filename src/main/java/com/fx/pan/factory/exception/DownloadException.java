package com.fx.pan.factory.exception;

/**
 * @author leaving
 * @date 2022/3/4 11:44
 * @version 1.0
 */

public class DownloadException extends RuntimeException {
    public DownloadException(Throwable cause) {
        super("下载出现了异常", cause);
    }

    public DownloadException(String message) {
        super(message);
    }

    public DownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}

