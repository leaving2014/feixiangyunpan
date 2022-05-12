package com.fx.pan.factory.operation.download.domain;

import com.fx.pan.factory.config.TxCosConfig;
import com.fx.pan.factory.utils.TxCosUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;

import java.io.InputStream;

/**
 * @author leaving
 * @date 2022/3/19 17:06
 * @version 1.0
 */

public class TxCosDownloader {
    private TxCosConfig txCosConfig;

    public TxCosDownloader() {
    }

    public TxCosDownloader(TxCosConfig txCosConfig) {
        this.txCosConfig = txCosConfig;
    }

    public InputStream getInputStream(DownloadFile downloadFile) {
        COSClient cosClient = TxCosUtil.getInstance();
        COSObject cosObject = cosClient.getObject("1111", "112122");
        // OSSObject ossObject = ossClient.getObject(this.aliyunConfig.getOss().getBucketName(), UFOPUtils.getAliyunObjectNameByFileUrl(downloadFile.getFileUrl()));
        InputStream inputStream = cosObject.getObjectContent();
        // downloadFile.setCosClient(cosObject);
        return inputStream;
    }
}
