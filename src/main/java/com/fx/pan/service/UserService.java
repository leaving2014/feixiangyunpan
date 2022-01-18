package com.fx.pan.service;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.User;

/**
 * @Author leaving
 * @Date 2022/1/14 10:47
 * @Version 1.0
 */

public interface UserService{



    Msg register(User user);

    Msg login(String username,String password);

    Msg logout();

    User seletUserWithUserName(String username);

    Msg updateUser(User user);
}
