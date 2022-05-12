package com.fx.pan.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.admin.service.UserManageService;
import com.fx.pan.domain.Share;
import com.fx.pan.domain.User;
import com.fx.pan.mapper.ShareMapper;
import com.fx.pan.mapper.UserMapper;
import lombok.experimental.PackagePrivate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author leaving
 * @date 2022/4/8 21:49
 * @version 1.0
 */

@Service
public class UserManageServiceImpl  extends ServiceImpl<UserMapper, User> implements UserManageService {

    @Resource
    private UserMapper userMapper;
    @Override
    public List<User> selectUsers(String query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query != null, User::getUserName, query);
        Page<User> page = new Page<>(pageNum, pageSize);

        return userMapper.selectPage(page, wrapper).getRecords();
    }

    @Override
    public Integer selectUsersTotal(String query) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query != null, User::getUserName, query);
        return userMapper.selectCount(wrapper);
    }

    @Override
    public int changeStatus(Long id, Integer status) {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id);
        wrapper.set("status", status);
        return userMapper.update(null, wrapper);
    }

    @Override
    public User selectUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public int updateUser(User user) {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", user.getId());
        return userMapper.update(user, wrapper);
    }
}
