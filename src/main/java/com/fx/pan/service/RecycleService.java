package com.fx.pan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fx.pan.domain.FileBean;

import java.util.List;

/**
 * @author leaving
 * @date 2022/3/8 12:20
 * @version 1.0
 */
public interface RecycleService extends IService<FileBean> {
    List<FileBean> selectRecycleByUserId(Long userId);

    int deleteAllRecycleFileByUserId(Long userId);

    FileBean selectRecycleFileById(Long fid, Long userId);

    int deleteRecycleFileById(Long id, Long userId);
}
