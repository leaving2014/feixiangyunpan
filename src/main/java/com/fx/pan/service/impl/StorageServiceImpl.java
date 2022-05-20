package com.fx.pan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.domain.Storage;
import com.fx.pan.mapper.StorageMapper;
import com.fx.pan.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;

/**
* @author leaving
* @description 针对表【storage(用户存储空间表)】的数据库操作Service实现
* @createDate 2022-01-25 13:43:28
*/
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage>
    implements StorageService{

    @Resource
    private StorageMapper storageMapper;
    @Override
    public boolean insertUserStorage(Storage storage) {
        return storageMapper.insertUserStorage(storage)>0;
    }

    @Override
    public Storage getUserStorage(Serializable user_id) {
        LambdaQueryWrapper<Storage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Storage::getUserId, user_id);
        return storageMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean updateStorageUse(Long fileSize, Long userId) {
        return storageMapper.updateStorageUse(fileSize, userId);
    }

    @Override
    public boolean checkStorage(Long userId, long totalSize) {
        Storage storage = storageMapper.selectById(userId);
        if(storage.getStorageSizeUsed()+totalSize>storage.getStorageSize()){
            return false;
        } else {
            return true;
        }
    }
}




