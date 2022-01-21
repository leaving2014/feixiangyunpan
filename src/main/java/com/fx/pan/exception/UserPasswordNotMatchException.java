package com.fx.pan.exception;


import com.fx.pan.exception.user.UserException;

/**
 * @Author leaving
 * @Date 2022/1/19 22:30
 * @Version 1.0
 */

public class UserPasswordNotMatchException extends UserException
{
    private static final long serialVersionUID = 1L;

    public UserPasswordNotMatchException()
    {
        super("user.password.not.match", null);
    }
}
