package com.admin.system.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author Admin
 */
@Component
public class JwtUtil {

    /**
     * 生成Token
     */
    public static String generateToken(String subject, String secret, long expireTime) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(subject, claims, secret, expireTime);
    }

    /**
     * 生成Token（带声明）
     */
    public static String generateToken(String subject, Map<String, Object> claims, String secret, long expireTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireTime * 60 * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从Token中获取用户名
     */
    public static String getUsernameFromToken(String token, String secret) {
        try {
            Claims claims = parseToken(token, secret);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从Token中获取声明
     */
    public static Claims parseToken(String token, String secret) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证Token是否过期
     */
    public static boolean isTokenExpired(String token, String secret) {
        try {
            Claims claims = parseToken(token, secret);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证Token
     */
    public static boolean validateToken(String token, String username, String secret) {
        String tokenUsername = getUsernameFromToken(token, secret);
        return (username.equals(tokenUsername) && !isTokenExpired(token, secret));
    }

}
