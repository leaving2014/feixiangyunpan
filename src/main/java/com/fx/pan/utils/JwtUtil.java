package com.fx.pan.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 * @author leaving
 * @date 2022/1/12 11:32
 * @version 1.0
 */

public class JwtUtil {

    // jti：jwt的唯一身份标识
    public static final String JWT_ID = UUID.randomUUID().toString();


    /**
     * 有效期为
     * 60 * 60 *1000  一个小时
     */
    public static final Long JWT_TTL = 3 * 60 * 60 * 1000L;

    public static final Long EXPIRE_TIME = 60 * 60 * 1000L * 24 * 7; // 7天
    /**
     * 设置秘钥明文
     */
    public static final String JWT_KEY = "fxclouddisc";

    public static String getUUID() {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token;
    }

    /**
     * 生成 jtw
     *
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public static String createJWT(String subject) {
        // 设置过期时间 空
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }


    /**
     * 生成 jwt
     *
     * @param subject   token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return
     */
    public static String createJWT(String subject, Long ttlMillis) {
        // 设置过期时间
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (ttlMillis == null) {
            ttlMillis = JwtUtil.EXPIRE_TIME;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                // 唯一的ID
                .setId(uuid)
                // 主题  可以是JSON数据
                .setSubject(subject)
                // 签发者
                .setIssuer("fxpan")
                // 签发时间
                .setIssuedAt(now)
                // 使用 HS256 对称加密算法签名, 第二个参数为秘钥
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(expDate);
    }

    /**
     * 创建 token
     *
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        // 设置过期时间
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id);
        return builder.compact();
    }

    public static void main(String[] args) throws Exception {
        /**
         * 生成 JWT
         */
        // String jwt = createJWT("1");
        // System.out.println(jwt);

        /**
         * 将生成的 JWT 还原为 之前的明文 2123，时间过期会报错，须重新生成再解析
         */
        Claims claims = parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ZWI0NzRjYmU1ODE0ZGYxYjZhODZjNWVhOTQxNzEzMCIsInN1YiI6IjEiLCJpc3MiOiJmeHBhbiIsImlhdCI6MTY0MzA4MDk2OSwiZXhwIjoxNjQzMTY3MzY5fQ.-0E7uwuMVRNroGDtA83RlPVRQlgd3ooSJcHEQxD5ULg");
        // Claims claims = parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJxaXdlbi1jbXMiLCJleHAiOjE2NDM2ODU4NzMsInN1YiI6IntcInVzZXJJZFwiOjE4MjN9IiwiYXVkIjoicWl3ZW5zaGFyZSIsImlhdCI6MTY0MzA4MTA3M30.5R8PaVlj3g8onR-m67Slkmz6jQIKuQpnim0tcNFyTNE");
        System.out.println(claims);
        String subject = claims.getSubject();
        System.out.println(subject);
    }

    /**
     * 生成加密后的秘钥 secretKey
     *
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 解析
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }




}
