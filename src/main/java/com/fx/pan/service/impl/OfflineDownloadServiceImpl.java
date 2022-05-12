package com.fx.pan.service.impl;

import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.FileBean;
import com.fx.pan.factory.fxUtils;
import com.fx.pan.service.FileService;
import com.fx.pan.service.OfflineDownloadService;
import com.fx.pan.service.StorageService;
import com.fx.pan.utils.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leaving
 * @date 2022/3/28 10:33
 * @version 1.0
 */
@Service
public class OfflineDownloadServiceImpl implements OfflineDownloadService {

    @Value("${fx.storageType}")
    private Integer storageType;

    @Value("${fx.absoluteFilePath}")
    private String absoluteFilePath;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private WgetUtil wgetUtil;

    @SneakyThrows
    @Override
    public ResponseResult downloadFromUrl(String urlStr, Long t, Long userId, String type) throws FileNotFoundException {
        String fileName = null;
        FileBean fileBean = new FileBean();
        Integer fileSize = 0;
        try {
            fileName = FileUtils.getFileName(urlStr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Date date = new Date(t);
        String dateStr = DateUtil.formatDate(date, "yyyyMMdd");

        String downloadPath = absoluteFilePath + "/tmp/" + fileName;
        String fileSavePath = absoluteFilePath + "/";

        // 文件保存位置
        File saveDir = new File(fileSavePath+dateStr);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }

        // String url = "https://mirrors.aliyun.com/apache/accumulo/2.0.1/accumulo-2.0.1-src.tar.gz";
        String option = "-O " + downloadPath;
        wgetUtil.wgetProgressRation(downloadPath, fileSavePath, fileName, urlStr, t, userId, type,
                progress -> {});
        // System.out.println(progress.toJSONString());


        // 下载文件并实时打印下载进度,最后保存到本地

        // OfflineDownloadUtil.downloadFileFromUrl(urlStr, savePath,t,type);
        Map<String, Object> map = new HashMap<>();
        map.put("fileName", fileName.replace("\"", ""));
        map.put("fileSize", fileSize);
        map.put("filePath", fileBean);
        map.put("progress", 0);
        System.out.println("返回结果前...=====" + new Date());
        redisCache.setCacheObject("remote-" + t + "-" + userId, 0);
        return ResponseResult.success("任务创建成功", map);

    }
}
