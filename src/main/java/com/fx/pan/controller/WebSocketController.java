package com.fx.pan.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fx.pan.common.Msg;
import com.fx.pan.config.WebSocketServer;
import com.fx.pan.domain.Message;
import com.fx.pan.domain.WorkBookEntity;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.utils.office.SheetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Author leaving
 * @Date 2022/3/28 18:01
 * @Version 1.0
 */
@RestController
public class WebSocketController {

    @Autowired
    private RedisCache redisCache;
    @Autowired
    // private WebSocket webSocket;

    // @Autowired
    // private AmqpTemplate amqpTemplate;

    @Resource
    private com.fx.pan.config.WebSocketServer webSocketServer;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Message greeting(String content, Principal pl) throws Exception {
        Message message = new Message();
        message.setContent(content.substring(1, content.length() - 1));
        message.setData(new Date().toString());
        message.setName(pl.getName());
        return message;
    }

    @PostMapping("/sendAllWebSocket")
    public String test() {
        String text = "你们好！这是websocket群体发送！";
        try {
            WebSocketServer.sendInfo(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    // @PostMapping("/sendAllWebSocket")
    // public String test() {
    //     String text="你们好！这是websocket群体发送！";
    //     try {
    //         // WebSocketServer.sendInfo(text);
    //     }catch (IOException e){
    //         e.printStackTrace();
    //     }
    //     return text;
    // }
    //
    // @GetMapping("/cloud/progress")
    // public Msg send(){
    //     try {
    //         // WebSocketServer.sendInfo("111111122222");
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    //     // webSocket.sendMessage("这是websocket群发消息！");
    //     return Msg.success("发送消息成功");
    // }


}
