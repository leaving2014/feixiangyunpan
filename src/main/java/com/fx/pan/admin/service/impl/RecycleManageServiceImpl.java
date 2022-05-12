package com.fx.pan.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.admin.service.RecycleManageService;
import com.fx.pan.domain.FileBean;
import com.fx.pan.mapper.RecycleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author leaving
 * @date 2022/4/8 21:33
 * @version 1.0
 */

@Service
public class RecycleManageServiceImpl extends ServiceImpl<RecycleMapper, FileBean> implements RecycleManageService {

    @Resource
    private RecycleMapper recycleMapper;
    @Override
    public List<FileBean> selectRecycleList(Integer pageNum, Integer pageSize,String query) {
        PageHelper.startPage(pageNum, pageSize);
        List<FileBean> list = recycleMapper.secletRecycleList(query);
        PageInfo<FileBean> pageInfo = new PageInfo<>(list);
        return pageInfo.getList();
        // return recycleMapper.secletRecycleList(pageNum,pageSize);
    }

    @Override
    public int deleteRecycleFile(Long id) {
        return recycleMapper.delelteRecycleFile(id);
    }

    @Override
    public int recoverFile(Long id) {
        return recycleMapper.recoverFile(id);
    }

    @Override
    public Integer selectRecycleCount(String query) {
        return recycleMapper.selectRecycleCount(query);
    }
}
