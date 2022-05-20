package com.fx.pan.utils;

/**
 * @author leaving
 * @date 2021/11/25 10:32
 * @version 1.0
 */

import com.fx.pan.common.Constants;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.service.FileService;
import com.qcloud.cos.COSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现离线下载
 * @author leaving
 */
@Component
public class OfflineDownloadUtil {
    @Value("${tencent.cos.accessKey}")
    private String accessKey;
    @Value("${tencent.cos.secretKey}")
    private String secretKey;
    @Value("${tencent.cos.bucketRegion}")
    private String bucketRegion;
    @Value("${tencent.cos.bucketName}")
    private String bucketName;
    @Value("${tencent.cos.path}")
    private String path;

    private static RedisCache redisCache;

    @Autowired
    private RedisCache rc;

    public @PostConstruct
    void init() {
        redisCache = rc;
    }


    @Autowired
    private FileService fs;

    private static FileService fileService;

    public @PostConstruct
    void init1() {
        fileService = fs;
    }

    public static String downloadFileFromUrl(String href, String savePath, Long t, String type) throws FileNotFoundException, InterruptedException {
        Long userId = SecurityUtils.getUserId();
        System.out.println("userId:" + userId);
        String fileName = null;
        final Integer[] progress = {0};
        try {
            fileName = FileUtils.getFileName(href);
            fileName = fileName.replaceAll("\"", "");

        } catch (IOException e) {
            e.printStackTrace();
        }
        long begin_time = System.currentTimeMillis();
        URL url = null;
        try {
            url = new URL(href);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        URLConnection conn = null;
        try {
            conn = url.openConnection();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.out.println("开始下载>>>");
        int fileSize = conn.getContentLength();
        System.out.println("文件总共大小：" + fileSize + "字节");

        // 设置分块大小
        int blockSize = 1024 * 1024;
        // 文件分块的数量
        int blockNum = fileSize / blockSize;

        if ((fileSize % blockSize) != 0) {
            blockNum += 1;
        }

        System.out.println("分块数->线程数：" + blockNum);
        final String finalFileName = fileName;
        Thread[] threads = new Thread[blockNum];
        int index = 0;
        for (int i = 0; i < blockNum; i++) {

            // 匿名函数对象需要用到的变量
            index = i;
            final int finalBlockNum = blockNum;
            // final String finalFileName = fileName;
            System.out.println("finalFileName:" + finalFileName);
            // 创建一个线程
            int finalIndex = index;
            threads[i] = new Thread() {
                public void run() {
                    URL url = null;
                    try {
                        url = new URL(href);
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        // 重新获取连接
                        URLConnection conn = url.openConnection();
                        // 重新获取流
                        InputStream in = conn.getInputStream();
                        // 定义起始和结束点
                        int beginPoint = 0, endPoint = 0;

                        System.out.print("第" + (finalIndex + 1) + "块文件：");
                        beginPoint = finalIndex * blockSize;
                        // 判断结束点
                        if (finalIndex < finalBlockNum - 1) {
                            endPoint = beginPoint + blockSize;
                        } else {
                            endPoint = fileSize;
                        }
                        System.out.println("起始字节数：" + beginPoint + ",结束字节数：" + endPoint);
                        // 将下载的文件存储到一个文件夹中
                        //当该文件夹不存在时，则新建
                        File filePath = new File(savePath);
                        if (!filePath.exists()) {
                            filePath.mkdirs();
                        }
                        FileOutputStream fos =
                                new FileOutputStream(new File(savePath + finalFileName) + "_" + (finalIndex + 1));
                        // 跳过 beginPoint个字节进行读取
                        in.skip(beginPoint);
                        byte[] buffer = new byte[1024];
                        int count;
                        // 定义当前下载进度
                        int process = beginPoint;
                        // 当前进度必须小于结束字节数
                        while (process < endPoint) {
                            count = in.read(buffer);
                            // 判断是否读到最后一块
                            if (process + count >= endPoint) {
                                count = endPoint - process;
                                process = endPoint;
                                redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + type + "-" + userId +
                                                ":" + t,
                                        100);
                            } else {
                                // 计算当前进度
                                process += count;
                                progress[0] = process / fileSize * 100 * 100;
                                redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + type + "-" + userId +
                                                ":" + t,
                                        progress[0]);
                            }
                            // 保存文件流
                            fos.write(buffer, 0, count);
                        }
                        fos.close();
                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            threads[i].start();

        }

        // 当所有线程都结束时才开始文件的合并
        for (Thread thread : threads) {
            thread.join();
        }

        // 若该文件夹不存在，则创建一个文件夹
        File filePath = new File(savePath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        // 定义文件输出流
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(savePath + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < blockNum; i++) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(savePath + fileName + "_" + (i + 1));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] buffer = new byte[1024];
            int count;
            try {
                while ((count = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date date = new Date();
        Date formatCurrentTime = DateUtil.getFormatCurrentTime("yyyy-MM-dd");
        String fileUrl = "/" + formatCurrentTime.toString() + "/" + finalFileName;
        File file = new File(savePath + finalFileName);
        file.renameTo(new File(savePath + finalFileName + "_" + (index + 1)));

        FileBean fileBean = FileUtils.getFileBeanByPath(savePath + finalFileName, null, date, 1,
                userId);
        fileBean.setOrigin(1);
        fileService.insertFileInfo(fileBean);

        long end_time = System.currentTimeMillis();
        long seconds = (end_time - begin_time) / 1000;
        long minutes = seconds / 60;
        long second = seconds % 60;
        System.out.println("下载完成,用时：" + minutes + "分" + second + "秒");
        return fileName;
    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


}
