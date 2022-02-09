package com.fx.pan.service;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.FileBean;

import java.util.List;

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

    boolean deleteFile(Long id,Long user_id);


    List getFileList(String path,Long user_id);

    boolean isFolderExist(String filePath, String fileName, Long userId);

    boolean isFileExist(String filePath, String fileName, Long userId);


    boolean insertFileInfo(FileBean fileBean);

    List<FileBean> getRecycleList(Long id);


    List searchFile(String keywords, Long user_id);


    // 回收站操作
    boolean deleteRecycleFileById(Long id,Long userId);

    Msg copyFile(Long copyFileId, Long copyFilePath) ;

    Msg moveFile(Long moveFileId, Long moveFilePathId);

    boolean restoreFile(Long id,Long user_id);

    void unzip(Long fileId, int unzipMode, String filePath);

    Msg cleanFile(Long userId);

    // void isFileExist(String filename,String userId){
    //
    // }
}
