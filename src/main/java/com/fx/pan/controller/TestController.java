package com.fx.pan.controller;

import com.fx.pan.common.Msg;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author leaving
 * @Date 2021/11/26 10:07
 * @Version 1.0
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public Msg test(){

        return Msg.success("获取成功").put("name", "张三").put("sex", "男");
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}
