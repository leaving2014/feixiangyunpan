package com.fx.pan.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fx.pan.domain.SysOperationLog;
import com.fx.pan.service.SysOperationLogService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * @author leaving
 * @version 1.0
 * @date 2022/5/12 9:15
 */

@Service("sysOperationLogService")
public class SysOperationLogServiceImpl implements SysOperationLogService {
    @Override
    public boolean saveBatch(Collection<SysOperationLog> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<SysOperationLog> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<SysOperationLog> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(SysOperationLog entity) {
        return false;
    }

    @Override
    public SysOperationLog getOne(Wrapper<SysOperationLog> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<SysOperationLog> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<SysOperationLog> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<SysOperationLog> getBaseMapper() {
        return null;
    }

    @Override
    public Class<SysOperationLog> getEntityClass() {
        return null;
    }
}
