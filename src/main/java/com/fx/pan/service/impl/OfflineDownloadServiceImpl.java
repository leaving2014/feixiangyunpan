package com.fx.pan.service.impl;

import cn.hutool.core.date.DateUtil;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.FileBean;
import com.fx.pan.factory.fxUtils;
import com.fx.pan.service.FileService;
import com.fx.pan.service.OfflineDownloadService;
import com.fx.pan.service.StorageService;
import com.fx.pan.utils.BeanCopyUtils;
import com.fx.pan.utils.FileUtils;
import com.fx.pan.utils.Md5Utils;
import com.fx.pan.utils.SecurityUtils;
import com.fx.pan.utils.file.ImageUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * @Author leaving
 * @Date 2022/3/28 10:33
 * @Version 1.0
 */
@Service
public class OfflineDownloadServiceImpl implements OfflineDownloadService {

    @Value("${fx.storageType}")
    private Integer storageType;

    @Value("${fx.absoluteFilePath}")
    private String absoluteFilePath;

    @Resource
    private FileService fileService;

    @Resource
    private StorageService storageService;

    @Override
    public Msg downloadFromUrl(String urlStr) {
        Long userId = SecurityUtils.getUserId();
        String fileName = null;
        FileBean fileBean = new FileBean();
        String savePath = fxUtils.getStaticPath() + "/tmp";
        String fileSize = "";
        try {
            fileName = FileUtils.getFileName(urlStr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long contentLength = 0;
        try {

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            // 防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //获取下载文件大小
            contentLength = conn.getContentLength();
            fileSize = FileUtils.fileSizeUnitConversionAndUnit(contentLength);
            // 得到输入流
            InputStream inputStream = conn.getInputStream();
            // 获取字节数组
            byte[] getData = IOUtils.toByteArray(inputStream);
            // FileUtils.readInputStream(inputStream);

            // 文件保存位置
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            String saveFilePath = absoluteFilePath + "/tmp/" + fileName;
            File file = new File(saveFilePath);
            Date date = new Date();
            String dateStr = DateUtil.format(date, "yyyyMMdd");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            if (fos != null) {
                fos.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }

            String md5 = Md5Utils.md5HashCode32(saveFilePath);
            // 获取文本扩展名
            String fileUrl = dateStr + "/" + md5 + "." + FileUtils.getFileExt(fileName);
            fileBean = BeanCopyUtils.copyBean(FileUtils.getFileBeanByPath(file.getAbsolutePath(),
                    fileBean.getFilePath(), date, storageType,
                    userId), FileBean.class);
            fileBean.setFileName(fileName);
            fileService.save(fileBean);
            if (fileBean.getFileType() == 1 || fileBean.getFileType() == 2) {
                ImageUtil.startGenerateThumbnail(fxUtils.getStaticPath() + "/" + fileUrl, fileBean, true, 0.3);
            }
            storageService.updateStorageUse(file.length(), userId);
            file.renameTo(new File(fxUtils.getStaticPath() + "/" + fileUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Msg.success("下载成功").put("fileName", fileName).put("fileSize", fileSize).put("progress", 0).put("filePath"
                , fileBean);

    }
}
