package com.fx.pan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.domain.ExcelBean;
import com.fx.pan.domain.FileBean;
import com.fx.pan.mapper.ExcelMapper;
import com.fx.pan.service.ExcelService;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * (Excel)表服务实现类
 *
 * @author leaving
 * @since 2022-04-05 13:20:16
 */
@Service("excelService")
public class ExcelServiceImpl extends ServiceImpl<ExcelMapper, ExcelBean> implements ExcelService {
    @Resource
    private ExcelMapper excelMapper;

    @Override
    public ExcelBean selectByFileId(Long id) {
        LambdaQueryWrapper<ExcelBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ExcelBean::getFileId, id);
        return excelMapper.selectOne(queryWrapper);
    }

    @Override
    public ExcelBean checkExist(Long id) {
        LambdaQueryWrapper<ExcelBean> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ExcelBean::getId, id);
        ExcelBean excelBean = excelMapper.selectOne(queryWrapper);
        if (excelBean == null) {
            return null;
        } else {
            return excelBean;
        }
    }

    @Override
    public boolean updateExcelData(Long id, String data) {
        UpdateWrapper<ExcelBean> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("data", data);
        return excelMapper.update(null, updateWrapper) > 0;
    }

    @Override
    public ExcelBean createCoEditingByFile(FileBean fileBean, String data) {
        ExcelBean excelBean = new ExcelBean();
        excelBean.setFileId(fileBean.getId());
        excelBean.setUserId(fileBean.getUserId());
        excelBean.setCollaborate(1);
        excelBean.setTitle(fileBean.getFileName());
        excelBean.setType(1);
        excelBean.setData(data);
        excelBean.setCreateTime(new Date());
        excelBean.setUpdateTime(new Date());
        excelBean.setIdentifier(fileBean.getIdentifier());
        LambdaQueryWrapper<ExcelBean> queryWrapper = new LambdaQueryWrapper();
        int insert = excelMapper.insert(excelBean);
        if (insert > 0) {
            return excelBean;
        } else {
            return null;
        }
    }
}

