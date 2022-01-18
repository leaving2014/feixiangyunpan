package com.fx.pan.filter;

import com.fx.pan.domain.LoginUser;
import com.fx.pan.utils.JwtUtil;
import com.fx.pan.utils.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @Author leaving
 * @Date 2022/1/13 20:56
 * @Version 1.0
 */

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取 token ( 前端，用户登录后，将 token 放到请求头当中。所以这里从请求头中获取 token )
        String token = request.getHeader("token");

        if (!StringUtils.hasText(token)) {
            // 如果请求头没有 token ，放行
            filterChain.doFilter(request, response);
            return;
        }

        // token 不为空，解析 token
        String uesrId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            uesrId = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("非法 token");
        }
        // 从 redis 中获取用户信息
        String redisKey = "fxpanlogin:" + uesrId;

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
