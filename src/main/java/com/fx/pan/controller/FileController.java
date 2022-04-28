package com.fx.pan.controller;

import com.alibaba.fastjson.JSONObject;
import com.fx.pan.advice.FxException;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.domain.Storage;
import com.fx.pan.dto.file.BatchCopyFileDTO;
import com.fx.pan.dto.file.BatchMoveFileDTO;
import com.fx.pan.dto.file.UnzipFileDTO;
import com.fx.pan.dto.file.UpdateFileDTO;
import com.fx.pan.factory.FxFactory;
import com.fx.pan.factory.operation.download.domain.DownloadFile;
import com.fx.pan.factory.operation.write.Writer;
import com.fx.pan.factory.operation.write.domain.WriteFile;
import com.fx.pan.service.FileService;
import com.fx.pan.service.StorageService;
import com.fx.pan.utils.*;
import com.fx.pan.utils.file.FileTypeUtils;
import com.fx.pan.vo.FileListVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

/**
 * 本地文件操作
 *
 * @Author leaving
 * @Date 2021/11/24 22:24
 * @Version 1.0
 */

@Tag(name = "file", description = "该接口为文件操作接口，主要用来做一些文件的基本操作，如创建目录，删除，移动，复制等。")
@Slf4j
@RequestMapping(value = "/file")
@RestController
public class FileController {


    @Resource
    private FileService fileService;

    @Resource
    private StorageService storageService;


    @Autowired
    private RedisCache redisCache;

    @Resource
    FxFactory fxFactory;

    /**
     * 上传文件信息校验
     *
     * @return
     */
    @PostMapping("/check")
    public Msg checkFiles(@RequestBody FileBean fileBean) {
        return Msg.success("ok").put("state", "成功");

    }


    /**
     * 创建文件夹 本地
     *
     * @param createFile
     * @return
     */
    @PostMapping("/createfolder")
    public Msg createfolder(@RequestBody FileBean createFile) {
        Long userId = SecurityUtils.getUserId();
        createFile.setUserId(userId);
        createFile.setIsDir(1);
        createFile.setFileCreateTime(new Date());
        createFile.setFileUpdateTime(new Date());
        // fileService.se
        if (!"/".equals(createFile.getFilePath())) {
            Long parentpPathId = fileService.selectByFilePath(createFile.getFilePath(), userId).getId();
            createFile.setParentPathId(parentpPathId);
        } else {
            createFile.setParentPathId(-1L);
        }

        boolean folderExist = fileService.isFolderExist(createFile.getFilePath(), createFile.getFileName(),
                userId);
        if (folderExist) {
            return Msg.error(500, "文件夹已存在");
        }
        boolean flag = fileService.createFolder(createFile);
        if (flag) {
            return Msg.success("文件夹创建成功").put("folderName", createFile.getFileName());
        } else {
            return Msg.error(500, "文件夹创建失败");
        }
    }

    /**
     * 文件重命名
     */
    @PostMapping("/rename")
    public Msg rename(@RequestParam Long fileId, @RequestParam String fileName) {
        Long userId = SecurityUtils.getUserId();
        FileBean fileBean = fileService.selectFileById(fileId);
        // 判断 重命名是否是文件夹 ,文件夹重命名需要更新文件夹下所有的子目录和文件的目录
        if (fileBean.getIsDir() == 1) {
            // 先对重命名的子目录和文件重命名
            List<FileBean> childFileList = fileService.selectChildFileListByPath('/' + fileBean.getFileName(), userId);
            log.info(childFileList.toString());
            for (FileBean file : childFileList) {
                String orginFilePath = file.getFilePath();
                String newPath = orginFilePath.replace(fileBean.getFileName(), fileName);
                fileService.updateFilePathById(file.getId(), newPath);
            }
            // 重命名用户重命名的文件
            boolean flag = fileService.renameFile(fileId, fileName);
            return Msg.success("重命名成功").put("newFileName", fileName);
        } else {
            boolean flag = fileService.renameFile(fileId, fileName);
            if (flag) {
                return Msg.success("重命名成功").put("newFileName", fileName);
            } else {
                return Msg.error(500, "重命名失败");
            }
        }
    }

    /**
     * 文件复制
     */

    @PostMapping("/copy")
    public Msg copyFile(@RequestParam Long fileId, @RequestParam String filePath) {
        Long userId = SecurityUtils.getUserId();
        Msg msg = fileService.copyFile(fileId, filePath, userId);
        return msg;
    }

    /**
     * 文件移动
     */
    @PostMapping("/move")
    public Msg moveFile(@RequestParam Long fileId, @RequestParam String filePath) {
        Long userId = SecurityUtils.getUserId();
        fileService.moveFile(fileId, filePath, userId);
        return Msg.success("移动成功");
    }

    /**
     * 文件删除
     */
    @PostMapping("/delete")
    public Msg deleteFile(@RequestParam Long id) {
        Long userId = SecurityUtils.getUserId();
        FileBean deleteFile = fileService.selectFileById(id);
        boolean flag = false;
        Long totalSize = 0L;
        // 删除对象是文件夹
        if (deleteFile.getIsDir() == 1) {
            // 获取删除文件夹下的所有文件
            List<FileBean> subFileList = fileService.selectChildFileListByPath(deleteFile.getFileName(), userId);
            for (FileBean file : subFileList) {

                if (file.getIsDir() == 0) {
                    totalSize += file.getFileSize();
                }
                // flag = fileService.deleteFile(id, userId);
            }
            flag = fileService.deleteFile(id, userId);
        } else {
            totalSize += deleteFile.getFileSize();
            flag = fileService.deleteFile(id, userId);
            String key = "fileList-uid:" + userId;
            redisCache.deleteCacheList(key, deleteFile);
        }

        if (flag) {
            storageService.updateStorageUse(-totalSize, userId);
            Storage storage = storageService.getUserStorage(userId);
            return Msg.success("删除成功").put("userStorage", storage);
        } else {
            return Msg.error(500, "删除失败");
        }
    }

    // 获取文件详细信息
    @PostMapping("/detail")
    public Msg getFileDetail(@RequestParam Long id) {
        FileBean fileBean = fileService.selectFileById(id);
        return Msg.success("获取文件详细信息成功").put("file", fileBean);
    }

    @PostMapping("/refresh")
    public Msg refresh() {
        Long userId = SecurityUtils.getUserId();
        String key = "fileList-uid:" + userId;
        List<FileBean> fl = null;
        fl = fileService.getFileList("/", userId, 0);
        if (redisCache.hasKey(key)) {
            redisCache.deleteObject(key);
            if (fl.size() > 0) {
                redisCache.setCacheList(key, fl);
            }
        }
        return Msg.success("ok").put("ts", System.currentTimeMillis()).put("total", fl.size());
    }

    /**
     * 根据路径获取所有文件列表
     *
     * @return
     */
    @GetMapping("/list")
    public Msg fileList(@RequestParam(required = false, defaultValue = "/") String filePath, @RequestParam(required =
            false, defaultValue = "0") Integer dir, @RequestParam Boolean refresh) {
        Long userId = SecurityUtils.getUserId();
        String key = "fileList-uid:" + userId;
        List<FileBean> fl = null;
        // 判断是否是否强制刷新
        if (refresh) {
            fl = fileService.getFileList(filePath, userId, (Integer) dir);
            redisCache.deleteObject(key);
            if (fl.size() > 0) {
                redisCache.setCacheList(key, fl);
            }
        } else {
            if (filePath.equals("/")) {
                if (redisCache.hasKey(key)) {
                    fl = redisCache.getCacheList(key);
                } else {
                    fl = fileService.getFileList(filePath, userId, (Integer) dir);
                    // 文件列表不为空缓存到redis中
                    if (fl.size() > 0) {
                        redisCache.deleteObject(key);
                        redisCache.setCacheList(key, fl);
                    }
                }
            } else {
                if (dir == null) {
                    fl = fileService.getFileList(filePath, userId, (Integer) dir);
                } else {
                    fl = fileService.getFileList(filePath, userId, (Integer) dir);
                }
            }
        }

        //判断redis中是否有键为key的缓存


        return Msg.success("获取成功").put("list", fl).put("path", filePath).put("total", fl.size());
    }

    /**
     * 按文件分类获取文件 默认值为9,未知文件
     *
     * @param fileType
     * @return
     */
    @GetMapping("/list/type")
    public Msg fileListOfType(@RequestParam(required = false, defaultValue = "9") int fileType,
                              long currentPage,
                              long pageCount) {
        Long userId = SecurityUtils.getUserId();

        List<FileListVo> fileList = new ArrayList<>();

        // redis缓存
        // String key = "fileList-type="+fileType+"-uid:"+userId ;
        // List<FileBean> fl = null;
        // //判断redis中是否有键为key的缓存
        // boolean hasKey = redisCache.hasKey(key);
        // if(hasKey && currentPage == 1) {
        //     System.out.println("redisCachehasKey============"+key);
        //     fl = redisCache.getCacheObject(key);
        // } else {
        //     fl = fileService.getFileList(filePath, userId);
        //     redisCache.deleteObject(key);
        //     redisCache.setCacheObject(key, fl);
        // }

        Long beginCount = 0L;
        if (pageCount == 0 || currentPage == 0) {
            beginCount = 0L;
            pageCount = 10L;
        } else {
            beginCount = (currentPage - 1) * pageCount;
        }
        if (fileType == FileTypeUtils.OTHER_TYPE) {

            List<String> arrList = new ArrayList<>();
            arrList.addAll(Arrays.asList(FileTypeUtils.DOC_FILE));
            arrList.addAll(Arrays.asList(FileTypeUtils.IMG_FILE));
            arrList.addAll(Arrays.asList(FileTypeUtils.VIDEO_FILE));
            arrList.addAll(Arrays.asList(FileTypeUtils.ZIP_FILE));
            arrList.addAll(Arrays.asList(FileTypeUtils.Audio_FILE));

            fileList = fileService.selectFileNotInExtendNames(arrList, beginCount, pageCount, userId);
            // total = fileService.selectCountNotInExtendNames(arrList, beginCount, pageCount, userId);
        } else {
            fileList = fileService.selectFileByExtendName(FileTypeUtils.getFileExtendsByType(fileType), beginCount,
                    pageCount, userId);
            // total = fileService.selectCountByExtendName(FileTypeUtils.getFileExtendsByType(fileType), beginCount,
            //         pageCount, userId);
        }

        // Map<String, Object> map = new HashMap<>();
        // map.put("list",fileList);
        // map.put("total", total);

        return Msg.success("获取成功").put("list", fileList).put("total", fileList.size());
    }

    /**
     * 获取指定扩展名的文件列表
     *
     * @param extName
     * @return
     */
    @GetMapping("/list/type/ext")
    public Msg fileListOfTypeExt(@RequestParam String extName) {
        List<String> list = new ArrayList<>();

        list.add(extName);
        List<FileListVo> fileList = fileService.selectFileByExtendName(list, 1L, 100L, SecurityUtils.getUserId());
        return Msg.success("获取成功").put("list", fileList);
    }


    /**
     * 获取文件树
     *
     * @return
     */
    @GetMapping("/filetree")
    public Msg getFileTree() {
        // fileService.getFileTree();
        Long userId = SecurityUtils.getUserId();
        return Msg.success("文件树获取成功");
    }

    /**
     * 解压文件
     */
    @PostMapping("/unzip")
    public Msg unzipFile(@RequestBody UnzipFileDTO unzipFileDto) {
        Long userId = SecurityUtils.getUserId();
        FileBean fileBean = fileService.selectFileById(unzipFileDto.getFileId());
        Long fileSize = fileBean.getFileSize();
        Storage storage = storageService.getUserStorage(fileBean.getUserId());
        Long remainingSize = storage.getStorageSize() - storage.getStorageSizeUsed();

        if (fileSize > remainingSize) {
            return Msg.error(500, "空间不足,请先扩容或删除文件后再解压");
        }
        // 解压到以文件名命名的目录时 创建目录
        if (unzipFileDto.getUnzipMode() == 2) {
            // String fullPath = unzipFileDto.getFilePath();
            // // 获取fullPath最后一个斜杠后的字符串
            // String fileName = fullPath.substring(fullPath.lastIndexOf("/") + 1);
            // // 获取fullPath最后一个斜杠前的字符串
            // String filePath = fullPath.substring(0, fullPath.lastIndexOf("/"));
            // // 获取
            // FileBean parentFile = fileService.selectByFilePath(filePath, userId);
            // String parentPath = fileBean.getFilePath();
            FileBean unzipRoot = new FileBean();
            unzipRoot.setParentPathId(fileBean.getParentPathId());
            unzipRoot.setFileName(unzipFileDto.getFilePath().substring(unzipFileDto.getFilePath().lastIndexOf("/") + 1));
            unzipRoot.setUserId(userId);
            unzipRoot.setIsDir(1);
            unzipRoot.setFileCreateTime(new Date());
            unzipRoot.setFileUpdateTime(new Date());
            System.out.println("创建根文件夹:" + unzipRoot);
            fileService.createFolder(unzipRoot);
        }
        boolean b = fileService.unzip(unzipFileDto.getFileId(), unzipFileDto.getUnzipMode(),
                unzipFileDto.getFilePath());
        return Msg.success("解压成功").put("filePath", unzipFileDto.getFilePath());
    }


    /**
     * 文件搜索
     *
     * @return
     */
    @GetMapping("/search")
    public Msg search(@RequestParam String keywords) {
        Long userId = SecurityUtils.getUserId();
        List list = fileService.searchFile(keywords, userId);
        List list1 = BeanCopyUtils.copyBeanList(list, FileListVo.class);
        return Msg.success("").put("list", list1);
    }

    @GetMapping("/clean")
    public Msg clean() {
        Long userId = SecurityUtils.getUserId();
        return fileService.cleanFile(userId);
    }


    /**
     * 批量复制文件
     */
    @PostMapping("/batchcopy")
    public Msg batchCopy(@RequestBody BatchCopyFileDTO batchCopyFileDTO) {
        Long userId = SecurityUtils.getUserId();
        List<FileBean> fileList = batchCopyFileDTO.getFileList();
        String filePath = batchCopyFileDTO.getFilePath();
        for (FileBean file : fileList) {
            Long fileId = file.getId();
            // 批量复制的文件夹
            FileBean fileBean = fileService.selectFileById(fileId);
            System.out.println(fileBean);
            // 判断 批量复制是否是文件夹 ,文件夹批量复制需要更新文件夹下所有的子目录和文件的目录
            if (fileBean.getIsDir() == 1) {
                // TODO 复制的是文件夹
                return Msg.error(500, "文件夹不能批量复制");

            } else {
                fileService.copyFile(fileId, filePath, userId);
            }
        }
        return Msg.success("批量复制成功").put("total", fileList.size());
    }

    /**
     * 批量移动文件
     */
    @PostMapping("/batchmove")
    public Msg batchMove(@RequestBody BatchMoveFileDTO batchMoveFileDTO) {
        Long userId = SecurityUtils.getUserId();
        List<FileBean> fileList = batchMoveFileDTO.getFileList();
        String targetPath = batchMoveFileDTO.getFilePath();
        // 批量移动
        for (FileBean file : fileList) {
            Long fileId = file.getId();
            // 移动的文件对象
            FileBean fileBean = fileService.selectFileById(fileId);
            // 判断 批量移动是否是文件夹 ,文件夹批量移动需要更新文件夹下所有的子目录和文件的目录
            if (fileBean.getIsDir() == 1) {
                // 对文件夹进行遍历移动
                // 获取移动文件夹的所有子文件
                List<FileBean> fileBeanList = fileService.selectChildFileListByPath("/" + fileBean.getFileName(),
                        userId);
                int childFileLength = fileBeanList.size();
                while (childFileLength > 0) {
                    for (FileBean fb : fileBeanList) {
                        if ("/".equals(targetPath)) {
                            String newPath = fb.getFilePath().replace(fileBean.getFilePath(), "");
                            fileService.moveFile(fb.getId(), newPath, userId);
                        } else {
                            fileService.moveFile(fb.getId(), targetPath + fb.getFilePath(), userId);

                        }
                        childFileLength--;
                    }
                }
                fileService.moveFile(fileId, targetPath, userId);

            } else {
                // 对单个文件移动
                fileService.moveFile(fileId, targetPath, userId);
            }
        }
        return Msg.success("批量移动成功").put("total", fileList.size());
    }

    /**
     * 批量删除文件
     *
     * @return
     */
    @PostMapping("/batchdelete")
    public Msg batchdelete(@RequestBody List<Long> fileList) {
        Long userId = SecurityUtils.getUserId();
        Long totalSize = 0L;
        for (Long fid : fileList) {
            FileBean fileBean = fileService.selectFileById(fid);
            if (fileBean.getIsDir() == 1) {
                List<FileBean> subFileList = fileService.selectChildFileListByPath(fileBean.getFileName(), userId);
                for (FileBean fb : subFileList) {
                    if (fb.getIsDir() == 0) {
                        totalSize += fb.getFileSize();
                    }
                }
            } else {
                totalSize += fileBean.getFileSize();
            }
            fileService.deleteFile(fid, userId);
        }
        return Msg.success("批量删除成功").put("total", fileList.size());
    }


    @Operation(summary = "修改文件", description = "支持普通文本类文件的修改", tags = {"file"})
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Msg updateFile(@RequestBody UpdateFileDTO updateFileDTO) {
        // JwtUser sessionUserBean =  SessionUtil.getSession();
        LoginUser loginUser = SecurityUtils.getLoginUser();

        FileBean fileBean = fileService.selectFileById(updateFileDTO.getFileId());
        FileBean orginFileBean = BeanCopyUtils.copyBean(fileBean, FileBean.class);
        String identifier = fileBean.getIdentifier();
        // Long pointCount = fileService.getFilePointCount(userFile.getFileId());
        // if (pointCount > 1) {
        //     return Msg.error(550,"暂不支持修改");
        // }
        String content = updateFileDTO.getFileContent();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes());
        try {

            Writer writer1 = fxFactory.getWriter(fileBean.getStorageType());
            System.out.println("Writer==" + writer1);
            WriteFile writeFile = new WriteFile();
            writeFile.setFileUrl(fileBean.getFileUrl());
            int fileSize = byteArrayInputStream.available();
            writeFile.setFileSize(fileSize);
            writer1.write(byteArrayInputStream, writeFile);
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.setFileUrl(fileBean.getFileUrl());
            InputStream inputStream = fxFactory.getDownloader(fileBean.getStorageType()).getInputStream(downloadFile);
            // String md5Str = DigestUtils.md5Hex(String.valueOf(inputStream));
            // System.out.println("文件更新后MD5====" + md5Str);

            File newFile = new File(FileUtils.getLocalStorageFilePathByFileBean(orginFileBean));

            String md5HashCode32 = Md5Utils.md5HashCode32(new FileInputStream(newFile));
            System.out.println("新文件的md5===" + md5HashCode32);
            fileBean.setIdentifier(md5HashCode32);
            String newFileUrl = orginFileBean.getFileUrl().replace(identifier, md5HashCode32);
            fileBean.setFileUrl(newFileUrl);
            fileBean.setFileUpdateTime(new Date());
            // fileBean.setModifyUserId(loginUser.getUserId());
            fileBean.setFileSize((long) fileSize);
            System.out.println(FileUtils.getLocalStorageFilePathByFileBean(orginFileBean).replace(orginFileBean.getIdentifier(), md5HashCode32));
            // fileService.updateById(fileBean);
            // File file = new File(FileUtils.getLocalStorageFilePathByFileBean(orginFileBean));
            // file.delete();
        } catch (Exception e) {
            throw new FxException(999999, "修改文件异常");
        } finally {
            try {
                byteArrayInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Msg.success("修改文件成功");
    }

    @GetMapping("clear/list")
    @ResponseBody
    public Msg clearFile(@RequestParam Long fileSize) {
        Long userId = SecurityUtils.getUserId();
        List<FileBean> fileBeanList = fileService.selectFileWithFileSize(fileSize, userId);
        // fileService.selectFileByUpdateTime(fileBean);
        return Msg.success("获取清理文件列表成功").put("list", fileBeanList);
    }

    /**
     * 获取文件操作进度 如解压
     *
     * @param fid  文件id
     * @param type 操作类型
     * @return
     */
    @GetMapping("/progress")
    @ResponseBody
    public Msg progress(@RequestParam Long fid, @RequestParam String type) {
        Long userId = SecurityUtils.getUserId();
        Object cacheObject = redisCache.getCacheObject(type + "-" + fid + "-" + userId);
        if (cacheObject != null) {
            return Msg.success("ok").put("progress", cacheObject);
        } else {
            return Msg.success("ok").put("progress", 100);
        }
    }

}
