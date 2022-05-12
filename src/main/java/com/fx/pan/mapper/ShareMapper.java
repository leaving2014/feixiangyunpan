package com.fx.pan.mapper;

import com.fx.pan.domain.Share;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fx.pan.vo.share.ShareFileListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author leaving
 * @description 针对表【share(文件分享表)】的数据库操作Mapper
 * @createDate 2022-03-30 11:39:56
 * @Entity com.fx.pan.domain.Share
 */
public interface ShareMapper extends BaseMapper<Share> {

    List<ShareFileListVO> selectShareFileList(Long userId);

    List<ShareFileListVO> selectShareList(Integer pageNum, Integer pageSize);

    int deleteExpireShareList(Long userId);
}




