package com.fx.pan.controller;

import com.fx.pan.annotation.Limit;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.FileBean;
import com.fx.pan.service.BaiduOcrService;
import com.fx.pan.service.FileService;
import com.fx.pan.utils.FileUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 * @author leaving
 * @date 2021/12/4 11:25
 * @version 1.0
 */

/**
 * 百度OCR
 *
 * @author leaving
 */
@Slf4j
@RequestMapping("/image")
@RestController
public class OcrController {

    @Resource
    private BaiduOcrService baiduOcrService;

    @Value("${fx.absoluteFilePath}")
    String absoluteFilePath;

    @Resource
    private FileService fileService;

    /**
     * 图片文字识别 (上传文件识别)
     *
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "图片文字识别 (上传文件识别)")
    @Limit(key = "limit1", permitsPerSecond = 1, timeout = 500, msg = "请求过于频繁，请稍后再试！请求频率限制为1次/秒")
    @PostMapping(value = "/ocr/file")
    public ResponseResult generalOcr(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request) throws IOException {
        if (file.getSize() > 4194304) {
            // 百度图片限制4M
            return ResponseResult.error(500, "上传图片文件过大");
        } else {
            ResponseResult responseResult = baiduOcrService.baiduGeneralOcr(file.getBytes(), "");
            return responseResult;
        }
    }

    /**
     * 图片文字识别 (文件id)
     * @param fileId
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "图片文字识别 (文件id识别)")
    @GetMapping("/ocr")
    @Limit(key = "limit1", permitsPerSecond = 1, timeout = 500, msg = "请求过于频繁，请稍后再试！请求频率限制为1次/秒")
    public ResponseResult ocr(@RequestParam Long fileId) throws IOException {
        FileBean fileBean = fileService.selectFileById(fileId);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(fileBean.getFileCreateTime());
        String imagePath = FileUtils.getLocalStorageFilePathByFileBean(fileBean);
        ResponseResult responseResult = baiduOcrService.baiduGeneralOcr(FileUtils.readImageFile(imagePath), imagePath);
        return responseResult;
    }
}
