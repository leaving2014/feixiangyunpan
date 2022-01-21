package com.fx.pan.controller;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.FileBean;
import com.fx.pan.service.FileService;
import com.fx.pan.service.TokenService;
import com.fx.pan.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 本地文件操作
 * @Author leaving
 * @Date 2021/11/24 22:24
 * @Version 1.0
 */


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
     * @return
     */
    @PostMapping("/checkfiles")
    public Msg checkFiles() {
        return Msg.success("ok").put("state", "成功");

    }



    /**
     * 创建文件夹 本地
     *
     * @param createFile
     * @return
     */
    @PostMapping("/createfolder")
    public Msg createfolder(@RequestBody FileBean createFile,
                            @RequestHeader("Authorization") String token) {
        // tokenService.getToken(request);
        System.out.println("Authorization:"+token);
        // User user = tokenService.getUsernameFromToken(token);
        // User user = userService.getUserBeanByToken(token);
        // if (user == null) {
        //     throw new NotLoginException();
        // }


        boolean flag = fileService.createFolder(createFile);
        if (flag) {
            return Msg.success("文件夹创建成功");
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
    public Msg copyFile() {
        // fileService.copyFile("");
        return Msg.success("复制成功");
    }

    /**
     * 文件移动
     */
    @PostMapping("/move")
    public Msg moveFile() {

        // fileService.moveFile("");
        return Msg.success("移动成功");
    }

    /**
     * 文件分享
     */
    @PostMapping("/share")
    public Msg shareFile() {

        // fileService.shareFile("");
        return Msg.success("分享成功");
    }




}
