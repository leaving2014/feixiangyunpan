package com.fx.pan.service;

import com.fx.pan.domain.Storage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;

/**
* @author leaving
* @description 针对表【storage(用户存储空间表)】的数据库操作Service
* @createDate 2022-01-25 13:43:28
*/
public interface StorageService extends IService<Storage> {

    boolean insertUserStorage(Storage storage);

    Storage getUserStorage(Serializable user_id);

    boolean updateStorageUse(Long fileSize, Long userId);

    boolean checkStorage(Long userId, long totalSize);

}
