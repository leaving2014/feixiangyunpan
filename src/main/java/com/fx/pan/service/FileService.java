package com.fx.pan.service;

import com.fx.pan.domain.FileBean;

/**
 * @Author leaving
 * @Date 2022/1/18 19:01
 * @Version 1.0
 */
public interface FileService {


    /**
     *创建文件夹
     * @param createFile
     * @return
     */
    boolean createFolder(FileBean createFile);

    /**
     * 文件重命名
     * @param fileId
     * @param fileName
     */
    boolean fileRename(Long fileId, String fileName);

    // void isFileExist(String filename,String userId){
    //
    // }
}
