package com.fx.pan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.domain.Role;
import com.fx.pan.domain.User;
import com.fx.pan.mapper.RoleMapper;
import com.fx.pan.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author leaving
 * @Date 2022/1/15 12:24
 * @Version 1.0
 */

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Resource
    private LoginUser loginUser;

    public UserDetails createLoginUser(Long id, User user) {
        return new LoginUser(id, user);
    }

    public UserDetails createLoginUser(User user, List<String> permissions) {
        return new LoginUser(user, permissions);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, username);
        User user = userMapper.selectOne(queryWrapper);

        if (Objects.isNull(user)) {
            throw new RuntimeException("用户名或者密码错误");
        }

        // 查询对应的权限信息
        String role = user.getRole();
        Role userRole = roleMapper.selectById(role);
        String roleName = userRole.getRole();
        List<String> roleList = new ArrayList<>(Arrays.asList("user", "test", roleName));
        // List<String> roleList = new HashSet<>(Arrays.asList("user", "test", roleName));
        System.out.println(roleList);
        return new LoginUser(user, roleList);
        // return new LoginUser(user.getId(), user);

    }
}
