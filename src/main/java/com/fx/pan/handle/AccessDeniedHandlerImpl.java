package com.fx.pan.handle;

import com.alibaba.fastjson.JSON;
import com.fx.pan.common.HttpStatus;
import com.fx.pan.common.Msg;
import com.fx.pan.utils.WebUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author leaving
 * @Date 2022/1/21 14:46
 * @Version 1.0
 */

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Msg msg = new Msg(HttpStatus.FORBIDDEN,"您没有权限访问");
        String json = JSON.toJSONString(msg);
        WebUtils.renderString(response, json);
    }
}
