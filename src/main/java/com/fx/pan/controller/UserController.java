package com.fx.pan.controller;

import com.fx.pan.common.Constants;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.domain.User;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.RedisCache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author leaving
 * @Date 2022/1/12 17:00
 * @Version 1.0
 */

@RestController
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private PasswordEncoder passwordEncoder;


    /**
     * @param user
     * @return
     */
    @PostMapping("/user/register")
    public Msg register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
    @PostMapping("/user/logout")
    public Msg logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) auth.getPrincipal();
        if (auth != null) {//清除认证
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        Long id = loginUser.getUser().getId();
        // 删除redis中的值
        redisCache.deleteObject(Constants.REDIS_LOGIN_USER_PREFIX +id);
        return  Msg.success("注销成功");
    }

    @PostMapping("/user/token")
    public Msg token(){

        return null;
    }


}
