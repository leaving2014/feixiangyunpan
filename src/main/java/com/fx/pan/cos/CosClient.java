package com.fx.pan.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Author leaving
 * @Date 2021/11/26 22:31
 * @Version 1.0
 */

/**
 * 生成腾讯Cos存储client
 * @author leaving
 */
public class CosClient {
    @Value("${spring.tengxun.accessKey}")
    private static String accessKey;
    @Value("${spring.tengxun.secretKey}")
    private static String secretKey;
    @Value("${spring.tengxun.bucketRegion}")
    private static String bucketRegion;
    @Value("${spring.tengxun.bucketName}")
    private static String bucketName;
    @Value("${spring.tengxun.path}")
    private static String path;
    @Value("${spring.tengxun.qianzui}")
    private static String qianzui;


    // 1 初始化用户身份信息(secretId, secretKey)
    private  static COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
    // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
    private  static ClientConfig clientConfig = new ClientConfig(new Region(bucketRegion));
    // 3 生成cos客户端
    private  static  COSClient cosclient = new COSClient(cred, clientConfig);

    public static COSClient  getInstance(){
        return cosclient;
    }

}
