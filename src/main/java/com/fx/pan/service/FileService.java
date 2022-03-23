package com.fx.pan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.FileBean;
import com.fx.pan.vo.FileListVo;

import java.util.List;

/**
 * @Author leaving
 * @Date 2022/1/18 19:01
 * @Version 1.0
 */
public interface FileService extends IService<FileBean>  {


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

    boolean deleteFile(Long id,Long userId);


    List getFileList(String path,Long user_id);

    boolean isFolderExist(String filePath, String fileName, Long userId);

    boolean isFileExist(String filePath, String fileName, Long userId);


    boolean insertFileInfo(FileBean fileBean);

    List<FileBean> getRecycleList(Long id);


    List searchFile(String keywords, Long user_id);


    /**
     * 回收站删除文件
     * @param id
     * @param userId
     * @return
     */
    boolean deleteRecycleFileById(Long id,Long userId);

    Msg copyFile(Long copyFileId, String copyFilePath,Long userId) ;

    Msg moveFile(Long fileId, String filePath,Long userId);

    FileBean selectByFilePath(String filePath, Long userId);

    boolean restoreFile(Long id,Long userId);

    void unzip(Long fileId, int unzipMode, String filePath);

    Msg cleanFile(Long userId);

    List getFileListOfType(String s, Long user_id, String fileType);

    List<FileListVo> selectFileByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    Long selectCountByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);

    List<FileListVo> selectFileNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);

    FileBean selectFileById(long longValue);

    List selectChildFileListByPath(String s);

    int updateFilePathById(Long id, String newPath);

    // void isFileExist(String filename,String userId){
    //
    // }
}
