package com.fx.pan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fx.pan.domain.Share;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件分享表(Share)表数据库访问层
 *
 * @author leaving
 * @since 2022-01-26 23:24:22
 */
public interface ShareMapper extends BaseMapper<Share> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Share> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Share> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Share> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Share> entities);

}

