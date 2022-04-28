package com.fx.pan.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fx.pan.common.Constants;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.Storage;
import com.fx.pan.domain.User;
import com.fx.pan.factory.fxUtils;
import com.fx.pan.service.StorageService;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.PathUtils;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author leaving
 * @Date 2022/1/12 17:00
 * @Version 1.0
 */

@Tag(name = "user", description = "该接口为用户接口，主要做用户登录，注册,退出和校验token")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Autowired
    private StorageService storageService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private PasswordEncoder passwordEncoder;


    /**
     * @param user
     * @return
     */
    @PostMapping("/register")
    public Msg register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.register(user);
    }


    /**
     * 登录
     *
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Msg login(@RequestBody User user) {
        return userService.login(user.getUserName(),user.getPassword());
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @PostMapping("/update")
    public Msg updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }


    @PostMapping("/query")
    public Msg login(@RequestParam String username) {
        User user = userService.seletUserWithUserName(username);
        return Msg.success("成功").put("res", user);
    }


    /**
     * 退出登录
     *
     * @return
     */
    @PostMapping("/logout")
    public Msg logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) auth.getPrincipal();
        if (auth != null) {//清除认证
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        Long id = loginUser.getUser().getId();
        // 删除redis中的值
        redisCache.deleteObject(Constants.REDIS_LOGIN_USER_PREFIX + id);
        return Msg.success("注销成功");
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("/userinfo")
    public Msg userInfo(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            userId = SecurityUtils.getUserId();
        }
        User user = userService.selectUserById(userId);
        QueryWrapper<Storage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return Msg.success("获取成功").put("userInfo", user);
    }

    @GetMapping("/shareuser")
    public Msg shareUserInfo(@RequestParam(required = true) Long userId) {
        if (userId == null) {
            userId = SecurityUtils.getUserId();
        }
        User user = userService.selectUserById(userId);
        QueryWrapper<Storage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return Msg.success("获取成功").put("userInfo", user);
    }

    /**
     * 获取用户存储信息
     *
     * @return
     */
    @GetMapping("/storage")
    public Msg userStorage() {
        Long userId = SecurityUtils.getUserId();
        QueryWrapper<Storage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Storage userStorage = storageService.getUserStorage(userId);
        if (userStorage == null) {
            userStorage = new Storage();
            userStorage.setUserId(userId);
            storageService.insertUserStorage(userStorage);
            userStorage = storageService.getUserStorage(userId);
        }
        return Msg.success("获取成功").put("userStorage", userStorage);
    }

    /**
     * 更新用户头像
     *
     * @param request
     * @param multipartFile
     * @return
     * @throws IOException
     * @throws ServletException
     */
    @PostMapping("/upload/avatar")
    public Msg updateSingerSong(HttpServletRequest request,
                                @RequestParam("file") MultipartFile multipartFile) throws IOException,
            ServletException {
        Long userId = SecurityUtils.getUserId();
        String fileName = multipartFile.getOriginalFilename();
        String filepath = userService.uploadAvatar(request, multipartFile);
        User user = userService.selectUserById(userId);
        user.setAvatar(filepath);
        Msg msg = userService.updateUser(user);
        return msg;
    }

    @PostMapping("/token")
    public Msg token() {

        return null;
    }

    // 用户管理

    /**
     * 查询用户列表
     *
     * @return
     */
    @GetMapping("/list")
    public Msg getUserList(@RequestParam(required = false) String query,
                           @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                           @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        List<User> list = userService.getUserList(query, (int) pageNum, (int) pageSize);
        return Msg.success("获取成功").put("list", list);
    }


}
