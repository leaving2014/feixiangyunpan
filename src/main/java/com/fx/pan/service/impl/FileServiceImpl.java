package com.fx.pan.service.impl;

import cn.hutool.Hutool;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.FileBean;
import com.fx.pan.factory.fxUtils;
import com.fx.pan.mapper.FileMapper;
import com.fx.pan.service.FileService;
import com.fx.pan.utils.BeanCopyUtils;
import com.fx.pan.utils.FileUtils;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.vo.FileListVo;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * @Author leaving
 * @Date 2022/1/18 19:01
 * @Version 1.0
 */

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileBean> implements FileService {
    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private RedisCache redisCache;


    @Override
    public boolean createFolder(FileBean createFile) {
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
        queryWrapper.eq(FileBean::getFilePath, s);
        queryWrapper.eq(FileBean::getUserId, user_id);
        List<FileBean> list = fileMapper.selectList(queryWrapper);
        return list;
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
        QueryWrapper<FileBean> queryWrapper = new QueryWrapper<FileBean>();
        queryWrapper.eq("file_path", filePath);
        queryWrapper.eq("file_name", fileName);
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_dir", 1);
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
        return fileMapper.insertFile(fileBean) > 0;
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
    public Msg copyFile(Long copyFileId, String copyFilePath, Long userId) {
        FileBean copyFileBean = fileMapper.selectById(copyFileId);
        boolean fileExist = isFileExist(copyFilePath, copyFileBean.getFileName(),
                copyFileBean.getUserId());
        if (fileExist) {
            return Msg.error(500, "文件已存在");
        }
        FileBean fileBean = BeanCopyUtils.copyBean(copyFileBean, FileBean.class);
        fileBean.setFilePath(copyFilePath);
        System.out.println(fileBean);
        fileBean.setId(null);
        fileBean.setFileCreateTime(copyFileBean.getFileCreateTime());
        fileBean.setFileUpdateTime(new Date());

        Long parentId = selectByFilePath(copyFilePath, userId).getId();
        fileBean.setParentPathId(parentId);
        if (fileMapper.insert(fileBean) > 0) {
            return Msg.success("复制成功");
        } else {
            return Msg.error(500, "复制失败");
        }
    }

    @Override
    public FileBean selectByFilePath(String copyFilePath, Long userId) {
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
    public Msg moveFile(Long fileId, String filePath, Long userId) {

        FileBean target = fileMapper.selectById(fileId);
        UpdateWrapper<FileBean> updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id", fileId).eq("user_id", userId).set("file_path", filePath);
        int flag = fileMapper.update(target, updateWrapper);
        if (flag > 0) {
            return Msg.success("移动成功");
        } else {
            return Msg.error(500, "移动失败");
        }

    }

    @Override
    public boolean restoreFile(Long id, Long userId) {
        return fileMapper.restoreDeletedFileById(id, userId) > 0;
    }

    @SneakyThrows
    @Override
    public boolean unzip(Long fileId, int unzipMode, String filePath) {
        FileBean zipFileBean = fileMapper.selectById(fileId);
        Date date = new Date();
        String dateStr = DateUtil.format(date, "yyyyMMdd");
        String zipFilePath = fxUtils.getStaticPath() + '/' + zipFileBean.getFileUrl();
        String destPath = fxUtils.getStaticPath() + '/' + dateStr;
        // FileUtils.unzip(zipFilePath, destPath);

        boolean unzip = FileUtils.unzipFile(zipFilePath, destPath, zipFileBean, filePath, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                redisCache.set("unzip-" + fileId + "-" + zipFileBean.getUserId(), evt.getNewValue(), 60 * 60 * 24);
                // System.out.println(">>>Source:" + evt.getSource());
                // System.out.println(">>>NewValue:" + evt.getNewValue());
            }
        });
        // FileUtils.unzip(fileId,unzipMode,filePath);
        redisCache.deleteObject("unzip-" + fileId + "-" + zipFileBean.getUserId());
        return unzip;
    }

    @Override
    public Msg cleanFile(Long userId) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FileBean::getUserId, userId);
        queryWrapper.eq(FileBean::getIsDir, "0");
        queryWrapper.orderByDesc(FileBean::getFileUpdateTime);
        List<FileBean> list = fileMapper.selectList(queryWrapper);
        List<FileListVo> fileListVos = BeanCopyUtils.copyBeanList(list, FileListVo.class);

        return Msg.success().put("rows", fileListVos);
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
        return fileMapper.selectList(queryWrapper);
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
    public List<FileBean> fileList(String path, boolean onlyFile, int page, int size) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(path!=null, FileBean::getFilePath, path);
        queryWrapper.eq(onlyFile==true, FileBean::getIsDir, 0);
        // queryWrapper.orderByDesc(FileBean::getFileCreateTime);
        Page<FileBean> pageBean = new Page<>(page, size);
        IPage<FileBean> iPage = fileMapper.selectPage(pageBean, queryWrapper);
        return iPage.getRecords();
    }

}
