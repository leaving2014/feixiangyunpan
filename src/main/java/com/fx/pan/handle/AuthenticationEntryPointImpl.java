package com.fx.pan.handle;

import com.alibaba.fastjson.JSON;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.common.AppHttpCodeEnum;
import com.fx.pan.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author leaving
 * @date 2022/1/21 14:40
 * @version 1.0
 */
@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseResult msg = null;
        if (authException instanceof BadCredentialsException) {
            msg = ResponseResult.error(AppHttpCodeEnum.LOGIN_ERROR.getCode(), authException.getMessage());
        } else if (authException instanceof InsufficientAuthenticationException) {
            msg = ResponseResult.error(AppHttpCodeEnum.NEED_LOGIN.getCode(), AppHttpCodeEnum.NEED_LOGIN.getMsg());

        } else {
            msg = ResponseResult.error(AppHttpCodeEnum.SYSTEM_ERROR.getCode(), "认证或授权失败");
        }
        // 响应给前端
        WebUtil.renderString(response, JSON.toJSONString(msg));
    }
}
