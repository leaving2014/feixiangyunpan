package com.fx.pan.controller;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.User;
import com.fx.pan.service.LoginService;
import com.fx.pan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author leaving
 * @Date 2022/1/12 17:00
 * @Version 1.0
 */

@RestController
public class UserController {

    @Resource
    private UserService userService;

    @Autowired
    private LoginService loginService;

    /**
     * @param user
     * @return
     */
    @PostMapping("/user/register")
    public Msg register(@RequestBody User user) {

        return userService.register(user);
    }


    /**
     * 登录
     *
     * @param user
     * @return
     */
    @PostMapping("/user/login")
    public Msg login(@RequestBody User user) {
        return userService.login(user.getUserName(),user.getPassword());
        // return loginService.login(user);
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @PostMapping("/user/update")
    public Msg updateUser(@RequestBody User user) {
        System.out.println(user);
        return userService.updateUser(user);
    }


    @PostMapping("/user/query")
    public Msg login(@RequestParam String username) {
        User user = userService.seletUserWithUserName(username);
        return Msg.success("成功").put("res", user);
    }


    /**
     * 退出登录
     *
     * @return
     */
    @GetMapping("/user/logout")
    public Msg logout() {
        return userService.logout();
    }


}
