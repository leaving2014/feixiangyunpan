package com.fx.pan.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.common.Constants;
import com.fx.pan.common.Msg;
import com.fx.pan.component.UserDealComp;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.domain.User;
import com.fx.pan.mapper.UserMapper;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.JwtUtil;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.utils.SysUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author leaving
 * @Date 2022/1/14 10:48
 * @Version 1.0
 */

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserDealComp userDealComp;


    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public Msg register(User user) {
        String msg = "";
        int code = 500;
        boolean userFlag = userDealComp.isUserNameExist(user);
        if (userFlag) {
            msg = "注册用户失败，用户名" + user.getUserName() + "已存在";
        } else {
            boolean flag = userMapper.insertUser(user) > 0;
            if (flag) {
                code = 0;
                msg = "注册成功";
            } else {
                msg = "注册失败";
            }
        }
        return Msg.msg(code, msg).put("ts", SysUtil.getTimeStamp());
    }


    /**
     * 用户登录
     * @param username,password
     * @return
     */
    @Override
    public Msg login(String username,String password) {
        // AuthenticationManager authenticate进行用户认证

        Authentication authenticationToken =
                new UsernamePasswordAuthenticationToken(username,password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 如果认证没通过
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登录失败");
        }

        //如果认证通过
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);

        redisCache.setCacheObject(Constants.REDIS_LOGIN_USER_PREFIX + userId, loginUser);
        return  Msg.success("登录成功").put("token", jwt).put("ts", SysUtil.getTimeStamp());
    }

    /**
     * 退出登录
     * @return
     */
    @Override
    public Msg logout() {
        // 获取securityContextHolder中的用户id
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        Long id = loginUser.getUser().getId();
        // 删除redis中的值
        redisCache.deleteObject(Constants.REDIS_LOGIN_USER_PREFIX+id);
        return  Msg.success("注销成功");
    }


    @Override
    public User seletUserWithUserName(String username) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserName, username);
        return userMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @Override
    public Msg updateUser(User user) {

        int flag = userMapper.updateById(user);
        if (flag>0){
            return Msg.success("修改成功");
        }else{
            return Msg.error(500,"修改失败");
        }

    }

    @Override
    public User getUserBeanByToken(String token) {
        Claims c = null;
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        System.out.println("getUserBeanByToken:"+token);
        //        if (!token.startsWith("Bearer ")) {
//            throw new NotLoginException("token格式错误");
//        }
        token = token.replace("Bearer ", "");
        System.out.println(token);
        try {
            c = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            log.info("解码异常:" + e);
            return null;
        }
        if (c == null) {
            log.info("解码为空");
            return null;
        }
        String subject = c.getSubject();
        log.debug("解析结果：" + subject);
        User tokenUser = JSON.parseObject(subject, User.class);

        User saveUser = new User();
        String tokenPassword = "";
        String savePassword = "";
        if (StringUtils.isNotEmpty(tokenUser.getPassword())) {
            saveUser = seletUserWithUserName(tokenUser.getUserName());
            if (saveUser == null) {
                return null;
            }
            tokenPassword = tokenUser.getPassword();
            savePassword = saveUser.getPassword();
        }
        if (StringUtils.isEmpty(tokenPassword) || StringUtils.isEmpty(savePassword)) {
            return null;
        }
        if (tokenPassword.equals(savePassword)) {

            return saveUser;
        } else {
            return null;
        }
    }

}
