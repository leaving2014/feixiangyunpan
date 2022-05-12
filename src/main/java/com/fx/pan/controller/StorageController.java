package com.fx.pan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.Storage;
import com.fx.pan.service.StorageService;
import com.fx.pan.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leaving
 * @date 2022/2/13 19:37
 * @version 1.0
 */
@Tag(name = "storage", description = "该接口为用户存储接")
@RestController
@RequestMapping("/storage")
public class StorageController {

    @Resource
    private StorageService storageService;

    @GetMapping("/info")
    public ResponseResult storageInfo(){
        Long userId = SecurityUtils.getUserId();
        Storage userStorage = storageService.getUserStorage(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("storage",userStorage);
        return ResponseResult.success(map);
    }

}
