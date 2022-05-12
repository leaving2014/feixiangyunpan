package com.fx.pan.controller;

import com.fx.pan.common.Constants;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.factory.fxUtils;
import com.fx.pan.service.FileService;
import com.fx.pan.service.OfflineDownloadService;
import com.fx.pan.utils.FileUtils;
import com.fx.pan.utils.OfflineDownloadUtil;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author leaving
 * @date 2021/12/18 15:24
 * @version 1.0
 */
@RequestMapping("/offline")
@RestController
public class OfflineDownloadController {

    @Value("${fx.storageType}")
    private Integer storageType;

    @Value("${fx.absoluteFilePath}")
    private String absoluteFilePath;

    @Resource
    private FileService fileService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private OfflineDownloadService offlineDownloadService;

    // https://mirrors.aliyun.com/apache/accumulo/2.0.1/accumulo-2.0.1-src.tar.gz

    /**
     * 创建离线下载任务
     * @param url
     * @param t
     * @param type
     * @param response
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @PostMapping("/new")
    public ResponseResult newOfflineDownload(@RequestParam("url") String url,@RequestParam("t") Long t,@RequestParam(
            "type") String type,HttpServletResponse response) throws IOException, InterruptedException {
        Long userId = SecurityUtils.getUserId();
        // String fileName = FileUtils.getFileName(url);
        redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX +"-"+type+"-"+userId+":"+ t, 0);

        String fileName = null;
        FileBean fileBean = new FileBean();
        // String savePath = fxUtils.getStaticPath() + "/tmp/";
        Integer fileSize = 0;
        try {
            fileName = FileUtils.getFileName(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // String saveFilePath = absoluteFilePath + "/tmp/" + fileName;

        // OfflineDownloadUtil.downloadFileFromUrl(url, savePath,t,type);

        // wget下载文件并实时打印下载进度,最后保存到本地
        offlineDownloadService.downloadFromUrl(url,t,userId,type);
        Map<String, Object> map = new HashMap<>();
        map.put("fileName", fileName.replace("\"", ""));
        map.put("fileSize", fileSize);
        map.put("filePath", fileBean);
        map.put("progress", 0);
        System.out.println("返回结果前...=====" + new Date());
        redisCache.setCacheObject("remote-" + t + "-" + userId, 0);
        return ResponseResult.success("任务创建成功", map);
    }

    public static void inputStream2File(InputStream is, File file) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OfflineDownloadController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OfflineDownloadController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (null != os) {
                    os.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(OfflineDownloadController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Logger.getLogger(OfflineDownloadController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    @PostMapping("/status")
    public ResponseResult downloadStatus(@RequestParam("t") String string, HttpServletResponse response) {
        int progress = redisCache.getCacheObject(Constants.REDIS_DATA_PREFIX + "-" + string);
        return ResponseResult.success(0,progress);
    }
}
