package com.fx.pan.controller;

import com.fx.pan.domain.ResponseResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leaving
 * @date 2022/5/3 12:47
 * @version 1.0
 */

@RestController
@RequestMapping("/system")

public class SystemController {

    @Value("${fx.enableCaptcha}")
    private String enableCaptcha;

    @GetMapping("/config")
    public ResponseResult config() {
        Map<String, Object> map = new HashMap<>();
        map.put("enableCaptcha", enableCaptcha);
        return ResponseResult.success(map);
    }
}
