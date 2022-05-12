package com.fx.pan.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fx.pan.domain.FileBean;

import java.util.List;

/**
 * @author leaving
 * @date 2022/4/8 16:49
 * @version 1.0
 */
public interface FileManageService extends IService<FileBean> {
    List<FileBean> fileList(String path,String query, int pageNum, int pageSize, boolean onlyFile, Long uid);


    Integer fileListTotal(String path, String query,boolean onlyFile, int pageNum, int pageSize, Long uid);

    List<FileBean> search(String keyword, int pageNum, int pageSize);

    Integer searchTotal(String keyword);

    int rename(Long id, String name);

    int deleteFileById(Long id);

    FileBean selectFileById(Long id);

    List<FileBean> fileListByType(Integer fileType, int pageNum, int pageSize);

    Integer fileListByTypeTotal(Integer fileType);

    int updateAudit(Long id, Integer audit);
}
