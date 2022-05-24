package com.fx.pan.controller;

import com.fx.pan.advice.FxException;
import com.fx.pan.annotation.Limit;
import com.fx.pan.common.Constants;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.Storage;
import com.fx.pan.dto.file.*;
import com.fx.pan.factory.FxFactory;
import com.fx.pan.factory.FxUtils;
import com.fx.pan.factory.operation.download.domain.DownloadFile;
import com.fx.pan.factory.operation.write.Writer;
import com.fx.pan.factory.operation.write.domain.WriteFile;
import com.fx.pan.service.FileService;
import com.fx.pan.service.StorageService;
import com.fx.pan.utils.*;
import com.fx.pan.utils.file.FileTypeUtils;
import com.fx.pan.vo.FileListVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件操作
 *
 * @author leaving
 * @date 2021/11/24 22:24
 * @version 1.0
 */

@Tag(name = "file", description = "该接口为文件操作接口，主要用来做一些文件的基本操作，如创建目录，删除，移动，复制等。")
@Slf4j
@RequestMapping(value = "/file")
@RestController
public class FileController {

    @Value("${fx.absoluteFilePath}")
    String absoluteFilePath;

    @Value("${fx.storageType}")
    Integer storageType;


    @Resource
    private FileService fileService;

    @Resource
    private StorageService storageService;


    @Resource
    private RedisCache redisCache;

    @Resource
    FxFactory fxFactory;


    // 获取文件信息
    @GetMapping("/info")
    public ResponseResult getFileInfo(@RequestParam("id") String id) {
        FileBean fileBean = fileService.selectFileById(Long.parseLong(id));
        return ResponseResult.success("获取文件信息成功",new HashMap<>().put("file", fileBean));
    }


    /**
     * 新建文件夹
     *
     * @param createFile
     * @return
     */
    @PostMapping("/createfolder")
    public ResponseResult createfolder(@RequestBody FileBean createFile) {
        Long userId = SecurityUtils.getUserId();
        createFile.setUserId(userId);
        createFile.setIsDir(1);
        createFile.setFileCreateTime(new Date());
        createFile.setFileUpdateTime(new Date());
        if (!"/".equals(createFile.getFilePath())) {
            Long parentpPathId = fileService.selectByFilePath(createFile.getFilePath(), userId).getId();
            createFile.setParentPathId(parentpPathId);
        } else {
            redisCache.deleteObject(Constants.REDIS_FILE_LIST_PREFIX + userId);
            createFile.setParentPathId(-1L);
        }
        boolean folderExist = fileService.isFolderExist(createFile.getFilePath(), createFile.getFileName(), userId);
        if (folderExist) {
            return ResponseResult.error(500, "文件夹已存在");
        }
        boolean flag = fileService.createFolder(createFile);
        if (flag) {
            return ResponseResult.success("文件夹创建成功");
        } else {
            return ResponseResult.error(500, "文件夹创建失败");
        }
    }

    /**
     * 文件重命名
     */
    @PostMapping("/rename")
    public ResponseResult rename(@RequestParam Long userId, @RequestParam Long fileId, @RequestParam String fileName,
                                 @RequestParam("filePath") String filePath,@RequestParam("isDir") Integer isDir) {
        FileBean fileBean1 = fileService.selectFileByNameAndPath(fileName, filePath, userId,isDir);
        boolean flag;
        if (fileBean1 != null) {
            return ResponseResult.error(500, "文件名已存在");
        }
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
            // 重命名文件夹
            flag = fileService.renameFile(fileId, fileName);
        } else {
            flag = fileService.renameFile(fileId, fileName+"."+fileBean.getFileExt());

        }
        if (flag) {
            return ResponseResult.success("重命名成功");
        } else {
            return ResponseResult.error(500, "重命名失败");
        }
    }

    /**
     * 文件复制
     */
    @PostMapping("/copy")
    public ResponseResult copyFile(@RequestParam Long userId, @RequestParam Long fileId, @RequestParam String filePath) {
        if (filePath.endsWith("/")) {
            redisCache.deleteObject(Constants.REDIS_FILE_LIST_PREFIX + userId);
        }
        ResponseResult msg = fileService.copyFile(fileId, filePath, userId);
        return msg;
    }

    /**
     * 文件移动
     */
    @PostMapping("/move")
    public ResponseResult moveFile(@RequestParam Long userId, @RequestParam Long fileId,@RequestParam String filePath) {
        if (filePath.endsWith("/")) {
            redisCache.deleteObject(Constants.REDIS_FILE_LIST_PREFIX+ userId);
        }
        FileBean fileBean = fileService.selectFileById(fileId);
        if (fileBean.getFilePath().equals(filePath)) {
            return ResponseResult.error(500, "不能移动到原来的位置");
        }
        if (fileBean.getFilePath().equals("/")){
            redisCache.deleteObject(Constants.REDIS_FILE_LIST_PREFIX+ userId);
        }
        fileService.moveFile(fileId, filePath, userId);
        return ResponseResult.success("移动成功");
    }

    /**
     * 文件删除
     */
    @ApiOperation(value = "删除文件")
    @PostMapping("/delete")
    public ResponseResult deleteFile(@RequestParam Long userId, @RequestParam Long id) {
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
            }
            flag = fileService.deleteFile(id, userId);
            if (flag) {
                redisCache.set(Constants.REDIS_DELETE_SUFFIX+"-file-uid-"+userId+":"+id, id,
                        3600 * 24 * 10);
            }
        } else {
            totalSize += deleteFile.getFileSize();
            flag = fileService.deleteFile(id, userId);
            if (flag) {
                redisCache.set(Constants.REDIS_DELETE_SUFFIX+"-file-uid-"+userId+":"+id, id,
                        3600 * 24 * 10);
            }
            String key = Constants.REDIS_FILE_LIST_PREFIX + userId;
            redisCache.deleteCacheList(key, deleteFile);
        }
        if (flag) {
            storageService.updateStorageUse(-totalSize, userId);
            Storage storage = storageService.getUserStorage(userId);
            Map<String, Object> map = new HashMap<>();
            map.put("userStorage", storage);
            return ResponseResult.success("删除成功",map);
        } else {
            return ResponseResult.error(500, "删除失败");
        }
    }

    /**
     * 获取文件详细信息
     * @param id
     * @return
     */
    @PostMapping("/detail")
    public ResponseResult getFileDetail(@RequestParam Long id) {
        FileBean fileBean = fileService.selectFileById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("file", fileBean);
        return ResponseResult.success("获取文件详细信息成功",map);
    }

    @PostMapping("/refresh")
    public ResponseResult refresh() {
        Long userId = SecurityUtils.getUserId();
        String key = Constants.REDIS_FILE_LIST_PREFIX + userId;
        List<FileBean> fl = null;
        fl = fileService.getFileList("/", userId, 0);
        if (redisCache.hasKey(key)) {
            redisCache.deleteObject(key);
            if (fl.size() > 0) {
                redisCache.setCacheList(key, fl);
            }
        }
        Map map = new HashMap();
        map.put("fileList", fl);
        map.put("ts", System.currentTimeMillis());
        map.put("total",fl.size());
        return ResponseResult.success(map);
    }

    /**
     * 根据路径获取所有文件列表
     * @return
     */
    @Limit(key = "limit1", permitsPerSecond = 1, timeout = 500, msg = "请求过于频繁，请稍后再试！请求频率限制为1次/秒")
    @GetMapping("/list")
    // @CacheEvict("'fileList-uid:' + #p0") //该注解用于删除缓存
    public ResponseResult fileList(@RequestParam("userId") Long userId,
                                   @RequestParam(required = false, defaultValue = "/") String filePath, @RequestParam(required =
            false, defaultValue = "0") Integer dir, @RequestParam Boolean refresh) {
        String key = Constants.REDIS_FILE_LIST_PREFIX + userId;
        List<FileBean> fl = null;
        Integer auditFileCount= 0;
        Integer auditAccessCount = 0;
        Map map = new HashMap();
        // 判断是否是否强制刷新
        if (refresh) {
            fl = fileService.getFileList(filePath, userId, dir);
            redisCache.deleteObject(key);
            if (fl.size() > 0) {
                redisCache.setCacheList(key, fl);
            }
        } else {
            if (filePath.equals("/")) {
                auditFileCount = fileService.getAuditFileCount(userId,false);
                auditAccessCount = fileService.getAuditFileCount(userId,true);
                map.put("auditCount", auditFileCount);
                if (redisCache.hasKey(key)) {
                    fl = redisCache.getCacheList(key);
                } else {
                    fl = fileService.getFileList(filePath, userId, (Integer) dir);
                    // 文件列表不为空缓存到redis中
                    if (!fl.isEmpty()) {
                        redisCache.deleteObject(key);
                        redisCache.setCacheList(key, fl);
                    }
                }
            } else {
                if (dir == null) {
                    fl = fileService.getFileList(filePath, userId, dir);
                } else {
                    fl = fileService.getFileList(filePath, userId, dir);
                }
            }
        }
        map.put("list", fl);
        map.put("path", filePath);
        map.put("total", auditAccessCount);
        map.put("ts", System.currentTimeMillis());
        return ResponseResult.success("获取成功",map);
    }

    /**
     * 按文件分类获取文件 默认值为9,未知文件
     * @param fileType
     * @return
     */
    @GetMapping("/list/type")
    public ResponseResult fileListOfType(@RequestParam(required = false, defaultValue = "9") int fileType,
                                         long currentPage,
                                         long pageCount) {
        Long userId = SecurityUtils.getUserId();

        List<FileListVo> fileList;
        Long beginCount;
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

        } else {
            fileList = fileService.selectFileByExtendName(FileTypeUtils.getFileExtendsByType(fileType), beginCount,
                    pageCount, userId);

        }

        Map<String, Object> map = new HashMap<>();
        map.put("list",fileList);
        map.put("total", fileList.size());
        return ResponseResult.success("获取成功",map);
    }
    
    @GetMapping("/list/offline")
    public ResponseResult fileListOfOffline(){
        Long userId = SecurityUtils.getUserId();
        List<FileBean> fileList = fileService.selectOfflineFileList(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("list",fileList);
        map.put("total", fileList.size());
        return ResponseResult.success("获取成功",map);
    }

    /**
     * 获取指定扩展名的文件列表
     *
     * @param extName
     * @return
     */
    @GetMapping("/list/type/ext")
    public ResponseResult fileListOfTypeExt(@RequestParam String extName) {
        List<String> list = new ArrayList<>();

        list.add(extName);
        List<FileListVo> fileList = fileService.selectFileByExtendName(list, 1L, 100L, SecurityUtils.getUserId());
        return ResponseResult.success("获取成功",fileList);
    }


    /**
     * 解压文件
     */
    @PostMapping("/unzip")
    public ResponseResult unzipFile(@RequestBody UnzipFileDTO unzipFileDto) {
        Long userId = SecurityUtils.getUserId();
        String type = "unzip";
        FileBean fileBean = fileService.selectFileById(unzipFileDto.getFid());
        Long fileSize = fileBean.getFileSize();
        Storage storage = storageService.getUserStorage(fileBean.getUserId());
        Long remainingSize = storage.getStorageSize() - storage.getStorageSizeUsed();

        if (fileSize > remainingSize) {
            return ResponseResult.error(500, "空间不足,请先扩容或删除文件后再解压");
        }
        redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + type + "-" + userId + "-file"+
                ":" + unzipFileDto.getT(),fileBean);
        redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + type + "-" + userId +
                        ":" + unzipFileDto.getT(),
                0);
        // 解压到以文件名命名的目录时 创建目录
        if (unzipFileDto.getUnzipMode() == 2) {
            FileBean unzipRoot = new FileBean();
            unzipRoot.setParentPathId(fileBean.getParentPathId());
            unzipRoot.setFileName(unzipFileDto.getFilePath().substring(unzipFileDto.getFilePath().lastIndexOf("/") + 1));
            String folderPath = unzipFileDto.getFilePath().substring(0, unzipFileDto.getFilePath().lastIndexOf("/"));
            if (folderPath.length() <=1) {
                folderPath = "/";
            }
            unzipRoot.setFilePath(folderPath);
            unzipRoot.setUserId(userId);
            unzipRoot.setIsDir(1);
            unzipRoot.setFileCreateTime(new Date());
            unzipRoot.setFileUpdateTime(new Date());

            boolean flag = fileService.isFolderExist(unzipRoot.getFilePath(), unzipRoot.getFileName(), userId);
            if (!flag) {
                System.out.println("文件夹不存在,创建根文件夹:" + unzipRoot);
                fileService.createFolder(unzipRoot);
            }
        }
        boolean b = fileService.unzip(unzipFileDto.getFid(), unzipFileDto.getUnzipMode(),
                unzipFileDto.getFilePath(),unzipFileDto.getT(),userId);
        Map map = new HashMap();
        map.put("filePath", unzipFileDto.getFilePath());
        return ResponseResult.success("任务创建成功",map);
    }


    /**
     * 文件搜索
     *
     * @return
     */
    @GetMapping("/search")
    public ResponseResult search(@RequestParam String keywords) {
        Long userId = SecurityUtils.getUserId();
        List list = fileService.searchFile(keywords, userId);
        List list1 = BeanCopyUtils.copyBeanList(list, FileListVo.class);
        return ResponseResult.success(list1);
    }



    /**
     * 批量复制文件
     */
    @PostMapping("/batchcopy")
    public ResponseResult batchCopy(@RequestBody BatchCopyFileDTO batchCopyFileDTO) {

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
                return ResponseResult.error(500, "文件夹不能批量复制");
            } else {
                fileService.copyFile(fileId, filePath, userId);
            }
        }
        Map map = new HashMap();
        map.put("total", fileList.size());
        return ResponseResult.success("批量复制成功",map);
    }

    /**
     * 批量移动文件
     */
    @PostMapping("/batchmove")
    public ResponseResult batchMove(@RequestBody BatchMoveFileDTO batchMoveFileDTO) {
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
        Map map = new HashMap();
        map.put("total", fileList.size());
        return ResponseResult.success("批量移动成功",map);
    }

    /**
     * 批量删除文件
     *
     * @return
     */
    @PostMapping("/batchdelete")
    public ResponseResult batchdelete(@RequestBody List<Long> fileList) {
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
        return ResponseResult.success("批量删除成功");
    }


    /**
     * 实时修改文件
     * @param updateFileDTO
     * @return
     * @throws ParseException
     * @throws IOException
     */
    @Operation(summary = "修改文件", description = "支持普通文本类文件的修改", tags = {"file"})
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult updateFile(@RequestBody UpdateFileDTO updateFileDTO) throws ParseException, IOException,
            InterruptedException {
        // JwtUser sessionUserBean =  SessionUtil.getSession();
        Long userId = SecurityUtils.getUserId();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        FileBean createFileBean = new FileBean();
        redisCache.deleteObject("fileList-uid:" + userId);
        String date = DateUtil.getDateByTimeStamp(updateFileDTO.getTimestamp());
        if (updateFileDTO.getFileId() == 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // Date date = sdf.parse(updateFileDTO.getTimestamp());

            String fileName = "新建笔记-" + date + ".md";
            createFileBean.setFileName(fileName);
            createFileBean.setFilePath("/");
            createFileBean.setFileExt("md");
            createFileBean.setUserId(userId);
            createFileBean.setIsDir(0);
            createFileBean.setFileType(2);
            createFileBean.setAudit(1);
            createFileBean.setFileSize((long) updateFileDTO.getFileContent().getBytes().length);
            createFileBean.setFileCreateTime(new Date());
            createFileBean.setFileUpdateTime(new Date());
            // 生成文件唯一识别码
            String uuid = UUID.randomUUID().toString().replace("-", "");
            createFileBean.setIdentifier(uuid);

            String realFileName = uuid + ".md";
            createFileBean.setFileUrl("/" + date + "/" + realFileName);
            File folder = new File(com.fx.pan.factory.FxUtils.getStaticPath() + "/" + date );
            System.out.println("folder:" + folder.getAbsolutePath());
            if (!folder.exists()) {
                folder.mkdirs();
            }            boolean b = fileService.insertFileInfo(createFileBean);
            // 创建文件
            File file = new File(FxUtils.getStaticPath() + "/" + date + "/" + realFileName);    // 创建文件
            if (!file.exists()) {
                file.createNewFile();
            }
            // return ResponseResult.success("文件创建成功").put("file", createFileBean);
        }
        FileBean fileBean = null;
        if (updateFileDTO.getFileId() != 0) {
            fileBean = fileService.selectFileById(updateFileDTO.getFileId());
        } else {
            fileBean = fileService.selectFileById(createFileBean.getId());
        }
        if (fileBean.getOrigin() == 1) {
            String sourceFilePath = FxUtils.getStaticPath() + "/" + fileBean.getFileUrl();
            // 文件是引用文件则把源文件复制一份
            String identifier = UUID.randomUUID().toString().replace("-", "");
            String fileUrl = date + "/" + identifier + "." +fileBean.getFileExt();
            fileBean.setIdentifier(identifier);
            fileBean.setFileUrl(fileUrl);
            fileBean.setOrigin(0);
            fileBean.setFileCreateTime(new Date());
            String targetFilePath = FxUtils.getStaticPath() + "/" + fileUrl;
            FileUtils.copyFileUsingStream(new File(sourceFilePath), new File(targetFilePath));
            fileService.updateById(fileBean);
            Thread.sleep(1000);
        }

        FileBean orginFileBean = BeanCopyUtils.copyBean(fileBean, FileBean.class);
        String identifier = fileBean.getIdentifier();

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
            File newFile = new File(FileUtils.getLocalStorageFilePathByFileBean(orginFileBean));
            String md5HashCode32 = Md5Utils.md5HashCode32(new FileInputStream(newFile));
            System.out.println("新文件的md5===" + md5HashCode32);
            fileBean.setIdentifier(md5HashCode32);
            String newFileUrl = orginFileBean.getFileUrl().replace(identifier, md5HashCode32);
            fileBean.setFileUrl(newFileUrl);
            fileBean.setFileUpdateTime(new Date());
            fileBean.setFileSize((long) fileSize);
            System.out.println(FileUtils.getLocalStorageFilePathByFileBean(orginFileBean).replace(orginFileBean.getIdentifier(), md5HashCode32));
        } catch (Exception e) {
            throw new FxException(999999, "修改文件异常");
        } finally {
            try {
                byteArrayInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map map = new HashMap();
        map.put("file", fileBean);
        return ResponseResult.success("修改文件成功", map);
    }

    /**
     * 获取清理文件列表
     * @param fileSize
     * @return
     */
    @GetMapping("/clear/list")
    @ResponseBody
    public ResponseResult clearFile(@RequestParam Long fileSize) {
        Long userId = SecurityUtils.getUserId();
        List<FileBean> fileBeanList = fileService.selectFileWithFileSize(fileSize, userId);
        Map map = new HashMap();
        map.put("list", fileBeanList);
        return ResponseResult.success("获取清理文件列表成功",map);
    }

    /**
     * 清理文件
     * @return
     */
    @GetMapping("/clean")
    public ResponseResult clean() {
        Long userId = SecurityUtils.getUserId();
        return fileService.cleanFile(userId);
    }


    /**
     * 获取文件操作进度 如解压,离线下载
     * @param progressDTO
     * @return
     */
    @SneakyThrows
    @PostMapping("/progress")
    @ResponseBody
    public ResponseResult progress(@RequestBody ProgressDTO progressDTO) {
        Long userId = SecurityUtils.getUserId();
        Object cacheObject =
                redisCache.getCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + progressDTO.getType() + "-" + userId +
                ":" + progressDTO.getT());
        if (cacheObject != null) {
            if ((Integer) cacheObject == 100) {
                FileBean fileBean =
                        redisCache.getCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + progressDTO.getType() + "-" + userId + "-file"+
                                ":" + progressDTO.getT());
                if (progressDTO.getType().equals("conversion")) {
                    if (cacheObject.equals(100)) {
                        Long fileSize = 0L;
                        FileBean newFileBean = new FileBean();
                        String convertFilePath = absoluteFilePath + "/tmp/" + fileBean.getFileName().replace(fileBean.getFileExt(),
                                progressDTO.getConvertExt());
                        Date date = new Date();
                        String dateStr = cn.hutool.core.date.DateUtil.format(date, "yyyyMMdd");
                        File convertFile = new File(convertFilePath);
                        String md5 = Md5Utils.md5HashCode32(convertFilePath);
                        String fileUrl = dateStr + "/" + md5 + "." + progressDTO.getConvertExt();
                        newFileBean = BeanCopyUtils.copyBean(FileUtils.getFileBeanByPath(convertFilePath, fileBean.getFilePath(),
                                date, storageType, userId), FileBean.class);
                        newFileBean.setAudit(1);
                        fileService.save(newFileBean);
                        redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + progressDTO.getType() + "-" + userId + "-file"+
                                ":" + progressDTO.getT(), newFileBean);
                        fileSize = newFileBean.getFileSize();
                        boolean b = storageService.updateStorageUse(fileSize, userId);
                        // 移动文件到文件存储路径
                        File moveFolder = new File(com.fx.pan.factory.FxUtils.getStaticPath() + "/" + dateStr);
                        System.out.println("moveFolder:" + moveFolder.getAbsolutePath());
                        log.info("格式转换完成,文件最终地址为:{}", com.fx.pan.factory.FxUtils.getStaticPath() + "/" + dateStr);

                        // 判断目录moveFolder是否存在，不存在则创建
                        if (!moveFolder.exists()) {
                            moveFolder.mkdirs();
                        }
                        convertFile.renameTo(new File(com.fx.pan.factory.FxUtils.getStaticPath() + "/" + fileUrl));
                    }

                } else if (progressDTO.getType().equals("unzip")) {
                    int fileNum = fileService.saveUnzipFile(fileBean,progressDTO.getFilePath(),progressDTO.getT(),
                            progressDTO.getUnzipMode());

                }
                Map map = new HashMap();
                map.put("progress", 100);
                map.put("finish", true);

                map.put("file",fileBean);
                storageService.updateStorageUse(fileBean.getFileSize(), userId);
                redisCache.deleteObject(Constants.REDIS_DATA_SUFFIX + "-" + progressDTO.getType() + "-" + userId +
                        ":" + progressDTO.getT());
                redisCache.deleteObject(Constants.REDIS_DATA_SUFFIX + "-" + progressDTO.getType() + "-" + userId + "-file"+
                        ":" + progressDTO.getT());
                redisCache.deleteObject(Constants.REDIS_FILE_LIST_PREFIX + userId);
                return ResponseResult.success("ok",map);
            } else {
                Map map = new HashMap();
                map.put("progress", cacheObject);
                return ResponseResult.success("ok",map);
            }

        } else {
            Map map = new HashMap();
            map.put("finish", true);
            return ResponseResult.success(map);
        }
    }

}
