package com.fx.pan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.mapper.ShareMapper;
import com.fx.pan.domain.Share;
import org.springframework.stereotype.Service;
import com.fx.pan.service.ShareService;

/**
 * 文件分享表(Share)表服务实现类
 *
 * @author leaving
 * @since 2022-01-26 23:24:22
 */
@Service("shareService")
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {

}

