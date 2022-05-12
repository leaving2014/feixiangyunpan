package com.fx.pan.controller;

import com.fx.pan.config.EditWebSocketServer;
import com.fx.pan.domain.Message;
import com.fx.pan.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;

/**
 * @author leaving
 * @date 2022/3/28 18:01
 * @version 1.0
 */
@RestController
public class WebSocketController {

    @Resource
    private RedisCache redisCache;
    // @Resource
    // private WebSocket webSocket;

    // @Autowired
    // private AmqpTemplate amqpTemplate;

    @Resource
    private EditWebSocketServer webSocketServer;

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
            EditWebSocketServer.sendInfo(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    // @PostMapping("/sendAllWebSocket")
    // public String test() {
    //     String text="你们好！这是websocket群体发送！";
    //     try {
    //         // EditWebSocketServer.sendInfo(text);
    //     }catch (IOException e){
    //         e.printStackTrace();
    //     }
    //     return text;
    // }
    //
    // @GetMapping("/cloud/progress")
    // public ResponseResult send(){
    //     try {
    //         // EditWebSocketServer.sendInfo("111111122222");
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    //     // webSocket.sendMessage("这是websocket群发消息！");
    //     return ResponseResult.success("发送消息成功");
    // }


}
