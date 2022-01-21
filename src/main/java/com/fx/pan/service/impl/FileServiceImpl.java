package com.fx.pan.service.impl;

import com.fx.pan.domain.FileBean;
import com.fx.pan.mapper.FileMapper;
import com.fx.pan.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author leaving
 * @Date 2022/1/18 19:01
 * @Version 1.0
 */

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileMapper fileMapper;


    // @Override
    // public void createFolder(String fileName, String filePath, Long userId) {
    //     fileMapper.insert(file)
    // }

    @Override
    public boolean createFolder(FileBean createFile) {
        System.out.println(createFile);
        return fileMapper.insertFile(createFile)>0;
        // return fileMapper.insert(createFile)>0;
    }

    @Override
    public boolean fileRename(Long fileId, String fileName) {
        return fileMapper.updateFileNameById(fileId, fileName) > 0 ;
    }
}
