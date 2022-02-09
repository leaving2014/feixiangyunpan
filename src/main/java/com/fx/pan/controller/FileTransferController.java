package com.fx.pan.controller;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.Chunk;
import com.fx.pan.dto.file.DownloadFileDTO;
import com.fx.pan.dto.file.UploadFileDTO;
import com.fx.pan.service.BigFileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * @Author leaving
 * @Date 2021/12/14 9:11
 * @Version 1.0
 */

@Tag(name = "filetransfer", description = "该接口为文件传输接口，主要用来做文件的上传和下载")
@RestController
@RequestMapping("/filetransfer")
public class FileTransferController {

    @Autowired
    BigFileService bigFileService;

    /**
     * 极速上传 根据MD5进行极速上传
     * @return
     */
    @PostMapping("/quickUpload")
    public Msg quickUpload(UploadFileDTO uploadFileDto, @RequestHeader("Authorization") String token){

        return Msg.success("");
    }

    /**
     * 处理文件上传POST请求
     * 将上传的文件存放到服务器内
     * @param chunk 文件块
     * @param response 响应
     * @return 上传响应状态
     */
    @PostMapping("/fileUpload")
    public Msg uploadPost(@ModelAttribute Chunk chunk, HttpServletResponse response){
        return bigFileService.fileUploadPost(chunk,response);
    }


    @GetMapping("/download")
    public Msg download(@RequestBody DownloadFileDTO downloadFileDTO){
        File file = new File(downloadFileDTO.getExtractionCode());

        return Msg.success("");
    }


}

