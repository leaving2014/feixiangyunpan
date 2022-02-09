package com.fx.pan.utils;

/**
 * @Author leaving
 * @Date 2021/11/25 10:32
 * @Version 1.0
 */

import com.fx.pan.common.Msg;
import com.fx.pan.cos.CosClient;
import com.qcloud.cos.COSClient;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 实现离线下载
 */

public class OfflineDownload {
    @Value("${tengxun.cos.accessKey}")
    private String accessKey;
    @Value("${tengxun.cos.secretKey}")
    private String secretKey;
    @Value("${tengxun.cos.bucketRegion}")
    private String bucketRegion;
    @Value("${tengxun.cos.bucketName}")
    private String bucketName;
    @Value("${tengxun.cos.path}")
    private String path;
    @Value("${tengxun.cos.qianzui}")
    private String qianzui;

    @Value("user1001")
    private String user;

    //https://dlcdn.apache.org//commons/io/binaries/commons-io-2.11.0-bin.zip


    /**
     * 获取网络文件大小
     */
    private static long getFileLength(String downloadUrl) throws IOException{
        if(downloadUrl == null || "".equals(downloadUrl)){
            return 0L ;
        }
        URL url = new URL(downloadUrl);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            return (long) conn.getContentLength();
        } catch (IOException e) {
            return 0L;
        } finally {
            conn.disconnect();
        }
    }

    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @param savePath
     * @throws IOException
     * @return
     */
    public static Msg downLoadFromUrl(String urlStr,  String savePath) {
        String fileName = null;
        String fileSize = "";

        try {
            fileName = FileUtil.getFileName(urlStr);
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
            fileSize = FileUtil.fileSizeUnitConversionAndUnit(contentLength);
            System.out.println("文件大小:"+fileSize);

            // 得到输入流
            InputStream inputStream = conn.getInputStream();
            // 获取字节数组
            byte[] getData = readInputStream(inputStream);

            // 文件保存位置
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            if (fos != null) {
                fos.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }

            return Msg.success("ok").put("fileName", fileName).put("fileSize", fileSize).put("progress", 0);
                    //saveDir + File.separator + fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("fileSize:"+fileSize);
        return Msg.success("ok").put("fileName", fileName).put("fileSize", fileSize).put("progress", 0);

    }


    public Msg uploadToCOS(File file){
        COSClient cosclient = CosClient.getInstance();
        // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        String bucketName = this.bucketName;

        // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20 M 以下的文件使用该接口
        // 大文件上传请参照 API 文档高级 API 上传
        File localFile = null;
        // try {
        //     localFile = File.createTempFile("temp", null);
        //     file.transferTo(localFile);
        //     // 指定要上传到 COS 上的路径
        //     String key = "/" + year + "/" + (month + 1) + "/" + day + "/" + newFileName;
        //     PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        //     PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
        //     log.info("FileName:" + oldFileName + "  fileSize:" + fileSize + "  fileHashCode:" + fileHshCode);
        //     return new UploadMsg(0, "上传成功", this.path + putObjectRequest.getKey());
        // } catch (IOException e) {
        //     return Msg.error(500, e.getMessage());
        //
        // } finally {
        //     // 关闭客户端(关闭后台线程)
        //     cosclient.shutdown();
        // }
        return null;
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
