package com.fx.pan.cos;

import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.qcloud.cos.demo.BucketRefererDemo.cosClient;

/**
 * @author leaving
 * @date 2021/11/25 10:19
 * @version 1.0
 */

public class folder {

    @Value("${spring.tengxun.bucketName}")
    String bucketName;

    /**
     * 创建目录
     */
    public void createFolder() {
        // String bucketName = bucketName;
        String key = "folder/images/";
        // 目录对象即是一个/结尾的空文件，上传一个长度为 0 的 byte 流
        InputStream input = new ByteArrayInputStream(new byte[0]);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(0);
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, key, input, objectMetadata);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
    }
}
