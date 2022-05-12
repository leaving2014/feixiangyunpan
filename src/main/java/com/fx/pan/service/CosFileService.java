package com.fx.pan.service;

import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.ResponseResult;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author leaving
 * @date 2022/1/18 20:53
 * @version 1.0
 */

public interface CosFileService {


    boolean upload(MultipartFile file);

    ImageAuditingResponse fileAudit(FileBean fileBean, Boolean skipUpload);

    ResponseResult createFolder(String filename);
}
