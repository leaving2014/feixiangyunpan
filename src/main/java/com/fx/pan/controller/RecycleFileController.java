package com.fx.pan.controller;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.FileBean;
import com.fx.pan.service.FileService;
import com.fx.pan.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author leaving
 * @Date 2022/2/6 13:18
 * @Version 1.0
 */
@Tag(name = "recycle", description = "该接口为回收站文件操作接口")
@Slf4j
@RequestMapping(value = "/recycle")
@RestController
public class RecycleFileController {

    @Resource
    private FileService fileService;

    /**
     * 获取回收站文件
     * @return
     */
    @GetMapping("/list")
    public Msg fileRecycle(@RequestHeader("Authorization") String token){
        Long userId = SecurityUtils.getUserId();
        List<FileBean> list = fileService.getRecycleList(userId);
        return Msg.success("获取成功").put("list", list).put("count", list.size());
    }

    /**
     *还原回收站文件
     * @param fileList
     */
    @PostMapping("/restore")
    public Msg restoreFile(@RequestBody List<Long> fileList){
        Long userId = SecurityUtils.getUserId();
        for (Long fid : fileList) {
            fileService.restoreFile(fid,userId);
        }
        return Msg.success("还原成功").put("count", fileList.size());
    }

    /**
     * 删除回收站文件
     * @param fileList
     * @return
     */
    @PostMapping("/delete")
    public Msg deleteRecycleFile(@RequestBody List<Long> fileList){
        Long userId = SecurityUtils.getUserId();
        System.out.println(userId);
        for (Long fid : fileList) {
            fileService.deleteRecycleFileById(fid,userId);
        }
        return Msg.success("删除成功");
    }


}
