package com.fx.pan.exception.user;

import com.fx.pan.exception.base.BaseException;

/**
 * @author leaving
 * @date 2022/1/19 22:32
 * @version 1.0
 */

public class UserException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args)
    {
        super("user", code, args, null);
    }

}
