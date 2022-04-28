package com.fx.pan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fx.pan.domain.FileBean;

import java.util.List;

/**
 * @Author leaving
 * @Date 2022/4/2 13:19
 * @Version 1.0
 */

public interface RecycleMapper extends BaseMapper<FileBean> {

    List<FileBean> selectRecycleFileByUserId(Long userId);

    int deleteAllRecycleFileByUserId(Long userId);

    FileBean selectRecycleFileById(Long fid, Long userId);

    int deleteRecycleFileById(Long id, Long userId);
}
