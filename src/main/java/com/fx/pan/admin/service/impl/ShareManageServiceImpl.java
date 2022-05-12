package com.fx.pan.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.admin.service.ShareManageService;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.Share;
import com.fx.pan.mapper.FileMapper;
import com.fx.pan.mapper.ShareMapper;
import com.fx.pan.vo.share.ShareFileListVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author leaving
 * @date 2022/4/8 21:47
 * @version 1.0
 */

@Service
public class ShareManageServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareManageService {

    @Resource
    private ShareMapper shareMapper;
    @Override
    public List<ShareFileListVO> shareList(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        // wrapper.orderByDesc(Share::get);
        Page<Share> page = new Page<>(pageNum, pageSize);
        // return shareMapper.selectShareList(pageNum, pageSize);
        return shareMapper.selectShareFileList(null);
        // return shareMapper.selectPage(page, wrapper).getRecords();
    }

    @Override
    public int deleteShare(Long id) {
        return shareMapper.deleteById(id);
    }

    @Override
    public Integer shareListTotal() {
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        return shareMapper.selectCount(wrapper);
    }

    @Override
    public int clearShare() {
        LambdaQueryWrapper<Share> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(Share::getExpiredTime, new Date());
        return shareMapper.delete(queryWrapper);
    }

}
