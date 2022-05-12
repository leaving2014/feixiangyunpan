package com.fx.pan.admin.service;

import com.fx.pan.domain.FileBean;

import java.util.List;

/**
 * @author leaving
 * @date 2022/4/8 21:31
 * @version 1.0
 */
public interface RecycleManageService {
    List<FileBean> selectRecycleList(Integer pageNum, Integer pageSize,String query);

    int deleteRecycleFile(Long id);

    int recoverFile(Long id);

    Integer selectRecycleCount(String query);
}
