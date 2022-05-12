package com.fx.pan.controller;


import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fx.pan.domain.*;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.common.Constants;
import com.fx.pan.dto.share.ShareFileDTO;
import com.fx.pan.service.FileService;
import com.fx.pan.service.ShareService;
import com.fx.pan.service.StorageService;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.BeanCopyUtils;
import com.fx.pan.utils.DateUtil;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.utils.SecurityUtils;
import com.fx.pan.vo.share.ShareFileListVO;
import com.fx.pan.vo.share.ShareFileSaveDTO;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件分享表(Share)表控制层
 *
 * @author leaving
 * @since 2022-01-26 23:24:21
 */
@RestController
@RequestMapping("/share")
public class ShareController {
    /**
     * 服务对象
     */

    @Resource
    private UserService userService;
    @Resource
    private ShareService shareService;

    @Resource
    private StorageService storageService;

    @Resource
    private FileService fileService;

    @Resource
    private RedisCache redisCache;


    /**
     * 文件分享
     */
    @SneakyThrows
    @PostMapping("/create")
    public ResponseResult createShare(@RequestBody ShareFileDTO shareSecretDTO) {
        Long userId = SecurityUtils.getUserId();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        FileBean shareFile = fileService.selectFileById(shareSecretDTO.getFileId());
        Share share = new Share();
        Date shareTime = DateUtil.getFormatCurrentTime("yyyy-MM-dd HH:mm:ss");
        share.setShareTime(shareTime);
        share.setUpdateTime(shareTime);
        share.setUserId(userId);
        share.setFilePath(shareFile.getFilePath());
        share.setType(shareSecretDTO.getType());
        String extractionCode = RandomUtil.randomString(4);
        share.setExtractionCode(extractionCode);
        share.setBatchNum(uuid);
        share.setStatus(0);
        share.setFileId(shareSecretDTO.getFileId());
        share.setExpired(shareSecretDTO.getExpired());
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date expiredTime = null;
        if (shareSecretDTO.getExpired() == 0) {
            expiredTime = dateFormat2.parse("2099-12-31 00:00:00");
        } else {
            expiredTime = DateUtil.plusDayWithCurrentTime(shareSecretDTO.getExpired());
        }
        share.setExpiredTime(expiredTime);
        shareService.save(share);
        // 设置过期自动删除
        if (shareSecretDTO.getExpired() > 0) {
            Long daySecond =  86400L;
            Long totalSecond = (7 + shareSecretDTO.getExpired()) * daySecond;
            redisCache.set(Constants.REDIS_DELETE_SUFFIX+"-share-uid-"+userId+":"+share.getId(), shareSecretDTO.getExpired(),totalSecond);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("share", share);
        map.put("extractionCode", extractionCode);
        map.put("batchNum", uuid);
        return ResponseResult.success("分享成功", map);
    }


    @GetMapping("/list")
    public ResponseResult shareList() {
        Long userId = SecurityUtils.getUserId();
        List<ShareFileListVO> list = shareService.selectShareFileList(userId);
        List<ShareFileListVO> listWithoutDuplicates = list.stream().distinct().collect(Collectors.toList());
        listWithoutDuplicates.forEach(item -> {
            Double zsetScore = redisCache.getZsetScore(Constants.REDIS_DATA_SUFFIX + "-share-bt", item.getId());
            item.setBrowseTimes(zsetScore == null ? 0L : zsetScore.intValue());
        });
        Map<String, Object> map = new HashMap<>();
        map.put("list", listWithoutDuplicates);
        return ResponseResult.success(map);
    }

    @GetMapping("/sharefile/list")
    public ResponseResult shareFileList(@RequestParam(required = false) Long userId,
                                        @RequestParam(required = false) Long fileId,
                                        @RequestParam(required = false) String batchNum,
                                        @RequestParam(required = false) String path) {
        List fileBeanList;
        FileBean fileBean;
        if (path != null) {
            fileBean = fileService.selectByFilePath(path, userId);
        } else if (batchNum != null) {
            fileBean = fileService.selectFileById(shareService.selectShareWithBatchNum(batchNum).getFileId());
        } else {
            fileBean = fileService.selectFileById(fileId);
        }
        fileBeanList = fileService.selectFileByParentId(fileBean.getId(), userId);
        Map<String, Object> map = new HashMap<>();
        map.put("list", fileBeanList);
        return ResponseResult.success("获取成功", map);
    }

    @GetMapping("/shareinfo")
    public ResponseResult shareInfo(@RequestParam("shareBatchNum") String batchNum) {
        Share share = shareService.selectShareWithBatchNum(batchNum);
        if (share == null) {
            return ResponseResult.error(500, "分享不存在");
        } else {
            FileBean fileBean = fileService.selectFileById(share.getFileId());
            Map<String, Object> map = new HashMap<>();
            map.put("share", share);
            map.put("file", fileBean);
            return ResponseResult.success("获取成功", map);
        }

    }

    @GetMapping("/checkextractioncode")
    public ResponseResult checkExtractionCode(@RequestParam String batchNum, @RequestParam String extractionCode) {
        LambdaQueryWrapper<Share> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Share::getBatchNum, batchNum)
                .eq(Share::getExtractionCode, extractionCode);
        List<Share> list = shareService.list(lambdaQueryWrapper);
        redisCache.updateZset(Constants.REDIS_DATA_SUFFIX + "-share-bt", list.get(0).getId(), 1L);
        if (list.isEmpty()) {
            return ResponseResult.error(500, "分享码错误");
        } else {
            FileBean fileBean = fileService.selectFileById(list.get(0).getFileId());
            Map<String, Object> map = new HashMap<>();
            map.put("file", fileBean);
            return ResponseResult.success("验证成功", map);
        }

    }

    @PostMapping("savesharefile")
    public ResponseResult saveShareFile(@RequestBody ShareFileSaveDTO shareFileSaveDTO) {
        Long userId = SecurityUtils.getUserId();
        Long[] files = shareFileSaveDTO.getFiles();
        Share share = shareService.selectShareWithBatchNum(shareFileSaveDTO.getBatchNum());
        Long parentPathId = -1L;
        Long totalFileSize = 0L;
        if (!"/".equals(shareFileSaveDTO.getFilePath())) {
            FileBean parentFile = fileService.selectByFilePath(shareFileSaveDTO.getFilePath(), userId);
            parentPathId = parentFile.getId();
        }
        String savePath = shareFileSaveDTO.getFilePath();
        for (Long file : files) {
            // 分享的文件对象
            FileBean fileBean = fileService.selectFileById((Long) file);
            // 如果分享文件为目录
            if (fileBean.getIsDir() == 1) {
                FileBean saveFileBean = BeanCopyUtils.copyBean(fileBean, FileBean.class);
                saveFileBean.setFilePath(savePath);
                saveFileBean.setOrigin(1);
                saveFileBean.setUserId(userId);
                saveFileBean.setParentPathId(parentPathId);
                fileService.save(saveFileBean);
                // 获取保存目录的子文件和目录
                List<FileBean> saveShareFileList = fileService.selectChildFileListByPath(fileBean.getFileName(),
                        fileBean.getUserId());
                // 计算分享文件的总大小
                for (FileBean fileBean1 : saveShareFileList) {
                    FileBean saveFileBean1 = BeanCopyUtils.copyBean(fileBean1, FileBean.class);
                    saveFileBean1.setUserId(userId);
                    saveFileBean1.setFilePath(savePath + fileBean1.getFilePath());
                    fileService.save(saveFileBean1);
                    if (fileBean1.getIsDir() == 0) {
                        totalFileSize += fileBean1.getFileSize();
                    }
                }
            } else {
                FileBean saveFile = BeanCopyUtils.copyBean(fileBean, FileBean.class);
                saveFile.setUserId(userId);
                saveFile.setOrigin(1);
                saveFile.setParentPathId(parentPathId);
                saveFile.setFilePath(shareFileSaveDTO.getFilePath());
                fileService.save(saveFile);
                totalFileSize += saveFile.getFileSize();

            }
            storageService.updateStorageUse(totalFileSize, userId);

        }
        Storage storage = storageService.getUserStorage(userId);
        // 更新分享文件的保存次数
        redisCache.updateZset(Constants.REDIS_DATA_SUFFIX + "-share-st", share.getId(), 1L);
        Map<String, Object> map = new HashMap<>();
        map.put("userStorage", storage);
        redisCache.deleteObject("fileList-uid:" + userId);
        return ResponseResult.success("保存成功", map);
    }

    @PostMapping("/cancel")
    public ResponseResult cancelShare(@RequestParam("id") Long id, @RequestParam String batchNum) {
        Long userId = SecurityUtils.getUserId();
        int flag = shareService.deleteShare(id, userId);
        if (redisCache.hasKey(Constants.REDIS_DATA_SUFFIX + "-share-st")) {
            redisCache.deleteZset(Constants.REDIS_DATA_SUFFIX + "-share-st", id);
        }
        if (redisCache.hasKey(Constants.REDIS_DATA_SUFFIX + "-share-bt")) {
            redisCache.deleteZset(Constants.REDIS_DATA_SUFFIX + "-share-bt", id);
        }
        if (flag == 1) {
            return ResponseResult.success("取消分享成功");
        } else {
            return ResponseResult.error(500, "取消分享失败");
        }
    }

    @PostMapping("/clearinvalid")
    public ResponseResult clearInvalid() {
        Long userId = SecurityUtils.getUserId();
        List<Share> shareList = shareService.selectExpireShareFileList(userId);
        System.out.println("shareList===========================" + shareList);
        int i = shareService.deleteExpireShareList(userId);
        if (i > 1) {
            Map<String, Object> map = new HashMap<>();
            map.put("total", i);
            return ResponseResult.success("清除过期分享成功",map);
        } else {
            return ResponseResult.error(0, "没有过期分享");
        }
    }


}

