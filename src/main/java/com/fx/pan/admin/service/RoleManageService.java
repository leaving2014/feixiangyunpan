package com.fx.pan.admin.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.Role;
import com.fx.pan.mapper.RecycleMapper;
import com.fx.pan.mapper.RoleMapper;

import java.util.List;

/**
 * @author leaving
 * @date 2022/5/2 8:25
 * @version 1.0
 */

public interface RoleManageService {
    List<Role> selectRoleList();
}
