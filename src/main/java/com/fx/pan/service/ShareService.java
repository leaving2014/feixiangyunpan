package com.fx.pan.service;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.Share;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fx.pan.vo.share.ShareFileListVO;

import java.util.List;

/**
 * @author leaving
 * @description 针对表【share(文件分享表)】的数据库操作Service
 * @createDate 2022-03-30 11:39:56
 */
public interface ShareService extends IService<Share> {

    List<Share> getShareList(Long userId);

    int cancelShare(Long id, Long userId);

    int deleteShare(Long id, Long userId);

    Share selectShareWithBatchNum(String batchNum);


    List<ShareFileListVO> selectShareFileList(Long userId);
}
