package com.fx.pan.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author leaving
 * @Date 2022/1/11 22:43
 * @Version 1.0
 */

public class WebUtils {
    /**
     * 将字符串渲染到客户端
     * @param response 渲染对象
     * @param string 待渲染的字符串
     */
    public static String renderString(HttpServletResponse response,String string){
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        try {
            response.getWriter().print(string);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
