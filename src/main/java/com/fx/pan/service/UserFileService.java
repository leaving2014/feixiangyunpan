package com.fx.pan.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.domain.FileBean;
import com.fx.pan.mapper.FileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author leaving
 * @Date 2022/1/20 13:59
 * @Version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class UserFileService  extends ServiceImpl<FileMapper, FileBean> {

}
