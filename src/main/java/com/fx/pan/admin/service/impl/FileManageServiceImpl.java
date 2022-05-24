package com.fx.pan.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.domain.FileBean;
import com.fx.pan.mapper.FileMapper;
import com.fx.pan.admin.service.FileManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author leaving
 * @date 2022/4/8 16:52
 * @version 1.0
 */

@Service
public class FileManageServiceImpl extends ServiceImpl<FileMapper, FileBean> implements FileManageService {

    @Resource
    private FileMapper fileMapper;


    @Override
    public List<FileBean> fileList(String path, String query,int pageNum, int pageSize, boolean onlyFile, Long userId) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(query != null || !query.equals(""), FileBean::getFileName, query);
        queryWrapper.eq(path != null, FileBean::getFilePath, path);
        queryWrapper.eq(userId != null, FileBean::getUserId, userId);
        queryWrapper.eq(onlyFile == true, FileBean::getIsDir, 0);
        queryWrapper.in(FileBean::getDeleted, Arrays.asList(0, 1));
        // queryWrapper.orderByDesc(FileBean::getFileCreateTime);
        Page<FileBean> pageBean = new Page<>(pageNum, pageSize);
        IPage<FileBean> iPage = fileMapper.selectPage(pageBean, queryWrapper);
        return iPage.getRecords();
        // List<FileBean> fileBeanList = fileMapper.selectFileList(path, onlyFile, userId, pageNum, pageSize);
        // System.out.println(fileBeanList);
        // return fileBeanList;
    }

    @Override
    public Integer fileListTotal(String path,String query, boolean onlyFile, int pageNum, int pageSize, Long uid) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(query != null , FileBean::getFileName, query);
        queryWrapper.eq(path != null, FileBean::getFilePath, path);
        queryWrapper.eq(uid != null, FileBean::getUserId, uid);
        queryWrapper.eq(onlyFile == true, FileBean::getIsDir, 0);
        return fileMapper.selectCount(queryWrapper);
    }

    @Override
    public List<FileBean> search(String keyword, int pageNum, int pageSize) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(keyword != null, FileBean::getFileName, keyword);
        Page<FileBean> pageBean = new Page<>(pageNum, pageSize);
        IPage<FileBean> iPage = fileMapper.selectPage(pageBean, queryWrapper);
        return iPage.getRecords();
    }

    @Override
    public Integer searchTotal(String keyword) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(keyword != null, FileBean::getFileName, keyword);
        return fileMapper.selectCount(queryWrapper);
    }

    @Override
    public int rename(Long id, String name) {
        UpdateWrapper<FileBean> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("fileName", name);
        updateWrapper.eq("id", id);
        return fileMapper.update(null, updateWrapper);
    }

    @Override
    public int deleteFileById(Long id) {
        return fileMapper.deleteById(id);

    }

    @Override
    public FileBean selectFileById(Long id) {
        return fileMapper.selectById(id);
    }

    @Override
    public List<FileBean> fileListByType(Integer fileType, int pageNum, int pageSize) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(fileType < 6, FileBean::getFileType, fileType);
        queryWrapper.notIn(fileType == 6,FileBean::getFileType, Arrays.asList(0, 1, 2, 3, 4, 5));
        // queryWrapper.in("deleted", Arrays.asList(0, 1));
        Page<FileBean> pageBean = new Page<>(pageNum, pageSize);
        IPage<FileBean> iPage = fileMapper.selectPage(pageBean, queryWrapper);
        return iPage.getRecords();
    }

    @Override
    public Integer fileListByTypeTotal(Integer fileType) {
        LambdaQueryWrapper<FileBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(fileType != null, FileBean::getFileType, fileType);
        return fileMapper.selectCount(queryWrapper);
    }

    @Override
    public int updateAudit(Long id, Integer audit) {
        UpdateWrapper<FileBean> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("audit", audit);
        updateWrapper.eq("id", id);
        return fileMapper.update(null, updateWrapper);
    }

}
