package com.fx.pan.config;

// import com.fx.pan.handle.MyWebSocketHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author leaving
 * @Date 2022/3/28 15:22
 * @Version 1.0
 */

@Configuration
public class WebSocketConfig {
    // @Resource
    // private MyWebSocketHandler handler;

    /**
     * 注入ServerEndpointExporter，
     * 这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }


    // @Override
    // public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    //     registry.addHandler(handler, "/wsMy").addInterceptors(new HandshakeInterceptor() {
    //         @Override
    //         public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse serverHttpResponse,
    //         WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
    //             String jspCode = ((ServletServerHttpRequest) request).getServletRequest().getParameter("jspCode");
    //             if (jspCode != null) {
    //                 map.put("jspCode", jspCode);
    //             } else {
    //                 return false;
    //             }
    //
    //             return true;
    //         }
    //
    //         @Override
    //         public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse,
    //         WebSocketHandler webSocketHandler, Exception e) {
    //
    //         }
    //     });
    // }
}
