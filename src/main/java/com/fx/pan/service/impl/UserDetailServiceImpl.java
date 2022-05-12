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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author leaving
 * @date 2022/1/15 12:24
 * @version 1.0
 */

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;


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
        Integer role = user.getRole();
        Role userRole = roleMapper.selectById(role);
        if (userRole.getAvailable().equals("0")) {
            throw new RuntimeException("角色已被禁用");
        }
        String roleName = userRole.getRole();
        List<String> roleList = new ArrayList<>(Arrays.asList(roleName));
        // List<String> roleList = new HashSet<>(Arrays.asList("user", "test", roleName));
        System.out.println(roleList);
        return new LoginUser(user, roleList);
        // return new LoginUser(user.getId(), user);

    }
}
