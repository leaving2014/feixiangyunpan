package com.fx.pan.handle;

import com.alibaba.fastjson.JSON;
import com.fx.pan.common.HttpStatus;
import com.fx.pan.common.Msg;
import com.fx.pan.utils.WebUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author leaving
 * @Date 2022/1/21 14:40
 * @Version 1.0
 */

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    //认证失败处理
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 处理异常
        Msg msg = new Msg(HttpStatus.UNAUTHORIZED,"用户名或密码错误,请重新登录");
        String json = JSON.toJSONString(msg);
        WebUtils.renderString(response, json);
    }
}
