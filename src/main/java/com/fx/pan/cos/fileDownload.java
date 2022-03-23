package com.fx.pan.cos;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;

import java.io.File;
import java.io.IOException;

import static com.qcloud.cos.demo.BucketRefererDemo.cosClient;

/**
 * @Author leaving
 * @Date 2021/11/25 10:17
 * @Version 1.0
 */

public class fileDownload {
    // https://fxcloud-1254493079.cos.ap-beijing.myqcloud.com/file/2021%E5%B9%B412%E6%9C%8817%E6%97%A5.md
    //https://pan-1254493079.cos.ap-nanjing.myqcloud.com/pan/2021/10/24/4277ebf9-090e-464c-8544-fdf3b368d40c.jpg?q-sign-algorithm=sha1&q-ak=AKIDDhmWHKCq4OTrYgg0y8t38mX8F_z6JQWRPBKRMT_Ou1wyM6k6t4bYT-Qfx76G6mTV&q-sign-time=1637807015;1637810615&q-key-time=1637807015;1637810615&q-header-list=&q-url-param-list=&q-signature=a3b6e3ab499cfc615c353a2750f5fcc02e5c6d24&x-cos-security-token=zulAfe61XsLjM2RWg5oJ23uS14APyZya8706799144ce3e10aa52499445d749eeqEW8U688_ZHlHFOTBGmmkQRfSf7D1TviLesMz2Zf8R_VyGqBa5D0M4KvYRiHeYa_XMvHpAkvwjn68aFHG4rsQmwyjMs4HoC5RKnM9p3L1ueg091HuqlzhHysVzth8e6oogmYL4VWEKgfEyOwl0UdQlABhJFx2zmZts69S45m1qY0EeHPf_fLh0yIKDpSnVt4&response-content-type=application%2Foctet-stream&response-content-disposition=attachment

    public void getDownloadObject() {
        // Bucket的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
        String bucketName = "examplebucket-1250000000";
// 指定文件在 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示下载的文件 picture.jpg 在 folder 路径下
        String key = "exampleobject";
// 方法1 获取下载输入流
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        COSObject cosObject = cosClient.getObject(getObjectRequest);
        COSObjectInputStream cosObjectInput = cosObject.getObjectContent();
// 下载对象的 CRC64
        String crc64Ecma = cosObject.getObjectMetadata().getCrc64Ecma();
// 关闭输入流
        try {
            cosObjectInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

// 方法2 下载文件到本地的路径，例如 D 盘的某个目录
        String outputFilePath = "exampleobject";
        File downFile = new File(outputFilePath);
        getObjectRequest = new GetObjectRequest(bucketName, key);
        ObjectMetadata downObjectMeta = cosClient.getObject(getObjectRequest, downFile);
    }
}
