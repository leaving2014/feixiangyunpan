package com.fx.pan.factory.upload.product;

import com.fx.pan.exception.UploadException;
import com.fx.pan.factory.FxUtils;
import com.fx.pan.factory.constant.StorageTypeEnum;
import com.fx.pan.factory.constant.UploadFileStatusEnum;
import com.fx.pan.factory.upload.Uploader;
import com.fx.pan.factory.upload.domain.UploadFile;
import com.fx.pan.factory.upload.domain.UploadFileResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leaving
 * @date 2022/3/4 13:02
 * @version 1.0
 */

@Component
public class LocalStorageUploader extends Uploader {

    public static Map<String, String> FILE_URL_MAP = new HashMap<>();

    @Override
    protected UploadFileResult doUploadFlow(CosMultipartFile cosMultipartFile, UploadFile uploadFile) {
        UploadFileResult uploadFileResult = new UploadFileResult();
        try {
            String fileUrl = com.fx.pan.factory.FxUtils.getUploadFileUrl(uploadFile.getIdentifier(), cosMultipartFile.getExtendName());
            if (StringUtils.isNotEmpty(FILE_URL_MAP.get(uploadFile.getIdentifier()))) {
                fileUrl = FILE_URL_MAP.get(uploadFile.getIdentifier());
            } else {
                FILE_URL_MAP.put(uploadFile.getIdentifier(), fileUrl);
            }
            String tempFileUrl = fileUrl + "_tmp";
            String confFileUrl = fileUrl.replace("." + cosMultipartFile.getExtendName(), ".conf");

            File file = new File(FxUtils.getStaticPath() + fileUrl);
            File tempFile = new File(com.fx.pan.factory.FxUtils.getStaticPath() + tempFileUrl);
            File confFile = new File(com.fx.pan.factory.FxUtils.getStaticPath() + confFileUrl);

            //第一步 打开将要写入的文件
            RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
            //第二步 打开通道
            FileChannel fileChannel = raf.getChannel();
            //第三步 计算偏移量
            long position = (uploadFile.getChunkNumber() - 1) * uploadFile.getChunkSize();
            //第四步 获取分片数据
            byte[] fileData = cosMultipartFile.getUploadBytes();
            //第五步 写入数据
            fileChannel.position(position);
            fileChannel.write(ByteBuffer.wrap(fileData));
            fileChannel.force(true);
            fileChannel.close();
            raf.close();
            //判断是否完成文件的传输并进行校验与重命名
            boolean isComplete = checkUploadStatus(uploadFile, confFile);
            uploadFileResult.setFileUrl(fileUrl);
            uploadFileResult.setFileName(cosMultipartFile.getFileName());
            uploadFileResult.setExtendName(cosMultipartFile.getExtendName());
            uploadFileResult.setFileSize(uploadFile.getTotalSize());
            uploadFileResult.setStorageType(StorageTypeEnum.LOCAL);

            if (uploadFile.getTotalChunks() == 1) {
                uploadFileResult.setFileSize(cosMultipartFile.getSize());
            }

            if (isComplete) {
                tempFile.renameTo(file);
                FILE_URL_MAP.remove(uploadFile.getIdentifier());
                uploadFileResult.setStatus(UploadFileStatusEnum.SUCCESS);
            } else {
                uploadFileResult.setStatus(UploadFileStatusEnum.UNCOMPLATE);
            }
        } catch (IOException e) {
            throw new UploadException(e);
        }


        return uploadFileResult;
    }

    @Override
    public void cancelUpload(UploadFile uploadFile) {
    }

    @Override
    protected void doUploadFileChunk(CosMultipartFile cosMultipartFile, UploadFile uploadFile) throws IOException {

    }

    @Override
    protected UploadFileResult organizationalResults(CosMultipartFile cosMultipartFile, UploadFile uploadFile) {
        return null;
    }

}
