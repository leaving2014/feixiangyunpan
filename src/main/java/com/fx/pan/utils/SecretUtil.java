package com.fx.pan.utils;

import java.util.Base64;

/**
 * @Author leaving
 * @Date 2022/3/23 22:28
 * @Version 1.0
 */

public class SecretUtil {

    /**
     * Base64
     */
    public static String base64(String str) {
        byte[] bytes = str.getBytes();
        //Base64 加密
        String encoded = Base64.getEncoder().encodeToString(bytes);
        System.out.println("Base 64 加密后：" + encoded);
        return encoded;

    }

    /**
     * base64解密
     * @param str
     * @return
     */
    public static String decodeBase64(String str) {
        byte[] decoded = Base64.getDecoder().decode(str);
        String decodeStr = new String(decoded);
        return decodeStr;
    }
}
