package com.fx.pan.config;

import com.alibaba.fastjson.JSON;
import com.fx.pan.domain.WsResultBean;
import com.fx.pan.utils.StringUtil;
import com.fx.pan.utils.office.MyStringUtil;
import com.fx.pan.utils.office.Pako_GzipUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author leaving
 * @date 2022/3/28 18:14
 * @version 1.0
 */
@Component
@ServerEndpoint(value = "/push/websocket", configurator = GetHttpSessionConfigurator.class)
@Slf4j
public class EditWebSocketServer {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    // private static int onlineCount = 0;
    private static AtomicInteger onlineCount = new AtomicInteger();

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<EditWebSocketServer> webSocketSet = new CopyOnWriteArraySet<EditWebSocketServer>();

    private static ConcurrentHashMap<String, EditWebSocketServer> tokenMap = new ConcurrentHashMap<>();
    private static CopyOnWriteArraySet<EditWebSocketServer> webSockets = new CopyOnWriteArraySet<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private String userId;

    //接收sid
    private String sid = "";

    private String userName;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        URI requestURI = session.getRequestURI();
        String uid = StringUtil.getParamByUrl(requestURI.toString(), "uid");
        String fid = StringUtil.getParamByUrl(requestURI.toString(), "fid");
        String userName = StringUtil.getParamByUrl(requestURI.toString(), "username");
        System.out.println("新用户协同====uid:" + uid + "     fid:" + fid + "    userName:" + userName);

        userId = uid;
        webSockets.add(this);
        if (tokenMap.get(userId) == null) {
            onlineCount.incrementAndGet();
            System.out.println("新用户连接，当前在线人数为：" + onlineCount);
        } else {
        }
        tokenMap.put(userId, this);
        this.session = session;
        log.info("{} 建立了连接！用户名=======", userName);



    /*
        this.session = session;
        this.userName = userName;
        webSocketSet.add(this);     //加入set中
        onlineCount.incrementAndGet();           //在线数加1
        log.info("有新窗口开始监听:"+userName+",当前在线人数为" + onlineCount+ "::::fid===="+fid);*/
        /*try {
            sendMessage(JSON.toJSONString(RestResponse.success()));
        } catch (IOException e) {
            log.error("websocket IO异常");
        }*/
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        onlineCount.incrementAndGet();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + onlineCount);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        if (message.equals("rub")) {//rub代表心跳包
            return;
        }

        for (String key : tokenMap.keySet()) {
            if (!key.equals(userId)) {
                EditWebSocketServer socketServer = (EditWebSocketServer) tokenMap.get(key);
                WsResultBean wsResultBean = null;
                wsResultBean = new WsResultBean();
                log.info("消息解压前：" + MyStringUtil.getStringShow(message));
                String contentReal = Pako_GzipUtils.unCompressToURI(message);
                log.info("消息解压后：" + MyStringUtil.getStringShow(contentReal));
                wsResultBean.setData(contentReal);
                wsResultBean.setStatus(0);
                wsResultBean.setUsername(userId);
                wsResultBean.setId(wsResultBean.getUsername());
                wsResultBean.setReturnMessage("success");
                wsResultBean.setCreateTime("");


                // DBObject bson = null;
                // try {
                //     bson = (DBObject) JSONParse.parse(wsResultBean.getData());
                // } catch (Exception ex) {
                //     return;
                // }
                // if (bson != null) {
                //     if (bson.get("t").equals("mv")) {
                //         //更新选区显示
                //         wsResultBean.setType(3);
                //     } else {
                //         //更新数据
                //         wsResultBean.setType(2);
                //     }
                // }
                socketServer.sendMessage(wsResultBean, socketServer.session);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EditWebSocketServer that = (EditWebSocketServer) o;
        return Objects.equals(session, that.session);
    }

    /**
     * 服务端发送消息给客户端
     */
    private void sendMessage(WsResultBean wsResultBean, Session toSession) {
        try {
            log.info("服务端给客户端[{}]发送消息", toSession.getId());
            toSession.getBasicRemote().sendText(JSON.toJSONString(wsResultBean));
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败：{}", e);
        }
    }

    // 此为广播消息
    public void sendAllMessage(String message) {
        for (EditWebSocketServer webSocket : webSockets) {
            try {
                webSocket.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 全部踢下线
     */
    public void clear() {
        for (String key : tokenMap.keySet()) {
            EditWebSocketServer socketServer = (EditWebSocketServer) tokenMap.get(key);
            try {
                socketServer.session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("全部断开！剩余{}个", onlineCount.get());

    }


    @Override
    public int hashCode() {
        return Objects.hash(session);
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message) throws IOException {

        for (EditWebSocketServer item : webSocketSet) {
            try {
                //这里可以设定只推送给这个sid的，为null则全部推送
//                if(sid==null) {

                item.sendMessage(message);
                log.info("推送消息到窗口" + item.sid + "，推送内容:" + message);
//                }else if(item.sid.equals(sid)){
//                    item.sendMessage(message);
//                }
            } catch (IOException e) {
                continue;
            }
        }
    }
    //
    // public static synchronized int getOnlineCount() {
    //     return onlineCount;
    // }
    //
    // public static synchronized void addOnlineCount() {
    //     EditWebSocketServer.onlineCount++;
    // }
    //
    // public static synchronized void subOnlineCount() {
    //     EditWebSocketServer.onlineCount--;
    // }


}
