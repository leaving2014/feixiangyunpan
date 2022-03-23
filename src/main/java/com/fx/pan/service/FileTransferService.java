package com.fx.pan.service;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.Chunk;
import com.fx.pan.dto.file.DownloadFileDTO;
import com.fx.pan.dto.file.PreviewDTO;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author leaving
 * @Date 2021/12/14 9:16
 * @Version 1.0
 */
public interface FileTransferService {
    void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO);
    Msg fileUploadPost(Chunk chunk, HttpServletResponse response,String filePath);

    void previewFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO);
}
