package com.fx.pan.controller;

import com.alibaba.fastjson.JSONObject;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.dto.file.UnzipFileDTO;
import com.fx.pan.exception.NotLoginException;
import com.fx.pan.service.FileService;
import com.fx.pan.service.TokenService;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.BeanCopyUtils;
import com.fx.pan.utils.SessionUtil;
import com.fx.pan.vo.FileListVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    @Autowired
    private TokenService tokenService;

    @Value("user1001")
    private String user;


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
        LoginUser sessionUserBean = (LoginUser) SessionUtil.getSession();
        Long userId = sessionUserBean.getUserId();
        boolean folderExist = fileService.isFolderExist(createFile.getFilePath(), createFile.getFileName(),
                userId);
        if (folderExist) {
            return Msg.error(500, "文件夹已存在");
        }
        boolean flag = fileService.createFolder(createFile);
        if (flag) {
            return Msg.success("文件夹创建成功").put("folderName",createFile.getFileName());
        } else {
            return Msg.error(500, "文件夹创建失败");
        }
    }

    /**
     * 文件重命名
     */
    @PostMapping("/rename")
    public Msg rename(@RequestParam Long fileId, @RequestParam String fileName) {
        boolean flag = fileService.fileRename(fileId, fileName);
        if (flag) {
            return Msg.success("重命名成功").put("newFileName", fileName);
        } else {
            return Msg.error(500, "重命名失败");
        }

    }

    /**
     * 文件复制
     */

    @PostMapping("/copy")
    public Msg copyFile(@RequestParam("copyFileId") Long copyFileId,@RequestParam("copyFilePath") Long copyFilePathId) {
        Msg msg= fileService.copyFile(copyFileId, copyFilePathId);
        return msg;
    }

    /**
     * 文件移动
     */
    @PostMapping("/move")
    public Msg moveFile(@RequestParam("moveFileId") Long moveFileId,
                        @RequestParam("moveFilePath") Long moveFilePathId) {

        fileService.moveFile(moveFileId, moveFilePathId);
        return Msg.success("移动成功");
    }

    /**
     * 文件删除
     */
    @PostMapping("/delete")
    public Msg deleteFile(@RequestBody FileBean fileBean) {
        LoginUser sessionUserBean = (LoginUser) SessionUtil.getSession();
        Long userId = sessionUserBean.getUserId();
        boolean flag = fileService.deleteFile(fileBean.getId(),userId);
        if (flag) {
            return Msg.success("删除成功");
        } else {
            return Msg.error(500, "删除失败");
        }
    }




    /**
     * 获取文件列表
     *
     * @return
     */
    @GetMapping("list")
    public Msg fileList(@RequestParam(required = false, defaultValue = "/") String filePath,
                        @RequestHeader("Authorization") String token) {
        Long user_id = userService.getUserBeanByToken(token).getId();
        List fl = fileService.getFileList(filePath, user_id);
        return Msg.success("获取成功").put("list", JSONObject.toJSON(fl)).put("path", filePath).put("count", fl.size());
    }




    /**
     * 获取文件树
     *
     * @return
     */
    @GetMapping("filetree")
    public Msg getFIleTree() {
        // fileService.getFileTree();

        return Msg.success("文件树获取成功");
    }

    /**
     * 解压文件
     */
    @GetMapping("/unzipFile")
    public Msg unzipFile(@RequestBody UnzipFileDTO unzipFileDto) {
        fileService.unzip(unzipFileDto.getFileId(),unzipFileDto.getUnzipMode(),unzipFileDto.getFilePath());
        return Msg.success("解压成功");
    }


    /**
     * 文件搜索
     *
     * @return
     */
    @GetMapping("/search")
    public Msg search(@RequestParam String keywords) {
        LoginUser sessionUserBean = (LoginUser) SessionUtil.getSession();
        Long userId = sessionUserBean.getUserId();
        List list = fileService.searchFile(keywords, userId);
        List list1 = BeanCopyUtils.copyBeanList(list, FileListVo.class);
        return Msg.success("").put("list", list1);
    }

    @GetMapping("/clean")
    public Msg clean(){
        LoginUser sessionUserBean = (LoginUser) SessionUtil.getSession();
        Long userId = sessionUserBean.getUserId();
        return fileService.cleanFile(userId);
    }




    /**
     * 批量复制文件
     *
     * @return
     */
    @PostMapping("/batchcopy/{copyPathId}")
    public Msg batchCopy(@RequestBody List<Long> filelist,@PathVariable(name = "copyPathId") Long copyPathId) {
        LoginUser sessionUserBean = (LoginUser) SessionUtil.getSession();
        Long userId = sessionUserBean.getUserId();
        for (Long file : filelist) {
            fileService.copyFile(file,copyPathId);
        }
        return Msg.success("批量复制成功").put("count", filelist.size());
    }

    /**
     * 批量移动文件
     *
     * @return
     */
    @PostMapping("/batchmove/{movePathId}")
    public Msg batchMove(@RequestBody List<Long> filelist,@PathVariable(name = "movePathId") Long movePathId) {
        LoginUser sessionUserBean = (LoginUser) SessionUtil.getSession();
        Long userId = sessionUserBean.getUserId();
        for (Long file : filelist) {
            fileService.moveFile(file,movePathId);
        }
        return Msg.success("批量移动成功").put("count", filelist.size());
    }

    /**
     * 批量删除文件
     *
     * @return
     */
    @PostMapping("/batchdelete")
    public Msg batchdelete(@RequestBody List<Long> filelist) {
        LoginUser sessionUserBean = (LoginUser) SessionUtil.getSession();
        Long userId = sessionUserBean.getUserId();
        if (sessionUserBean == null) {
            throw new NotLoginException();
        }
        for (Long fid : filelist) {
            fileService.deleteFile(fid,userId);
        }
        return Msg.success("批量删除成功").put("count", filelist.size());
    }

}
