package com.fx.pan.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.Share;
import com.fx.pan.mapper.RecycleMapper;
import com.fx.pan.mapper.ShareMapper;
import com.fx.pan.service.RecycleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author leaving
 * @date 2022/4/2 13:17
 * @version 1.0
 */
@Service
public class RecycleServiceImpl extends ServiceImpl<RecycleMapper, FileBean> implements RecycleService {
    @Resource
    private RecycleMapper recycleMapper;

    @Override
    public List<FileBean> selectRecycleByUserId(Long userId) {
        return recycleMapper.selectRecycleFileByUserId(userId);
    }

    @Override
    public int deleteAllRecycleFileByUserId(Long userId) {
        return recycleMapper.deleteAllRecycleFileByUserId(userId);
    }

    @Override
    public FileBean selectRecycleFileById(Long fid, Long userId) {
        return recycleMapper.selectRecycleFileById(fid, userId);
    }

    @Override
    public int deleteRecycleFileById(Long id, Long userId) {
        return recycleMapper.deleteRecycleFileById(id, userId);
    }

}
