package com.fx.pan.filter;

import com.alibaba.fastjson.JSON;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.service.TokenService;
import com.fx.pan.utils.JwtUtil;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.utils.SessionUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static com.fx.pan.common.Constants.REDIS_LOGIN_USER_PREFIX;

/**
 * @author leaving
 * @date 2022/1/13 20:56
 * @version 1.0
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private RedisCache redisCache;

    @Resource
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 基于cookie验证
        // 获得cookie
        Cookie[] cookies = request.getCookies();

         // 没有cookie信息
        if (null == cookies) {
            filterChain.doFilter(request, response);
            return;
        }
        // // 获取cookie里面的一些用户信息
        String userToken = null;
        for (Cookie item : cookies) {
            if ("token".equals(item.getName())) {
                userToken = item.getValue();
            }
        }


        // 获取 token ( 前端，用户登录后，将 token 放到请求头当中。所以这里从请求头中获取 token )
        if (!StringUtils.hasText(userToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        // token 不为空，解析 token
        Long uesrId;
        try {

            Claims claims = JwtUtil.parseJWT(userToken);
            LoginUser user = JSON.parseObject(claims.getSubject(), LoginUser.class);
            uesrId = user.getUserId();
            SessionUtil.setSession(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("非法 token");
        }
        // 从 redis 中获取用户信息
        String redisKey = REDIS_LOGIN_USER_PREFIX + uesrId;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);

        if (Objects.isNull(loginUser)) {
            throw new RuntimeException("用户未登录");
        }

        // 将用户信息存入 SecurityContextHolder
        // 获取权限信息封装到 Authentication 中
        // 参数：用户信息、已认证状态、权限信息
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //放行
        filterChain.doFilter(request, response);
    }


}
