package com.fx.pan.admin.service;

import com.fx.pan.domain.User;

import java.util.List;

/**
 * @author leaving
 * @date 2022/4/8 21:48
 * @version 1.0
 */
public interface UserManageService {
    List<User> selectUsers(String query, Integer pageNumpn, Integer pageSize);

    Integer selectUsersTotal(String query);

    int changeStatus(Long id, Integer status);

    User selectUserById(Long id);

    int updateUser(User user);
}
