package com.fx.pan.factory.utils;

import com.fx.pan.factory.config.TxCosConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author leaving
 * @date 2022/3/19 16:47
 * @version 1.0
 */

public class TxCosUtil {
    public TxCosUtil() {
    }

    @Value("${tengxun.accessKey}")
    private static String accessKey;
    @Value("${tengxun.secretKey}")
    private static String secretKey;
    @Value("${tengxun.bucketRegion}")
    private static String bucketRegion;
    @Value("${tengxun.bucketName}")
    private static String bucketName;
    @Value("${tengxun.path}")
    private static String path;
    @Value("${tengxun.qianzui}")
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

    public static COSClient getCOSClient(TxCosConfig txCosConfig) {

        return cosclient;
    }

}
