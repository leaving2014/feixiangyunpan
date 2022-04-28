package com.fx.pan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.Share;
import com.fx.pan.mapper.FileMapper;
import com.fx.pan.service.ShareService;
import com.fx.pan.mapper.ShareMapper;
import com.fx.pan.vo.share.ShareFileListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author leaving
 * @description 针对表【share(文件分享表)】的数据库操作Service实现
 * @createDate 2022-03-30 11:39:56
 */
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share>
        implements ShareService {

    @Resource
    private ShareMapper shareMapper;

    @Override
    public List<Share> getShareList(Long userId) {
        LambdaQueryWrapper<Share> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Share::getUserId, userId);
        return shareMapper.selectList(queryWrapper);
    }

    @Override
    public int cancelShare(Long id, Long userId) {
        UpdateWrapper<Share> updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id", id);
        updateWrapper.eq("user_id", userId);
        updateWrapper.set("status", 2);
        return shareMapper.update(null, updateWrapper);
    }

    @Override
    public int deleteShare(Long id, Long userId) {
        LambdaQueryWrapper<Share> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Share::getId, id);
        queryWrapper.eq(Share::getUserId, userId);
        return shareMapper.delete(queryWrapper);
    }

    @Override
    public Share selectShareWithBatchNum(String batchNum) {
        LambdaQueryWrapper<Share> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Share::getBatchNum, batchNum);
        return shareMapper.selectOne(queryWrapper);
    }

    @Override
    public List<ShareFileListVO> selectShareFileList(Long userId) {
        return shareMapper.selectShareFileList(userId);
    }
}




