package com.fx.pan.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author leaving
 * @Date 2022/2/6 17:56
 * @Version 1.0
 */

public class SessionUtil {

    private static ThreadLocal<Map<String, Object>> resource = new InheritableThreadLocal<>();
    public static void setSession(Object o) {
        Map<String, Object> map = new HashMap<>();
        map.put("session", o);
        resource.set(map);
    }
    public static Object getSession(){
        Map<String, Object> map = resource.get();
        if (map == null) {
            return null;
        }
        return map.get("session");
    }
}
