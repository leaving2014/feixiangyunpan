package com.fx.pan.service;

import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * @author leaving
 * @date 2022/1/14 10:47
 * @version 1.0
 */

public interface UserService{

    ResponseResult register(User user);

    ResponseResult login(String username, String password);

    ResponseResult logout();

    User seletUserWithUserName(String username);

    ResponseResult updateUser(User user);

    User getUserBeanByToken(String token);

    User selectUserById(Serializable id);

    String uploadAvatar(HttpServletRequest request, MultipartFile multipartFile);

    List<User> getUserList(String query, int pageNum, int pageSize);

    int adminRegister(User user);

    int modifyPassword(User user, String oldPassword, String newPassword);
}
