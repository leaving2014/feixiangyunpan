package com.fx.pan.service;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.User;

/**
 * @Author leaving
 * @Date 2022/1/13 17:50
 * @Version 1.0
 */
public interface LoginService {
    Msg login(User user);

    ResponseResult logout();
}
