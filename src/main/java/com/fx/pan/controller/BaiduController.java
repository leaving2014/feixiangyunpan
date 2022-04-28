package com.fx.pan.controller;

import com.fx.pan.annotation.Limit;
import com.fx.pan.baidu.BaiduOcrUtil;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.FileBean;
import com.fx.pan.service.FileService;
import com.fx.pan.service.OcrService;
import com.fx.pan.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * @Author leaving
 * @Date 2021/12/4 11:25
 * @Version 1.0
 */

/**
 * 百度OCR
 *
 * @author leaving
 */

@RequestMapping("/image")
@RestController
public class BaiduController {
    @Value("${fx.absoluteFilePath}")
    String absoluteFilePath;

    @Resource
    private FileService fileService;

    @Resource
    private OcrService ocrService;


    /**
     * 图片文字识别 (上传文件识别)
     *
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/ocr/file")
    public Msg generalOcr(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request) throws IOException {

        if (file.getSize() > 4194304) {
            // 百度图片限制4M
            return Msg.error(500, "上传图片文件过大");
        } else {
            return BaiduOcrUtil.baiduGeneralOcr(file.getBytes(), "");
        }
    }

    @GetMapping("/ocr")
    @Limit(key = "limit1", permitsPerSecond = 1, timeout = 500, msg =
            "请求过于频繁，请稍后再试！请求频率限制为1次/秒")
    public Msg ocr(@RequestParam Long fileId) throws IOException {
        FileBean fileBean = fileService.selectFileById(fileId);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(fileBean.getFileCreateTime());
        String imagePath = FileUtils.getLocalStorageFilePathByFileBean(fileBean);
        System.out.println("文字Spring Boot图片路径===" + imagePath);

        Msg msg = ocrService.baiduGeneralOcr(FileUtils.readImageFile(imagePath), imagePath);
        return msg;
        // return BaiduOcrUtil.baiduGeneralOcr(FileUtil.readImageFile(imagePath), imagePath);
    }
}
