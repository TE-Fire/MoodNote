package com.moodnote.common.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;
  
/**
 * JWT工具类
 */
@Component
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成token
     * @param username
     * @param userId
     * @return
     */
    public String generateToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        return createToken(claims);
    }

    
    /**
     * 创建token
     * @param claims
     * @return
     */
    private String createToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now) // 签发时间
                .expiration(expiryDate) // 过期时间
                .signWith(secretKey) // 签名
                .compact();
    }

    /**
     * 提取过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    /**
     * 提取用户名
     * @param token
     * @return
     */
    public String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * 提取用户id
     * @param token
     * @return
     */
    public Long extractUserId(String token) {
        Claims claims = extractClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 校验token是否有效
     * @param token
     * @return
     */
    public Boolean validateToken(String token) {
        return !isTokenExpired(token); // 校验token是否过期
    }

    /**
     * 提取token中的claims
     * @param token
     * @return
     */
    private Claims extractClaims(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser()
                .verifyWith(secretKey) // 校验签名
                .build() // 构建解析器
                .parseSignedClaims(token) // 解析token
                .getPayload(); // 获取payload
    }


    /**
     * 校验token是否过期
     * @param token
     * @return
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // 过期时间是否在当前时间之前, true 表示过期
    }
}
