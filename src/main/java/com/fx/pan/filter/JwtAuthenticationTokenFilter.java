package com.fx.pan.filter;

import com.alibaba.fastjson.JSON;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.service.TokenService;
import com.fx.pan.utils.JwtUtil;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.utils.SessionUtil;
import io.jsonwebtoken.Claims;
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
 * @Author leaving
 * @Date 2022/1/13 20:56
 * @Version 1.0
 */

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
        // Cookie[] cookies = request.getCookies();
        // // 没有cookie信息，则重定向到登录界面
        // if (null == cookies) {
        //     response.sendRedirect(request.getContextPath() + "/login");
        //     return;
        // }
        // // 定义cookie_username，用户的一些登录信息，例如：用户名，密码等
        // String cookie_username = null;
        // // 获取cookie里面的一些用户信息
        // for (Cookie item : cookies) {
        //     if ("cookie_username".equals(item.getName())) {
        //         cookie_username = item.getValue();
        //         break;
        //     }
        // }


        // 获取 token ( 前端，用户登录后，将 token 放到请求头当中。所以这里从请求头中获取 token )
        String token = tokenService.getToken(request);
        if (!StringUtils.hasText(token)) {
            // 如果请求头没有 token ，放行
            // System.out.println("请求头没有 token,放行");
            filterChain.doFilter(request, response);
            return;
        }

        // token 不为空，解析 token
        Long uesrId;
        try {
            System.out.println("开始解析token:" + token);
            Claims claims = JwtUtil.parseJWT(token);
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
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());//loginUser.getAuthorities()
                // new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());//loginUser.getAuthorities()
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //放行
        filterChain.doFilter(request, response);
    }


    private boolean checkJwtToken(HttpServletRequest request){

        return true;
    }


}
