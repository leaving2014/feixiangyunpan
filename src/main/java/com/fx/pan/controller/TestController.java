package com.fx.pan.controller;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.User;
import com.fx.pan.service.TokenService;
import com.fx.pan.service.impl.UserServiceImpl;
import com.fx.pan.utils.SysUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author leaving
 * @Date 2021/11/26 10:07
 * @Version 1.0
 */
@RestController

public class TestController {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasAnyAuthority('superAdmin')")
    @GetMapping("/sa")
    public Msg sa(@RequestHeader("Authorization") String token){


        User user = userService.getUserBeanByToken(token);
        System.out.println(user);
        return Msg.success("获取成功").put("name", "superAdmin");
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping("/ad")
    public Msg test(){

        return Msg.success("获取成功").put("name", "admin");
    }


    // @PreAuthorize("hasAnyAuthority('user')")
    @GetMapping("/us")
    public Msg hello(@RequestHeader("Authorization") String token){
        // User user = userService.getUserBeanByToken(token);
        // System.out.println(user);

        return  Msg.success("hello").put("name", "user").put("ts", SysUtil.getTimeStamp());
    }

    @PreAuthorize("jsr250Enabled = false ")
    @GetMapping("/u1")
    public Msg hello(){
        User user = userService.seletUserWithUserName("sa");
        // System.out.println(user);

        return  Msg.success("hello").put("user", user).put("ts", SysUtil.getTimeStamp());
    }


}
