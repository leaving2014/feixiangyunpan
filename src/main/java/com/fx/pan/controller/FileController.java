package com.fx.pan.controller;

import com.alibaba.fastjson.JSONObject;
import com.fx.pan.advice.FxException;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.dto.file.BatchCopyFileDTO;
import com.fx.pan.dto.file.BatchMoveFileDTO;
import com.fx.pan.dto.file.UnzipFileDTO;
import com.fx.pan.dto.file.UpdateFileDTO;
import com.fx.pan.factory.FxFactory;
import com.fx.pan.factory.operation.download.domain.DownloadFile;
import com.fx.pan.factory.operation.write.Writer;
import com.fx.pan.factory.operation.write.domain.WriteFile;
import com.fx.pan.service.FileService;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.BeanCopyUtils;
import com.fx.pan.utils.SecurityUtils;
import com.fx.pan.utils.file.FileTypeUtils;
import com.fx.pan.vo.FileListVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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


    // @Value("${fx.fileStorageType}")
    // private String fileStorageType;
    //
    // @Value("${fx.fileStorageRootPath}")
    // private String fileStorageRootPath;

    @Autowired
    private UserService userService;

    @Resource
    private FileService fileService;

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
        if (!createFile.getFilePath().equals("/")){
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
    public Msg rename(@RequestBody Map params) {
        Integer fileId = (Integer) params.get("fileId");
        String newFileName = (String) params.get("fileName");

        // @RequestParam Long fileId, @RequestParam String fileName
        // 重命名的文件夹
        FileBean fileBean = fileService.selectFileById(fileId.longValue());
        System.out.println("重命名文件夹");
        System.out.println(fileBean);
        // 判断 重命名是否是文件夹 ,文件夹重命名需要更新文件夹下所有的子目录和文件的目录
        System.out.println(fileBean.getIsDir());
        System.out.println(fileBean.getIsDir() == 1);
        if (fileBean.getIsDir() == 1) {
            // TODO 重命名子目录和文件
            log.info("目录重命名加载子目录");
            // 先对重命名的子目录和文件重命名
            List<FileBean> childFileList = fileService.selectChildFileListByPath('/' + fileBean.getFileName());
            log.info(childFileList.toString());
            for (FileBean file : childFileList) {
                log.info("子目录" + file.getFileName() + "开始重命名");
                String orginFilePath = file.getFilePath();
                String newPath = orginFilePath.replace(fileBean.getFileName(), newFileName);
                int i = fileService.updateFilePathById(file.getId(), newPath);
            }

            // 最后重命名用户重命名的文件
            boolean flag = fileService.fileRename(fileId.longValue(), newFileName);
            return Msg.success("重命名成功12344").put("newFileName", newFileName);
        } else {
            boolean flag = fileService.fileRename(fileId.longValue(), newFileName);
            if (flag) {
                return Msg.success("重命名成功").put("newFileName", newFileName);
            } else {
                return Msg.error(500, "重命名失败");
            }
        }
    }

    /**
     * 文件复制
     */

    @PostMapping("/copy")
    public Msg copyFile(@RequestParam Long fileId,@RequestParam String filePath) {
        Long userId = SecurityUtils.getUserId();
        Msg msg = fileService.copyFile(fileId, filePath,userId);
        return msg;
    }

    /**
     * 文件移动
     */
    @PostMapping("/move")
    public Msg moveFile(@RequestBody Map params) {
        Long userId = SecurityUtils.getUserId();
        Integer fileId = (Integer) params.get("fileId");
        String filePath = (String) params.get("filePath");

        fileService.moveFile(fileId.longValue(), filePath,userId);
        return Msg.success("移动成功");
    }

    /**
     * 文件删除
     */
    @PostMapping("/delete")
    public Msg deleteFile(@RequestBody FileBean fileBean) {
        Long userId = SecurityUtils.getUserId();

        boolean flag = fileService.deleteFile(fileBean.getId(), userId);
        if (flag) {
            return Msg.success("删除成功");
        } else {
            return Msg.error(500, "删除失败");
        }
    }


    /**
     * 根据路径获取所有文件列表
     *
     * @return
     */
    @GetMapping("/list")
    public Msg fileList(@RequestParam(required = false, defaultValue = "/") String filePath) {
        Long userId = SecurityUtils.getUserId();
        List fl = fileService.getFileList(filePath, userId);
        return Msg.success("获取成功").put("list", JSONObject.toJSON(fl)).put("path", filePath).put("total", fl.size());
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
        Long beginCount = 0L;
        if (pageCount == 0 || currentPage == 0) {
            beginCount = 0L;
            pageCount = 10L;
        } else {
            beginCount = (currentPage - 1) * pageCount;
        }

        Long total = 0L;
        if (fileType == FileTypeUtils.OTHER_TYPE) {

            List<String> arrList = new ArrayList<>();
            arrList.addAll(Arrays.asList(FileTypeUtils.DOC_FILE));
            arrList.addAll(Arrays.asList(FileTypeUtils.IMG_FILE));
            arrList.addAll(Arrays.asList(FileTypeUtils.VIDEO_FILE));
            arrList.addAll(Arrays.asList(FileTypeUtils.BT_FILE));
            arrList.addAll(Arrays.asList(FileTypeUtils.Audio_FILE));

            fileList = fileService.selectFileNotInExtendNames(arrList, beginCount, pageCount, userId);
            total = fileService.selectCountNotInExtendNames(arrList, beginCount, pageCount, userId);
        } else {
            fileList = fileService.selectFileByExtendName(FileTypeUtils.getFileExtendsByType(fileType), beginCount,
                    pageCount, userId);
            total = fileService.selectCountByExtendName(FileTypeUtils.getFileExtendsByType(fileType), beginCount,
                    pageCount, userId);
        }

        // Map<String, Object> map = new HashMap<>();
        // map.put("list",fileList);
        // map.put("total", total);

        return Msg.success("获取成功").put("list", fileList).put("total", fileList.size());
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
    @GetMapping("/unzipFile")
    public Msg unzipFile(@RequestBody UnzipFileDTO unzipFileDto) {
        fileService.unzip(unzipFileDto.getFileId(), unzipFileDto.getUnzipMode(), unzipFileDto.getFilePath());
        return Msg.success("解压成功");
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
     *
     * @return
     */
    @PostMapping("/batchcopy")
    public Msg batchCopy(@RequestBody BatchCopyFileDTO batchCopyFileDTO) {
        Long userId = SecurityUtils.getUserId();
        List<FileBean> fileList = batchCopyFileDTO.getFileList();
        String filePath = batchCopyFileDTO.getFilePath();
        for (FileBean file : fileList) {
            Long fileId = file.getId();
            // 批量移动的文件夹
            FileBean fileBean = fileService.selectFileById(fileId);
            System.out.println("批量移动文件夹");
            System.out.println(fileBean);
            // 判断 批量移动是否是文件夹 ,文件夹批量移动需要更新文件夹下所有的子目录和文件的目录
            System.out.println(fileBean.getIsDir());
            System.out.println(fileBean.getIsDir() == 1);

            if (fileBean.getIsDir() == 1) {
                // TODO 复制的是文件夹
                return Msg.error(500, "文件夹不能批量复制");

            } else {
                fileService.copyFile(fileId, filePath,userId);
            }
        }
        return Msg.success("批量复制成功").put("total", fileList.size());
    }

    /**
     * 批量移动文件
     *
     * @return
     */
    @PostMapping("/batchmove")
    public Msg batchMove(@RequestBody BatchMoveFileDTO batchMoveFileDTO) {
        Long userId = SecurityUtils.getUserId();
        List<FileBean> fileList = batchMoveFileDTO.getFileList();
        String filePath = batchMoveFileDTO.getFilePath();
        // TODO  批量移动
        for (FileBean file : fileList) {
            Long fileId = file.getId();
            // 批量移动的文件夹
            FileBean fileBean = fileService.selectFileById(fileId);
            System.out.println("批量移动文件夹");
            System.out.println(fileBean);
            // 判断 批量移动是否是文件夹 ,文件夹批量移动需要更新文件夹下所有的子目录和文件的目录
            System.out.println(fileBean.getIsDir());
            System.out.println(fileBean.getIsDir() == 1);
            if (fileBean.getIsDir() == 1) {
                // 对文件夹进行遍历移动

            } else {
                // 对多文件移动
                fileService.moveFile(fileId,filePath,userId);
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
        for (Long fid : fileList) {
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
        // Long pointCount = fileService.getFilePointCount(userFile.getFileId());
        // if (pointCount > 1) {
        //     return Msg.error(550,"暂不支持修改");
        // }
        String content = updateFileDTO.getFileContent();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes());
        try {

            Writer writer1 = fxFactory.getWriter(fileBean.getStorageType());
            WriteFile writeFile = new WriteFile();
            writeFile.setFileUrl(fileBean.getFileUrl());
            int fileSize = byteArrayInputStream.available();
            writeFile.setFileSize(fileSize);
            writer1.write(byteArrayInputStream, writeFile);
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.setFileUrl(fileBean.getFileUrl());
            InputStream inputStream = fxFactory.getDownloader(fileBean.getStorageType()).getInputStream(downloadFile);
            String md5Str = DigestUtils.md5Hex(String.valueOf(inputStream));
            fileBean.setIdentifier(md5Str);
            fileBean.setFileUpdateTime(new Date());
            // fileBean.setModifyUserId(loginUser.getUserId());
            fileBean.setFileSize((long) fileSize);
            fileService.updateById(fileBean);
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



}
