package com.fx.pan.controller;

import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.Storage;
import com.fx.pan.factory.fxUtils;
import com.fx.pan.service.FileService;
import com.fx.pan.service.RecycleService;
import com.fx.pan.service.StorageService;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leaving
 * @date 2022/2/6 13:18
 * @version 1.0
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

    @Resource
    private RedisCache redisCache;

    /**
     * 获取回收站文件
     *
     * @return
     */
    @GetMapping("/list")
    public ResponseResult fileRecycle() {
        Long userId = SecurityUtils.getUserId();
        List<FileBean> list = recycleService.selectRecycleByUserId(userId);
        // fileService.getRecycleList(userId);
        Map map = new HashMap();
        map.put("list", list);
        map.put("total", list.size());
        return ResponseResult.success("获取成功",map);
    }

    /**
     *还原回收站文件
     * @param fileList
     */
    @PostMapping("/restore")
    public ResponseResult restoreFile(@RequestBody List<Long> fileList) {
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
        }
        if (flag) {
            boolean b = storageService.updateStorageUse(totalSize, userId);
            Storage storage = storageService.getUserStorage(userId);
            Map map = new HashMap();
            map.put("total", fileList.size());
            map.put("userStorage", storage);
            return ResponseResult.success("还原成功",map);
        } else {
            return ResponseResult.error(500, "还原失败");
        }
    }

    /**
     * 删除回收站文件
     * @param fileList
     * @return
     */
    @ApiOperation(value = "删除回收站文件")
    @PostMapping("/delete")
    public ResponseResult deleteRecycleFile(@RequestBody List<Long> fileList) {
        Long userId = SecurityUtils.getUserId();
        System.out.println(userId);
        for (Long id : fileList) {
            FileBean fileBean = recycleService.selectRecycleFileById(id, userId);
            recycleService.deleteRecycleFileById(id, userId);
            // 删除我看看
            if (fileBean.getIsDir() == 1) {
                if (fileBean.getFilePath().equals("/")) {
                } else {
                    List<FileBean> list = fileService.selectChildFileListByPath(fileBean.getFilePath(), userId);
                    if (list.size() == 0) {
                        List<FileBean> fileBeanList = fileService.selectFileByIdentifier(fileBean.getIdentifier());
                        if (fileBeanList.size() == 1) {
                            File file = fxUtils.getLocalSaveFile(fileBean.getFileUrl());
                            file.delete();
                        }
                        recycleService.deleteRecycleFileById(id, userId);

                    } else {
                        for (FileBean fileBean1 : list) {
                            List<FileBean> fileBeanList = fileService.selectFileByIdentifier(fileBean.getIdentifier());
                            if (fileBeanList.size() == 1) {
                                File file = fxUtils.getLocalSaveFile(fileBean.getFileUrl());
                                file.delete();
                            }
                            recycleService.deleteRecycleFileById(fileBean1.getId(), userId);
                        }
                    }
                }
            } else {
                List<FileBean> fileBeanList = fileService.selectFileByIdentifier(fileBean.getIdentifier());
                System.out.println("删除文件列表fileBeanList:" + fileBeanList);
                if (fileBeanList.size() == 1) {
                    File file = fxUtils.getLocalSaveFile(fileBean.getFileUrl());
                    file.delete();
                }
                recycleService.deleteRecycleFileById(id, userId);
            }
        }
        return ResponseResult.success("删除成功");
    }

    @PostMapping("/clearall")
    public ResponseResult clearRecycleFile() {
        Long userId = SecurityUtils.getUserId();
        int i = recycleService.deleteAllRecycleFileByUserId(userId);
        return ResponseResult.success("清空回收站成功");
    }


}
