package com.fx.pan.admin.controller;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.User;
import com.fx.pan.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author leaving
 * @Date 2022/1/20 16:50
 * @Version 1.0
 */

@RestController
@PreAuthorize("admin")
@RequestMapping("/admin/")
public class AdminUserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Msg login(@RequestParam("username") String username, @RequestParam("password") String password) {
        Msg login = userService.login(username, password);
        return login;
    }

    @PostMapping("/register")
    public Msg register(@RequestBody User user) {
        int i = userService.adminRegister(user);
        if (i == 1) {
            return Msg.success("注册成功");
        } else if (i == -1) {
            return Msg.error(500,"注册失败,用户名已存在");
        }
        return Msg.error(500,"注册失败");
    }

    @PostMapping("/logout")
    public Msg logout() {
        Msg logout = userService.logout();
        return logout;
    }
}
