package com.fx.pan.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.admin.service.ShareManageService;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.Share;
import com.fx.pan.mapper.FileMapper;
import com.fx.pan.mapper.ShareMapper;
import com.fx.pan.mapper.UserMapper;
import com.fx.pan.utils.BeanCopyUtils;
import com.fx.pan.vo.UserVo;
import com.fx.pan.vo.share.ShareFileListVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leaving
 * @version 1.0
 * @date 2022/4/8 21:47
 */

@Service
public class ShareManageServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareManageService {

    @Resource
    private ShareMapper shareMapper;

    @Resource
    FileMapper fileMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public List<ShareFileListVO> shareList(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        // wrapper.orderByDesc(Share::get);
        Page<Share> page = new Page<>(pageNum, pageSize);
        // return shareMapper.selectShareList(pageNum, pageSize);
        // return shareMapper.selectShareFileList(null);
        List<Share> records = shareMapper.selectPage(page, wrapper).getRecords();
        List<ShareFileListVO> shareFileListVOS = records.stream().map(record -> {
            ShareFileListVO shareFileListVO = BeanCopyUtils.copyBean(record, ShareFileListVO.class);
            FileBean fileBean = fileMapper.selectById(record.getFileId());
            shareFileListVO.setFileId(record.getFileId());
            shareFileListVO.setFilePath(fileBean.getFilePath());
            shareFileListVO.setFileName(fileBean.getFileName());
            shareFileListVO.setFileSize(fileBean.getFileSize());
            shareFileListVO.setFileType(fileBean.getFileType());
            shareFileListVO.setIdentifier(fileBean.getIdentifier());
            shareFileListVO.setIsDir(fileBean.getIsDir());
            shareFileListVO.setFileExt(fileBean.getFileExt());
            shareFileListVO.setFileCreateTime(fileBean.getFileCreateTime());
            shareFileListVO.setUser(BeanCopyUtils.copyBean(userMapper.selectById(record.getUserId()), UserVo.class));
            return shareFileListVO;
        }).collect(Collectors.toList());
        return shareFileListVOS;
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
