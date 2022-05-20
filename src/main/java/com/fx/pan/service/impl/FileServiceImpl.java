package com.fx.pan.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.common.Constants;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.FileBean;
import com.fx.pan.factory.FxUtils;
import com.fx.pan.mapper.FileMapper;
import com.fx.pan.service.FileService;
import com.fx.pan.utils.*;
import com.fx.pan.utils.file.FileTypeUtils;
import com.fx.pan.vo.FileListVo;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author leaving
 * @version 1.0
 * @date 2022/1/18 19:01
 */

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileBean> implements FileService {
    @Resource
    private FileMapper fileMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private FileUtils fileUtils;

    @Value("${fx.absoluteFilePath}")
    private String absoluteFilePath;


    @Override
    public boolean createFolder(FileBean createFile) {
        createFile.setAudit(1);
        return fileMapper.insert(createFile) > 0;
        // return fileMapper.insertFile(createFile)>0;
    }

    @Override
    public boolean renameFile(Long fileId, String fileName) {
        return fileMapper.updateFileNameById(fileId, fileName) > 0;
    }

    @Override
    public boolean deleteFile(Long id, Long userId) {
        return fileMapper.deleteFileByIdAndUserId(id, userId);
    }

    @Override
    public List getFileList(String s, Long user_id, Integer isDir) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        if (isDir == 1) {
            queryWrapper.eq(FileBean::getIsDir, 1);
        }
        queryWrapper.eq(FileBean::getAudit, 1);
        queryWrapper.eq(FileBean::getFilePath, s);
        queryWrapper.eq(FileBean::getUserId, user_id);
        List<FileBean> list = fileMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public List<FileBean> selectOfflineFileList(Long userId) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FileBean::getUserId, userId);
        queryWrapper.eq(FileBean::getOrigin, 1);
        return fileMapper.selectList(queryWrapper);
    }

    /**
     * 判断目录是否存在 存在:True 不存在:False
     *
     * @param filePath
     * @param fileName
     * @param userId
     * @return
     */
    @Override
    public boolean isFolderExist(String filePath, String fileName, Long userId) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper<FileBean>(FileBean.class);
        queryWrapper.eq(FileBean::getFilePath, filePath);
        queryWrapper.eq(FileBean::getFileName, fileName);
        queryWrapper.eq(FileBean::getUserId, userId);
        queryWrapper.eq(FileBean::getIsDir, 1);
        FileBean fileBean = fileMapper.selectOne(queryWrapper);
        if (fileBean != null) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 判断文件是否存在 存在:True 不存在:False
     *
     * @param filePath
     * @param fileName
     * @param userId
     * @return
     */
    @Override
    public boolean isFileExist(String filePath, String fileName, Long userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("file_path", filePath);
        queryWrapper.eq("file_name", fileName);
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_dir", 0);
        FileBean fileBean = fileMapper.selectOne(queryWrapper);
        if (fileBean != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean insertFileInfo(FileBean fileBean) {
        return fileMapper.insert(fileBean) > 0;
        // return fileMapper.insertFile(fileBean) > 0;
    }

    @Override
    public List<FileBean> getRecycleList(Long id) {
        return fileMapper.selectDeletedFile(id);
    }

    @Override
    public List searchFile(String keywords, Long user_id) {


        QueryWrapper<FileBean> queryWrapper = new QueryWrapper<FileBean>();
        queryWrapper.eq("user_id", user_id);
        queryWrapper.like("file_name", keywords);
        List<FileBean> fileBeans = fileMapper.selectList(queryWrapper);
        return fileBeans;
    }

    @Override
    public boolean deleteRecycleFileById(Long id, Long userId) {
        return fileMapper.deleteRecycleFileById(id, userId) > 0;
    }

    @Override
    public ResponseResult copyFile(Long copyFileId, String copyFilePath, Long userId) {
        FileBean copyFileBean = fileMapper.selectById(copyFileId);
        boolean fileExist = isFileExist(copyFilePath, copyFileBean.getFileName(),
                copyFileBean.getUserId());
        if (fileExist) {
            return ResponseResult.error(500, "文件已存在");
        }
        FileBean fileBean = BeanCopyUtils.copyBean(copyFileBean, FileBean.class);
        fileBean.setFilePath(copyFilePath);
        System.out.println(fileBean);
        fileBean.setId(null);
        fileBean.setFileCreateTime(copyFileBean.getFileCreateTime());
        fileBean.setFileUpdateTime(new Date());

        Long parentId = -1L;
        if (!copyFilePath.equals("/")) {
            parentId = selectByFilePath(copyFilePath, userId).getId();
        }
        fileBean.setParentPathId(parentId);
        if (fileMapper.insert(fileBean) > 0) {
            return ResponseResult.success("复制成功");
        } else {
            return ResponseResult.error(500, "复制失败");
        }
    }

    @Override
    public FileBean selectByFilePath(String copyFilePath, Long userId) {
        if (copyFilePath.equals("/")) {

        }
        String[] pathArr = copyFilePath.split("/");
        String fileName = pathArr[pathArr.length - 1];
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FileBean::getFileName, fileName);
        queryWrapper.eq(FileBean::getUserId, userId);
        queryWrapper.eq(FileBean::getIsDir, 1);
        FileBean fileBean = fileMapper.selectOne(queryWrapper);
        System.out.println(fileBean);
        return fileBean;
    }

    @Override
    public ResponseResult moveFile(Long fileId, String filePath, Long userId) {

        FileBean target = fileMapper.selectById(fileId);
        UpdateWrapper<FileBean> updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id", fileId).eq("user_id", userId).set("file_path", filePath);
        int flag = fileMapper.update(target, updateWrapper);
        if (flag > 0) {
            return ResponseResult.success("移动成功");
        } else {
            return ResponseResult.error(500, "移动失败");
        }

    }

    @Override
    public boolean restoreFile(Long id, Long userId) {
        return fileMapper.restoreDeletedFileById(id, userId) > 0;
    }

    @SneakyThrows
    @Override
    public boolean unzip(Long fileId, int unzipMode, String filePath, Long t, Long userId) {

        String type = "unzip";
        FileBean zipFileBean = fileMapper.selectById(fileId);
        Date date = new Date();
        String dateStr = DateUtil.format(date, "yyyyMMdd");
        String zipFilePath = FxUtils.getStaticPath() + '/' + zipFileBean.getFileUrl();
        String destPath = com.fx.pan.factory.FxUtils.getStaticPath() + '/' + dateStr;
        fileUtils.unzipFile(zipFilePath, destPath, zipFileBean, filePath, t, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + type + "-" + userId +
                                ":" + t,
                        (int) evt.getNewValue() >= 90 ? 100 : evt.getNewValue());
                // redisCache.set("unzip-" + fileId + "-" + zipFileBean.getUserId(), evt.getNewValue(), 60 * 60 * 24);
            }
        });
        redisCache.deleteObject("unzip-" + fileId + "-" + zipFileBean.getUserId());
        Map<String, Object> map = new HashMap<>();
        return true;
    }

    @Override
    public ResponseResult cleanFile(Long userId) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FileBean::getUserId, userId);
        queryWrapper.eq(FileBean::getIsDir, "0");
        queryWrapper.orderByDesc(FileBean::getFileUpdateTime);
        List<FileBean> list = fileMapper.selectList(queryWrapper);
        List<FileListVo> fileListVos = BeanCopyUtils.copyBeanList(list, FileListVo.class);
        Map<String, Object> map = new HashMap<>();
        map.put("rows", fileListVos);
        return ResponseResult.success(map);
    }

    @Override
    public List getFileListOfType(String s, Long user_id, String fileType) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FileBean::getUserId, user_id);
        queryWrapper.eq(FileBean::getIsDir, 0);
        queryWrapper.eq(FileBean::getFileType, fileType);
        queryWrapper.orderByDesc(FileBean::getFileUpdateTime);
        List<FileBean> list = fileMapper.selectList(queryWrapper);
        List<FileListVo> fileListVos = BeanCopyUtils.copyBeanList(list, FileListVo.class);
        return fileListVos;
    }

    @Override
    public List<FileListVo> selectFileByExtendName(List<String> fileNameList, Long beginCount, Long pageCount,
                                                   long userId) {
        return fileMapper.selectFileByExtendName(fileNameList, beginCount, pageCount, userId);
    }

    @Override
    public Long selectCountByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId) {
        return null;
    }

    @Override
    public List<FileListVo> selectFileNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount,
                                                       long userId) {
        return fileMapper.selectFileNotInExtendNames(fileNameList, beginCount, pageCount, userId);
    }

    @Override
    public Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId) {
        return null;
    }

    @Override
    public FileBean selectFileById(long id) {
        return fileMapper.selectById(id);
    }

    @Override
    public List selectChildFileListByPath(String path, Long userId) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        // queryWrapper.likeLeft(FileBean::getFilePath, path);
        queryWrapper.like(FileBean::getFilePath, path + "%");
        queryWrapper.eq(FileBean::getUserId, userId);
        List<FileBean> list = fileMapper.selectList(queryWrapper);
        // List<FileListVo> fileListVos = BeanCopyUtils.copyBeanList(list, FileListVo.class);
        return list;
    }

    @Override
    public int updateFilePathById(Long id, String newPath) {
        UpdateWrapper<FileBean> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("file_path", newPath);
        Integer rows = fileMapper.update(null, updateWrapper);
        return rows;
    }

    @Override
    public List selectFileByIdentifier(String identifier) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FileBean::getIdentifier, identifier);
        // queryWrapper.in(FileBean::getDeleted, 0, 1);
        return fileMapper.selectList(queryWrapper);
        // return fileMapper.selectFileByIdentifier(identifier);
        // return fileMapper.selectList(queryWrapper);
    }

    @Override
    public List<FileBean> selectFileByParentId(Long id, Long userId) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FileBean::getParentPathId, id);
        queryWrapper.eq(FileBean::getUserId, userId);
        return fileMapper.selectList(queryWrapper);
    }

    @Override
    public List<FileBean> selectFileWithFileSize(long l, Long userId) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FileBean::getUserId, userId);
        queryWrapper.gt(FileBean::getFileSize, l * 1024 * 1024);
        return fileMapper.selectList(queryWrapper);
    }


    @Override
    public FileBean selectFileByNameAndPath(String fileName, String filePath, Long userId, Integer isDir) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FileBean::getFileName, fileName);
        queryWrapper.eq(FileBean::getFilePath, filePath);
        queryWrapper.eq(FileBean::getIsDir, isDir);
        FileBean fileBean = fileMapper.selectOne(queryWrapper);
        System.out.println("fileBean = " + fileBean);
        return fileBean;
    }

    @Override
    public Integer getAuditFileCount(Long userId, Boolean isAudit) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FileBean::getUserId, userId);
        queryWrapper.in(isAudit == false, FileBean::getAudit, Arrays.asList(-1, 0));
        queryWrapper.in(isAudit == true, FileBean::getAudit, Arrays.asList(1));
        return fileMapper.selectCount(queryWrapper);
    }

    @SneakyThrows
    public FileBean getUnzipFileBean(String filePath, Long userId, String fullPath, String dateStr, Integer isDir) {
        FileBean fileBean = new FileBean();
        File file = new File(fullPath);
        String fileName = file.getName();
        fileBean.setFileName(file.getName());
        fileBean.setFilePath(filePath);
        fileBean.setUserId(userId);
        fileBean.setAudit(1);


        if (!(isDir == 1)) {
            fileBean.setIsDir(0);
            fileBean.setFileSize(new File(fullPath).length());
            Integer fileType = FileTypeUtils.getFileTypeByExtendName(FileUtils.getFileExt(file.getName()));
            fileBean.setFileType(fileType);
            fileBean.setId(null);
            fileBean.setFileCreateTime(new Date());
            fileBean.setFileUpdateTime(new Date());
            String md5 = Md5Utils.md5HashCode32(file.getAbsolutePath());
            fileBean.setIdentifier(md5);
            String newName = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".")), md5);
            fileBean.setFileUrl(dateStr + "/" + newName);
        } else {
            fileBean.setIsDir(1);
        }

        return fileBean;
    }

    //使用递归遍历文件夹及子文件夹中文件
    @SneakyThrows
    public void filesDirs(File file, String savePath, String tmpPath, String filePath, FileBean fileBean,
                          String dateStr, Long t, Integer unzipMode) {
        //File对象是文件或文件夹的路径，第一层判断路径是否为空
        if (file != null) {
            //第二层路径不为空，判断是文件夹还是文件
            if (file.isDirectory()) {
                if (unzipMode == 1 || unzipMode == 3) {
                    System.out.println("unzipMode1-3 " + unzipMode + " filePath=== " + file.getAbsolutePath());
                    if (!file.getAbsolutePath().endsWith("tmp") || !file.getAbsolutePath().endsWith(t.toString())) {
                        System.out.println("符合条件filePath=== " + file.getAbsolutePath());
                        FileBean fileBean1 = getUnzipFileBean(file.getAbsolutePath(), fileBean.getUserId(), dateStr,
                                dateStr, 1);
                        String folderFilePath =
                                file.getAbsolutePath().replaceAll("\\\\", "/").replace(tmpPath, "").replace("/" + file.getName(), "");
                        String path = (folderFilePath.isEmpty() ? "/" : folderFilePath);
                        fileBean1.setFileName(file.getName());
                        String absolutePath = file.getAbsolutePath().replaceAll("\\\\", "/");
                        String folderPath = absolutePath.replace(tmpPath, filePath).replace("/" + file.getName(), "");
                        fileBean1.setFilePath(folderPath);
                        fileBean1.setFileCreateTime(new Date());
                        fileBean1.setFileUpdateTime(new Date());
                        if (!fileBean1.getFileName().equals(t.toString())) {
                            fileMapper.insert(fileBean1);
                        }
                    }
                } else if (unzipMode == 2) {
                    System.out.println("unzipMode2文件夹：" + file.getAbsolutePath());
                    if (!file.getAbsolutePath().endsWith("tmp") || !file.getAbsolutePath().endsWith(t.toString())) {
                        FileBean folderFileBean = new FileBean();
                        folderFileBean.setIsDir(1);
                        folderFileBean.setUserId(fileBean.getUserId());
                        folderFileBean.setFileName(file.getName());
                        folderFileBean.setAudit(1);
                        String folderName = file.getName();

                        String folderPath = filePath + "/" + file.getName();
                        String folderFilePath =
                                file.getAbsolutePath().replaceAll("\\\\", "/").replace(tmpPath, "").replace("/" + file.getName(), "");
                        folderFileBean.setFileCreateTime(new Date());
                        folderFileBean.setFileUpdateTime(new Date());
                        String path =
                                (filePath.equals("/") ? "" : filePath) + folderFilePath.replace("/" + folderName, "");
                        folderFileBean.setFilePath(folderFilePath);
                        if (unzipMode == 1 || unzipMode == 3) {
                            path = (folderFilePath.isEmpty() ? "/" : folderFilePath);
                        } else if (unzipMode == 2) {
                            path = (filePath.equals("/") ? "" : filePath) + folderFilePath.replace("/" + folderName,
                                    "");
                        }
                        folderFileBean.setFilePath(path);
                        System.out.println("保存文件夹到数据库：path===" + path + " name===" + folderFileBean.getFileName());
                        if (!folderFileBean.getFileName().equals(t.toString())) {
                            fileMapper.insert(folderFileBean);
                        }

                    }
                }
                // System.out.println("file.getName()以16结尾："+file.getAbsolutePath().endsWith(t.toString()));

                System.out.println("当前是文件夹===" + file.getAbsolutePath());
                //进入这里说明为文件夹，此时需要获得当前文件夹下所有文件，包括目录
                File[] files = file.listFiles();//注意:这里只能用listFiles()，不能使用list()
                //files下的所有内容，可能是文件夹，也可能是文件，那么需要一个个去判断是文件还是文件夹，这个判断过程就是这里封装的方法
                //因此可以调用自己来判断，实现递归
                for (File flies2 : files) {
                    filesDirs(flies2, savePath, tmpPath, filePath, fileBean, dateStr, t, unzipMode);
                }
            } else {
                System.out.println("当前是文件是===" + file.getAbsolutePath());
                String fileBeanPath = file.getAbsolutePath().replaceAll("\\\\", "/").replace(tmpPath, "");
                FileBean fileBean1 = BeanCopyUtils.copyBean(fileBean, FileBean.class);
                String fileName = file.getName();
                // fileBean1.setFilePath(filePath + );
                fileBean1.setFilePath((filePath.equals("/") ? "" : filePath) + fileBeanPath.replace("/" + fileName,
                        ""));
                fileBean1.setFileName(file.getName());
                fileBean1.setFileSize(file.length());
                fileBean1.setFileExt(file.getName().substring(file.getName().lastIndexOf(".") + 1));
                Integer fileType = FileTypeUtils.getFileTypeByExtendName(FileUtils.getFileExt(file.getName()));
                fileBean1.setFileType(fileType);
                fileBean1.setId(null);
                fileBean1.setFileCreateTime(new Date());
                fileBean1.setFileUpdateTime(new Date());
                String md5 = Md5Utils.md5HashCode32(file.getAbsolutePath());
                fileBean1.setIdentifier(md5);
                String newName = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".")),
                        fileBean1.getIdentifier());
                fileBean1.setIsDir(0);
                fileMapper.insert(fileBean1);
                file.renameTo(new File(savePath + "/" + newName));
            }
        }
    }

    @Override
    public int saveUnzipFile(FileBean fileBean, String filePath, Long t, Integer unzipMode) {
        Date d = new Date(t);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sf.format(d);
        String savePath = absoluteFilePath + "/" + dateStr;
        String tmpPath = absoluteFilePath + "/tmp/" + t;
        File saveFolder = new File(savePath);
        if (!saveFolder.exists()) {
            saveFolder.mkdirs();
        }
        // 遍历 tmpPath 下的文件和文件夹
        //File对象是文件或文件夹的路径，第一层判断路径是否为空
        File file = new File(tmpPath);
        filesDirs(file, savePath, tmpPath, filePath, fileBean, dateStr, t, unzipMode);
        return 0;
    }

    @Override
    public FileBean selectParentPath(String path, String pathName, Long userId) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper<>();
        // if (pathName.equals("")) {
        //     // 文件的父目录
        //     String folderPath = path.substring(0, path.lastIndexOf("/"));
        //     String folderName = path.substring(path.lastIndexOf("/") + 1);
        //     queryWrapper.eq(FileBean::getFilePath, folderPath);
        //     queryWrapper.eq(FileBean::getFileName, folderName);
        // } else {
        //     // 文件夹的父目录
        //     queryWrapper.eq(FileBean::getFilePath, path);
        //     queryWrapper.eq(FileBean::getFileName, pathName);
        // }

        String folderPath = path.substring(0, path.lastIndexOf("/"));
        String folderName = path.substring(path.lastIndexOf("/") + 1);
        queryWrapper.eq(FileBean::getFilePath, folderPath);
        queryWrapper.eq(FileBean::getFileName, folderName);

        queryWrapper.eq(FileBean::getUserId, userId);
        return fileMapper.selectOne(queryWrapper);
    }


}
