package com.admin.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类（jjwt 0.12.x API）
 *
 * <p><b>架构说明（重要）：</b>本系统当前把 JWT 仅当作「登录时签发、之后作为 Redis 会话键」的
 * <b>不透明令牌</b>使用——{@code JwtAuthenticationFilter} 在每次请求时<b>不验签、不解析 claims</b>，
 * 而是直接拿 token 串去 Redis 查 {@code LoginUser}。即认证状态实为<b>有状态会话</b>，签名不参与运行期校验。
 * 详见 {@code docs/adr/0001-token-as-opaque-session.md}。</p>
 *
 * @author Admin
 */
@Component
public class JwtUtil {

    /**
     * 由密钥字符串构建 HMAC-SHA 密钥。
     * 注意：jjwt 0.12 要求密钥长度满足算法强度（HS512 需 ≥64 字节），否则抛 WeakKeyException。
     * 算法由密钥长度自动推断（64 字节密钥 → HS512）。
     */
    private static SecretKey buildKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成Token
     */
    public static String generateToken(String subject, String secret, long expireTime) {
        return generateToken(subject, new HashMap<>(), secret, expireTime);
    }

    /**
     * 生成Token（带声明）
     */
    public static String generateToken(String subject, Map<String, Object> claims, String secret, long expireTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireTime * 60 * 1000);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(buildKey(secret))
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
                .verifyWith(buildKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
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
