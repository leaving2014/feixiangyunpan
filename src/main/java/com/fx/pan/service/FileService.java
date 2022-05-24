package com.fx.pan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.FileBean;
import com.fx.pan.vo.FileListVo;
import org.checkerframework.checker.initialization.qual.FBCBottom;

import java.util.List;

/**
 * @author leaving
 * @date 2022/1/18 19:01
 * @version 1.0
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
     *
     * @param fileId
     * @param fileName
     */
    boolean renameFile(Long fileId, String fileName);

    boolean deleteFile(Long id,Long userId);


    List getFileList(String path, Long user_id, Integer isDir);

    List<FileBean> selectOfflineFileList(Long userId);

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

    ResponseResult copyFile(Long copyFileId, String copyFilePath, Long userId) ;

    ResponseResult moveFile(Long fileId, String filePath, Long userId);

    FileBean selectByFilePath(String filePath, Long userId);

    boolean restoreFile(Long id,Long userId);

    boolean unzip(Long fileId, int unzipMode, String filePath, Long t,Long userId);

    ResponseResult cleanFile(Long userId);

    List getFileListOfType(String s, Long user_id, String fileType);

    List<FileListVo> selectFileByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    Long selectCountByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);

    List<FileListVo> selectFileNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount,
                                                long userId);

    Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);

    FileBean selectFileById(Long fileId);

    List selectChildFileListByPath(String s, Long userId);

    int updateFilePathById(Long id, String newPath);

    List selectFileByIdentifier(String identifier);

    List<FileBean> selectFileByParentId(Long id, Long userId);

    List<FileBean> selectFileWithFileSize(long l, Long userId);

    FileBean selectFileByNameAndPath(String fileName, String filePath, Long userId,Integer isDir);

    Integer getAuditFileCount(Long userId, Boolean isAudit);

    int saveUnzipFile(FileBean fileBean, String filePath,Long t,Integer unzipMode);

    FileBean selectParentPath(String path, String pathName, Long userId);
}
