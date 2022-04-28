package com.fx.pan.service;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * @Author leaving
 * @Date 2022/1/14 10:47
 * @Version 1.0
 */

public interface UserService{

    Msg register(User user);

    Msg login(String username,String password);

    Msg logout();

    User seletUserWithUserName(String username);

    Msg updateUser(User user);

    User getUserBeanByToken(String token);

    User selectUserById(Serializable id);

    String uploadAvatar(HttpServletRequest request, MultipartFile multipartFile);

    List<User> getUserList(String query, int pageNum, int pageSize);

    int adminRegister(User user);
}
