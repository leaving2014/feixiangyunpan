package com.fx.pan.mapper;

import com.fx.pan.domain.Storage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author leaving
* @description 针对表【storage(用户存储空间表)】的数据库操作Mapper
* @createDate 2022-01-25 13:43:28
* @Entity com.fx.pan.domain.Storage
*/
@Repository
public interface StorageMapper extends BaseMapper<Storage> {

    int insertUserStorage(Storage storage);

    int updateUserStorage(Storage storage);

    boolean updateStorageUse(Long fileSize, Long userId);

    // Storage updateStorageUse(Long fileSize, Long userId);
}




