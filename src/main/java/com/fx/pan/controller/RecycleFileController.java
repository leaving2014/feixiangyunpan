package com.fx.pan.controller;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.Storage;
import com.fx.pan.service.FileService;
import com.fx.pan.service.RecycleService;
import com.fx.pan.service.StorageService;
import com.fx.pan.utils.FileUtils;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
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

    @Resource
    private StorageService storageService;

    @Resource
    private RecycleService recycleService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 获取回收站文件
     *
     * @return
     */
    @GetMapping("/list")
    public Msg fileRecycle() {
        Long userId = SecurityUtils.getUserId();
        List<FileBean> list = recycleService.selectRecycleByUserId(userId);
        // fileService.getRecycleList(userId);
        return Msg.success("获取成功").put("list", list).put("total", list.size());
    }

    /**
     *还原回收站文件
     * @param fileList
     */
    @PostMapping("/restore")
    public Msg restoreFile(@RequestBody List<Long> fileList) {
        Long userId = SecurityUtils.getUserId();
        Long totalSize = 0L;
        boolean flag = false;
        for (Long fid : fileList) {
            FileBean fileBean = recycleService.selectRecycleFileById(fid, userId);

            if (fileBean.getIsDir() == 1) {
                List<FileBean> list = fileService.selectChildFileListByPath(fileBean.getFilePath(), userId);
                for (FileBean fileBean1 : list) {
                    if (fileBean1.getIsDir() == 0) {
                        totalSize += fileBean1.getFileSize();
                    }
                }
            } else {
                totalSize = fileBean.getFileSize();
            }
            flag = fileService.restoreFile(fid, userId);
            // redisCache.
        }
        if (flag) {
            boolean b = storageService.updateStorageUse(totalSize, userId);
            Storage storage = storageService.getUserStorage(userId);
            return Msg.success("还原成功").put("total", fileList.size()).put("userStorage", storage);

        } else {
            return Msg.error(500, "还原失败");
        }
    }

    /**
     * 删除回收站文件
     * @param fileList
     * @return
     */
    @PostMapping("/delete")
    public Msg deleteRecycleFile(@RequestBody List<Long> fileList) {
        Long userId = SecurityUtils.getUserId();
        System.out.println(userId);
        for (Long id : fileList) {
            FileBean fileBean = recycleService.selectRecycleFileById(id, userId);
            recycleService.deleteRecycleFileById(id, userId);
            if (fileBean.getIsDir() == 1) {
                if (fileBean.getFilePath().equals("/")) {
                } else {
                    List<FileBean> list = fileService.selectChildFileListByPath(fileBean.getFilePath(), userId);
                    if (list.size() == 0) {
                        recycleService.deleteRecycleFileById(id, userId);
                    } else {
                        for (FileBean fileBean1 : list) {
                            recycleService.deleteRecycleFileById(fileBean1.getId(), userId);
                        }
                    }
                }
            } else {
                recycleService.deleteRecycleFileById(id, userId);
            }
        }
        return Msg.success("删除成功");
    }

    @PostMapping("/clearall")
    public Msg clearRecycleFile() {
        Long userId = SecurityUtils.getUserId();
        int i = recycleService.deleteAllRecycleFileByUserId(userId);
        return Msg.success("清空回收站成功").put("total", i);
    }


}
