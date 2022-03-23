package com.fx.pan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fx.pan.domain.FileBean;
import com.fx.pan.vo.FileListVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author leaving
 * @Date 2022/1/19 11:40
 * @Version 1.0
 */
@Repository
public interface  FileMapper extends BaseMapper<FileBean> {
    int insertFile(FileBean file);


    int updateFileNameById(Long fileId, String fileName);

    List<FileBean> selectDeletedFile(Long id);

    int deleteRecycleFileById(Long id,Long userId);

    int restoreDeletedFileById(Long id,Long userId);

    boolean deleteFileByIdAndUserId(Long id, Long userId);

    List<FileListVo> selectFileByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    Long selectCountByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    List<FileListVo> selectFileNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);

}
