package com.fx.pan.controller;

import com.fx.pan.baidu.BaiduOcrUtil;
import com.fx.pan.common.Msg;
import org.springframework.web.bind.annotation.*;
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
     *
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/ocr")
    @ResponseBody
    public Msg generalOcr(@RequestParam(value = "file") MultipartFile file,  HttpServletRequest request) throws IOException {
        return BaiduOcrUtil.baiduGeneralOCR(file.getBytes());
    }
}
