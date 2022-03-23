package com.fx.pan.factory.operation.write.product;

import com.fx.pan.factory.config.TxCosConfig;
import com.fx.pan.factory.operation.write.Writer;
import com.fx.pan.factory.operation.write.domain.WriteFile;
import com.fx.pan.factory.utils.TxCosUtil;
import com.qcloud.cos.COSClient;

import java.io.InputStream;

/**
 * @Author leaving
 * @Date 2022/3/19 16:36
 * @Version 1.0
 */

public class TxCosWriter extends Writer {
    private TxCosConfig txCosConfig;

    public TxCosWriter() {
    }

    public TxCosWriter(TxCosConfig txCosConfig) {
        this.txCosConfig = txCosConfig;
    }

    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        COSClient cosClient = TxCosUtil.getInstance();
        // ossClient.putObject(this.aliyunConfig.getOss().getBucketName(), UFOPUtils.getAliyunObjectNameByFileUrl(writeFile.getFileUrl()), inputStream);
        // ossClient.shutdown();
    }
}
