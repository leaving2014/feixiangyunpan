package com.fx.pan.controller;

import com.fx.pan.baidu.BaiduOcrUtil;
import com.fx.pan.common.Msg;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author leaving
 * @Date 2021/12/4 11:25
 * @Version 1.0
 */

/**
 * 百度OCR
 */

@RequestMapping("/api/")
@RestController
public class BaiduController {


    /**
     *图片文字识别
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/ocr")
    public Msg generalOcr(@RequestParam(value = "file") MultipartFile file,  HttpServletRequest request) throws IOException {

        if (file.getSize() > 4194304){
            // 百度图片限制4M
            return Msg.error(500, "上传图片文件过大");
        }else {
            return BaiduOcrUtil.baiduGeneralOcr(file.getBytes());
        }
    }
}
