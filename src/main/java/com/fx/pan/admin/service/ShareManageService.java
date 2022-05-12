package com.fx.pan.admin.service;

import com.fx.pan.domain.Share;
import com.fx.pan.vo.share.ShareFileListVO;

import java.util.List;

/**
 * @author leaving
 * @date 2022/4/8 21:46
 * @version 1.0
 */
public interface ShareManageService {
    List<ShareFileListVO> shareList(Integer pageNum, Integer pageSize);

    int deleteShare(Long id);

    Integer shareListTotal();

    int clearShare();
}
