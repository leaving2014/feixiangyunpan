package com.fx.pan.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fx.pan.domain.*;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.common.Constants;
import com.fx.pan.dto.user.LoginUserBody;
import com.fx.pan.service.StorageService;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.BeanCopyUtils;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leaving
 * @version 1.0
 * @date 2022/1/12 17:00
 */

@Slf4j
@Tag(name = "user", description = "该接口为用户接口，主要做用户登录，注册,退出和获取用户信息等操作")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private StorageService storageService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Value("${fx.enableCaptcha}")
    String enableCaptcha;


    /**
     * @param user
     * @return
     */
    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user) {
        
        return userService.register(user);
    }


    /**
     * 登录
     *
     * @param loginUserBody
     * @return
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public ResponseResult login(@RequestBody LoginUserBody loginUserBody) {
        if (enableCaptcha.equals("1")) {
            String captcha = loginUserBody.getCaptcha();
            String t = loginUserBody.getTs();
            Object cacheObject = redisCache.getCacheObject(Constants.REDIS_DATA_SUFFIX + "-captcha:" + t);
            if (cacheObject == null) {
                return ResponseResult.error(500, "验证码已过期，请重新获取");
            } else {
                if (!captcha.equals(cacheObject.toString())) {
                    return ResponseResult.error(500, "验证码错误");
                } else {
                    redisCache.deleteObject(Constants.REDIS_DATA_SUFFIX + "-captcha:" + t);
                    User user = new User();
                    user.setUserName(loginUserBody.getUserName());
                    user.setPassword(loginUserBody.getPassword());
                    // user.setPassword(passwordEncoder.encode(loginUserBody.getPassword()));
                    System.out.println("登录user:" + user);
                    return userService.login(user.getUserName(), user.getPassword());
                }
            }
        } else {
            User user = new User();
            user.setUserName(loginUserBody.getUserName());
            user.setPassword(loginUserBody.getPassword());
            // user.setPassword(passwordEncoder.encode(loginUserBody.getPassword()));
            return userService.login(user.getUserName(), user.getPassword());
        }
    }

    /**
     * 退出登录
     *
     * @return
     */
    @ApiOperation(value = "用户退出")
    @PostMapping("/logout")
    public ResponseResult logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) auth.getPrincipal();
        if (auth != null) {
            //清除认证
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        Long id = loginUser.getUser().getId();
        // 删除redis中的值
        redisCache.deleteObject(Constants.REDIS_LOGIN_USER_PREFIX + id);
        return ResponseResult.success("注销成功");
    }

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    @PostMapping("/update")
    public ResponseResult updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("/userinfo")
    public ResponseResult userInfo(@RequestParam(required = false) Long userId) {
        Map map = new HashMap();
        if (userId == null) {
            userId = SecurityUtils.getUserId();
        }
        Object cacheObject = redisCache.getCacheObject(Constants.REDIS_LOGIN_USER_PREFIX + userId);
        if (cacheObject != null) {
            LoginUser loginUser = (LoginUser) cacheObject;
            map.put("userInfo", loginUser.getUser());
            return ResponseResult.success("成功", map);
        }
        User user = userService.selectUserById(userId);

        map.put("userInfo", JSONObject.toJSON(user));
        return ResponseResult.success("获取成功", map);
    }

    /**
     * 获取用户存储信息
     *
     * @return
     */
    @GetMapping("/storage")
    public ResponseResult userStorage() {
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
        Map map = new HashMap();
        map.put("userStorage", userStorage);
        return ResponseResult.success("获取成功", map);
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
    public ResponseResult updateSingerSong(HttpServletRequest request,
                                           @RequestParam("file") MultipartFile multipartFile){
        Long userId = SecurityUtils.getUserId();
        String filepath = userService.uploadAvatar(request, multipartFile);
        User user = userService.selectUserById(userId);
        user.setAvatar(filepath);
        ResponseResult msg = userService.updateUser(user);
        return msg;
    }


    // 用户管理

    /**
     * 查询用户列表
     *
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getUserList(@RequestParam(required = false) String query,
                                      @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        List<User> list = userService.getUserList(query, (int) pageNum, (int) pageSize);
        Map map = new HashMap();
        map.put("list", list);
        return ResponseResult.success("获取成功", map);
    }

    /**
     * 修改密码
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @ApiOperation(value = "修改密码")
    @PostMapping("/update/password")
    public ResponseResult modifyPassword(@RequestParam(required = true) String oldPassword,
                                         @RequestParam(required = true) String newPassword) {
        Long userId = SecurityUtils.getUserId();
        User user = userService.selectUserById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseResult.error(500, "原密码错误");
        }
        int i = userService.modifyPassword(user, oldPassword, newPassword);
        if (i == 1) {
            return ResponseResult.success("修改成功");
        } else {
            return ResponseResult.error(500, "修改失败");
        }
    }

    /**
     * 获取登录图片验证码
     * @param response
     * @param t
     * @throws IOException
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, @RequestParam("t") String t) throws IOException {
        //生成验证码图片
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 25);
        redisCache.set(Constants.REDIS_DATA_SUFFIX+"-captcha:"+t, circleCaptcha.getCode(), 60);
        //告诉浏览器输出内容为jpeg类型的图片
        response.setContentType("image/jpeg");
        //禁止浏览器缓存
        response.setHeader("Pragma", "No-cache");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            //图形验证码写出，可以写出到流，也可以写出到文件如circleCaptcha.write("d:/circle25.jpeg”);
            circleCaptcha.write(outputStream);
            //从带有圆圈类型的图形验证码图片中获取它的字符串验证码(获取字符串验证码要在图形验证码写出wirte后面才行，不然得到的值为null)
            String code = circleCaptcha.getCode();
            log.info("生成的验证码：{}", code);
            //关闭流
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
