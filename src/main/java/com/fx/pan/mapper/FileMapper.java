package com.fx.pan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fx.pan.domain.FileBean;
import org.springframework.stereotype.Repository;

/**
 * @Author leaving
 * @Date 2022/1/19 11:40
 * @Version 1.0
 */
@Repository
public interface  FileMapper extends BaseMapper<FileBean> {
    public int insertFile(FileBean file);


    public int updateFileNameById(Long fileId, String fileName);
}
