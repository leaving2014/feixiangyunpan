package com.fx.pan.config;

import com.fx.pan.utils.StringUtil;
import com.fx.pan.utils.WgetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URI;

/**
 * @author leaving
 * @date 2022/5/7 22:01
 * @version 1.0
 */
@Slf4j
@ServerEndpoint("/progress")
@Component
public class ProgressServerEndpoint {

    @Autowired
    private WgetUtil wgetUtil;
    private String url = "https://mirrors.aliyun.com/ubuntu-releases/trusty/ubuntu-14.04.6-server-amd64.iso";
    private String option = "-c -O I:/ubuntu-14.04.6-server-amd64.iso";

    @OnOpen
    public void onOpen(Session session) throws Exception {
        URI requestURI = session.getRequestURI();
        log.info("WebSocket连接成功，请求地址：{}", requestURI);
        String fileName = StringUtil.getParamByUrl(requestURI.toString(), "fileName");
        String urlStr = StringUtil.getParamByUrl(requestURI.toString(), "urlStr");
        Long t = Long.valueOf(StringUtil.getParamByUrl(requestURI.toString(), "t"));
        Long userId = Long.valueOf(StringUtil.getParamByUrl(requestURI.toString(), "userId"));
        String type = StringUtil.getParamByUrl(requestURI.toString(), "type");
        String downloadPath = StringUtil.getParamByUrl(requestURI.toString(), "downloadPath");
        String fileSavePath = StringUtil.getParamByUrl(requestURI.toString(), "fileSavePath");
        log.info("sessionId：{}", session.getId());
        wgetUtil.wgetProgressRation(downloadPath, fileSavePath, fileName, urlStr, t, userId, type, progress -> sendText(session, progress));
    }

    private void sendText(Session session, WgetUtil.Progress progress) {
        try {
            session.getBasicRemote().sendText(progress.toJSONString());
        } catch (IOException e) {
            log.error("WebSocket发生消息失败！", e);
        }
    }

    @OnClose
    public void onClose() {
        log.info("WebSocket连接关闭");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.info("WebSocket错误", throwable);
    }
}
