package com.fx.pan.advice;

import com.fx.pan.common.ResultCodeEnum;
import lombok.Data;

/**
 * @author leaving
 * @date 2022/1/19 12:48
 * @version 1.0
 */
@Data
public class FxException extends RuntimeException {
    private Integer code;

    public FxException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public FxException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "FxException{" + "code=" + code + ", message=" + this.getMessage() + '}';
    }
}
