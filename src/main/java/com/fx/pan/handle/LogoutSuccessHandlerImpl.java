package com.fx.pan.handle;

import com.fx.pan.common.Constants;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.service.TokenService;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author leaving
 * @date 2022/1/19 16:56
 * @version 1.0
 */

@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler
{
    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisCache redisCache;


    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtil.isNotNull(loginUser))
        {
            Long id = loginUser.getUser().getId();
            // 删除redis中的值
            redisCache.deleteObject(Constants.REDIS_LOGIN_USER_PREFIX+id);

        }
    }
}
