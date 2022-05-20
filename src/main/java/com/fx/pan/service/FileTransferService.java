package com.fx.pan.service;

import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.Chunk;
import com.fx.pan.dto.file.DownloadFileDTO;
import com.fx.pan.dto.file.PreviewDTO;
import com.fx.pan.dto.file.UploadFileDTO;
import com.fx.pan.vo.file.UploadFileVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author leaving
 * @date 2021/12/14 9:16
 * @version 1.0
 */
public interface FileTransferService {


    ResponseResult fileUploadPost(Chunk chunk, HttpServletResponse response, String filePath, String relativePath);
    UploadFileVo uploadFileSpeed(UploadFileDTO uploadFileDto);

}
