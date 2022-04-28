package com.fx.pan.controller;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fx.pan.common.Constants;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.Share;
import com.fx.pan.domain.ShareFile;
import com.fx.pan.domain.Storage;
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

    @Autowired
    private UserService userService;
    @Resource
    private ShareService shareService;

    @Resource
    private StorageService storageService;

    @Autowired
    private FileService fileService;

    @Autowired
    private RedisCache redisCache;


    /**
     * 文件分享
     */
    @SneakyThrows
    @PostMapping("/create")
    public Msg shareFile(@RequestBody ShareFileDTO shareSecretDTO) {
        Long userId = SecurityUtils.getUserId();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        FileBean shareFile = fileService.selectFileById(shareSecretDTO.getFileId());
        Share share = new Share();
        // BeanUtil.copyProperties(shareSecretDTO, share);
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
        List<ShareFile> fileList = shareSecretDTO.getFiles();

        return Msg.success("分享成功").put("extractionCode", extractionCode).put("batchNum", uuid).put("share", share);
    }


    @GetMapping("/list")
    public Msg shareList() {
        Long userId = SecurityUtils.getUserId();
        // List<Share> shareFile = shareService.getShareList(userId);
        // List sl = shareService.selectShareAndFileInfo(userId);
        List<ShareFileListVO> list = shareService.selectShareFileList(userId);
        List<ShareFileListVO> listWithoutDuplicates = list.stream().distinct().collect(Collectors.toList());
        listWithoutDuplicates.forEach(item -> {
            Long browseTimes = redisCache.getCacheObject(Constants.REDIS_DATA_PREFIX + item.batchNum + "-bt");
            if (browseTimes != null) {
                item.setBrowseTimes(browseTimes);
            }

        });

        // FileBean fileBean = fileService.selectFileById(shareFile.get(0).getFileId());
        // List fb = new ArrayList();
        // Map map = new HashMap();
        //
        // for (Share share : shareFile) {
        //     map.put("share",share);
        //     FileBean fileBean1 = fileService.selectFileById(share.getFileId());
        //     fb.add(fileBean1);
        //     map.put("file",fb);
        //     fb.add(map);
        // }
        return Msg.success().put("list", listWithoutDuplicates);
    }

    @GetMapping("/sharefile/list")
    public Msg shareFileList(@RequestParam(required = false) Long userId,
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
        // List<FileBean> fileList = fileService.getFileList(filePath, userId,0);
        fileBeanList = fileService.selectFileByParentId(fileBean.getId(), userId);
        // List<FileBean> list = fileService.getShareFileList(filePath,userId);
        // List shareFileListVOList = BeanCopyUtils.copyBeanList(list, ShareFileListVO.class);

        return Msg.success("获取成功").put("list", fileBeanList);
    }

    // @AnonymousAccess
    @GetMapping("/shareinfo")
    public Msg shareInfo(@RequestParam("shareBatchNum") String batchNum) {
        Share share = shareService.selectShareWithBatchNum(batchNum);
        if (share == null) {
            return Msg.error(500, "分享不存在");
        } else {
            FileBean fileBean = fileService.selectFileById(share.getFileId());
            return Msg.success("获取成功").put("share", share).put("file", fileBean);
        }

    }

    @GetMapping("/checkextractioncode")
    public Msg checkExtractionCode(@RequestParam String batchNum, @RequestParam String extractionCode) {
        LambdaQueryWrapper<Share> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Share::getBatchNum, batchNum)
                .eq(Share::getExtractionCode, extractionCode);
        List<Share> list = shareService.list(lambdaQueryWrapper);

        Long browseTimes = redisCache.getCacheObject(Constants.REDIS_DATA_PREFIX + batchNum + "-bt");
        if (browseTimes == null) {
            browseTimes = 1L;
        } else {
            browseTimes = browseTimes + 1;
        }
        redisCache.setCacheObject(Constants.REDIS_DATA_PREFIX + batchNum + "-bt", browseTimes);
        // share.setBrowseTimes(browseTimes);

        if (list.isEmpty()) {
            return Msg.error(500, "分享码错误");
        } else {
            FileBean fileBean = fileService.selectFileById(list.get(0).getFileId());
            return Msg.success("验证成功").put("file", fileBean);
        }

    }

    @PostMapping("savesharefile")
    public Msg saveShareFile(@RequestBody ShareFileSaveDTO shareFileSaveDTO) {
        // Share share = shareService.selectShareWithBatchNum(shareFileSaveDTO.getBatchNum());
        Long userId = SecurityUtils.getUserId();
        Long[] files = shareFileSaveDTO.getFiles();
        Long parentPathId = -1L;
        Long totalFileSize = 0L;
        if (!shareFileSaveDTO.getFilePath().equals("/")) {
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
                // List<FileBean> fileBeanList = fileService.selectFileByParentId(fileBean.getId(),fileBean.getUserId
                // ());
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
                // storageService.updateStorageUse(totalFileSize,userId);
                // boolean b = storageService.updateStorageUse(saveFileBean1.getFileSize(), userId);
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
        Long saveTimes = redisCache.getCacheObject(Constants.REDIS_DATA_PREFIX + shareFileSaveDTO.getBatchNum() +
                "-st");
        if (saveTimes == null) {
            saveTimes = 1L;
        } else {
            saveTimes = saveTimes + 1;
        }
        redisCache.setCacheObject(Constants.REDIS_DATA_PREFIX + shareFileSaveDTO.getBatchNum() + "-st", saveTimes);
        return Msg.success("保存成功").put("userStorage", storage);
    }

    @PostMapping("/cancel")
    public Msg cancelShare(@RequestParam("id") Long id, @RequestParam String batchNum) {
        Long userId = SecurityUtils.getUserId();
        int flag = shareService.deleteShare(id, userId);
        boolean b = redisCache.hasKey(Constants.REDIS_DATA_PREFIX + batchNum + "-st");

        if (redisCache.hasKey(Constants.REDIS_DATA_PREFIX + batchNum + "-st")) {
            redisCache.deleteObject(Constants.REDIS_DATA_PREFIX + id + "-st");
        }
        if (redisCache.hasKey(Constants.REDIS_DATA_PREFIX + batchNum + "-bt")) {
            redisCache.deleteObject(Constants.REDIS_DATA_PREFIX + batchNum + "-bt");
        }
        if (flag == 1) {
            return Msg.success("取消分享成功");
        } else {
            return Msg.error(500, "取消分享失败");
        }
    }

    @PostMapping("/clearinvalid")
    public Msg clearInvalid(@RequestBody ShareFileDTO shareFileDTO) {
        Long userId = SecurityUtils.getUserId();
        List<ShareFile> fileList = shareFileDTO.getFiles();
        for (ShareFile file : fileList) {
            // shareService.clearInvalid(file,userId);
        }
        return Msg.success();
    }


    /**
     * 分页查询所有数据
     *
     * @param page  分页对象
     * @param share 查询实体
     * @return 所有数据
     */
    @GetMapping
    public Msg selectAll(Page<Share> page, Share share) {
        Long userId = SecurityUtils.getUserId();

        Page<Share> page1 = shareService.page(page, new QueryWrapper<>(share));

        return Msg.success().put("list", page1);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Msg selectOne(@PathVariable Serializable id) {
        Share res = shareService.getById(id);
        return Msg.success().put("res", res);
    }

    /**
     * 新增数据
     *
     * @param share 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Msg insert(@RequestBody Share share) {
        boolean save = shareService.save(share);
        return Msg.success().put("res", save);
    }

    /**
     * 修改数据
     *
     * @param share 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Msg update(@RequestBody Share share) {
        boolean b = shareService.updateById(share);
        return Msg.success("修改成功");
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public Msg delete(@RequestParam("idList") List<Long> idList) {
        boolean b = shareService.removeByIds(idList);
        return Msg.success();
    }
}

