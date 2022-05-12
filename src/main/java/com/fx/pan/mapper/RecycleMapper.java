package com.fx.pan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fx.pan.domain.FileBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author leaving
 * @date 2022/4/2 13:19
 * @version 1.0
 */
@Repository
public interface RecycleMapper extends BaseMapper<FileBean> {

    List<FileBean> selectRecycleFileByUserId(Long userId);

    int deleteAllRecycleFileByUserId(Long userId);

    FileBean selectRecycleFileById(Long fid, Long userId);

    int deleteRecycleFileById(Long id, Long userId);

    List<FileBean> secletRecycleList(@Param("query") String query);

    int delelteRecycleFile(Long id);

    int recoverFile(Long id);

    Integer selectRecycleCount(@Param("query") String query);
}
