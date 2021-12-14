package com.fx.pan.controller;

import com.fx.pan.entity.Chunk;
import com.fx.pan.interfaces.BigFileServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author leaving
 * @Date 2021/12/14 9:11
 * @Version 1.0
 */

@RestController
@RequestMapping("/bigFile")
public class BigFileController {

    @Autowired
    BigFileServiceInterface bigFileServiceInterface;

    /**
     * 处理文件上传POST请求
     * 将上传的文件存放到服务器内
     * @param chunk 文件块
     * @param response 响应
     * @return 上传响应状态
     */
    @PostMapping("/fileUpload")
    public String uploadPost(@ModelAttribute Chunk chunk, HttpServletResponse response){
        return bigFileServiceInterface.fileUploadPost(chunk,response);
    }


}

