package com.fx.pan.admin.controller;

import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.User;
import com.fx.pan.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author leaving
 * @date 2022/1/20 16:50
 * @version 1.0
 */

@RestController
@PreAuthorize("admin")
@RequestMapping("/admin/")
public class AdminUserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public ResponseResult login(@RequestParam("username") String username, @RequestParam("password") String password) {
        ResponseResult login = userService.login(username, password);
        return login;
    }

    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user) {
        int i = userService.adminRegister(user);
        if (i == 1) {
            return ResponseResult.success("注册成功");
        } else if (i == -1) {
            return ResponseResult.error(500,"注册失败,用户名已存在");
        }
        return ResponseResult.error(500,"注册失败");
    }

    @PostMapping("/logout")
    public ResponseResult logout() {
        ResponseResult logout = userService.logout();
        return logout;
    }
}
