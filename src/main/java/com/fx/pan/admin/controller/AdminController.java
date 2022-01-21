package com.fx.pan.admin.controller;

import com.fx.pan.common.Msg;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author leaving
 * @Date 2022/1/20 16:09
 * @Version 1.0
 */

@RequestMapping("/admin")
@PreAuthorize("admin")
@RestController
public class AdminController {

    @GetMapping("/hello")
    public Msg hello(){
        return Msg.success("hello");
    }

}
