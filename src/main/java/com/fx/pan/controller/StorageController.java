package com.fx.pan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.Storage;
import com.fx.pan.service.StorageService;
import com.fx.pan.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author leaving
 * @Date 2022/2/13 19:37
 * @Version 1.0
 */
@Tag(name = "storage", description = "该接口为用户存储接")
@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @GetMapping("/info")
    public Msg storageInfo(){
        Long userId = SecurityUtils.getUserId();
        Storage userStorage = storageService.getUserStorage(userId);
        return Msg.success().put("storage",userStorage);
    }


    @PostMapping("/update")
    public void updateUserStorage(@RequestParam  Long userId, @RequestParam Long fileSize) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        Storage storage = storageService.getUserStorage(userId);
        storage.setStorageSizeUsed(storage.getStorageSizeUsed()+fileSize);
        boolean b = storageService.updateById(storage);

        //
        // UpdateWrapper updateWrapper = new UpdateWrapper();
        // updateWrapper.set("storage_size_used", chunk.getTotalSize()+storage.getStorageSizeUsed());
        // updateWrapper.eq("user_id", userId);
        // storageMapper.update(storage, updateWrapper);

    }
}
