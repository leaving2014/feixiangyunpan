package com.fx.pan.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fx.pan.common.Constants;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.domain.Storage;
import com.fx.pan.domain.User;
import com.fx.pan.service.StorageService;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author leaving
 * @Date 2022/1/12 17:00
 * @Version 1.0
 */

@Tag(name = "user", description = "该接口为用户接口，主要做用户登录，注册,退出和校验token")
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @Autowired
    private StorageService storageService;

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

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/user/userinfo")
    public Msg userInfo(){
        Long userId = SecurityUtils.getUserId();
        User user = userService.selectUserById(userId);
        QueryWrapper<Storage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Storage userStorage = storageService.getUserStorage(userId);
        return Msg.success("获取成功").put("userInfo", JSONObject.toJSON(user)).put("userStorage",userStorage);
    }

    /**
     * 获取用户存储信息
     * @return
     */
    @GetMapping("/user/storage")
    public Msg userStorage(){
        Long userId = SecurityUtils.getUserId();
        QueryWrapper<Storage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Storage userStorage = storageService.getUserStorage(userId);
        return Msg.success("获取成功").put("userStorage",userStorage);
    }

    @PostMapping("/user/token")
    public Msg token(){

        return null;
    }


}
