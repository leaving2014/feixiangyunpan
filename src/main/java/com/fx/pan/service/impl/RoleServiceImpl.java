package com.fx.pan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.domain.Role;
import com.fx.pan.mapper.RoleMapper;
import com.fx.pan.service.RoleService;
import org.springframework.stereotype.Service;

/**
* @author leaving
* @description 针对表【role(角色表)】的数据库操作Service实现
* @createDate 2022-01-21 16:26:03
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
implements RoleService {

}
