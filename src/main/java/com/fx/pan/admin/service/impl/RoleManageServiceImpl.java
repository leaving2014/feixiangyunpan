package com.fx.pan.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.admin.service.RoleManageService;
import com.fx.pan.domain.Role;
import com.fx.pan.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author leaving
 * @date 2022/5/2 8:25
 * @version 1.0
 */

@Service
public class RoleManageServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleManageService {
    @Resource
    private RoleMapper roleMapper;

    @Override
    public List<Role> selectRoleList() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        return roleMapper.selectList(queryWrapper);
    }
}
