package com.fx.pan.config.redis;

import com.fx.pan.service.RecycleService;
import com.fx.pan.service.ShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @author leaving
 * @date 2022/5/9 15:29
 * @version 1.0
 */

@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Autowired
    private ShareService shareService;

    @Autowired
    private RecycleService recycleService;


    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString(); // 获取过期的key
        log.info("redis key过期：{}", expiredKey);
        Long id = null;
        Long userId = null;
        if (expiredKey.contains("delete-share") || expiredKey.contains("delete-file")) {
            id = Long.valueOf(expiredKey.substring(expiredKey.lastIndexOf(":")+1));
            userId = Long.valueOf(expiredKey.substring(expiredKey.lastIndexOf("-")+1,expiredKey.lastIndexOf(":")));
        }
        if (expiredKey.contains("delete-share")) { // 判断是否是想要监听的过期key
            shareService.deleteShare(id,userId);
        } else if (expiredKey.contains("delete-file")) {
            recycleService.deleteRecycleFileById(id,userId);
        }

    }
}
