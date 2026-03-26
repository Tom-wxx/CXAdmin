package com.admin.system.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试
 */
@DisplayName("JwtUtil 工具类测试")
class JwtUtilTest {

    private static final String SECRET = "testSecretKeyForJwtTokenGenerationAndValidation123456";
    private static final long EXPIRE_TIME = 30; // 30 minutes

    @BeforeEach
    void setUp() {
        // No setup needed for static utility class
    }

    // ==================== generateToken Tests ====================

    @Test
    @DisplayName("生成Token - 基本生成成功")
    void generateToken_shouldReturnNonNullToken() {
        String token = JwtUtil.generateToken("admin", SECRET, EXPIRE_TIME);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("生成Token - 不同用户生成不同Token")
    void generateToken_differentUsersShouldProduceDifferentTokens() {
        String token1 = JwtUtil.generateToken("user1", SECRET, EXPIRE_TIME);
        String token2 = JwtUtil.generateToken("user2", SECRET, EXPIRE_TIME);

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("生成Token - 带自定义声明")
    void generateToken_withClaims_shouldIncludeClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("role", "admin");

        String token = JwtUtil.generateToken("admin", claims, SECRET, EXPIRE_TIME);

        assertNotNull(token);
        Claims parsed = JwtUtil.parseToken(token, SECRET);
        assertEquals("admin", parsed.getSubject());
    }

    // ==================== getUsernameFromToken Tests ====================

    @Test
    @DisplayName("从Token获取用户名 - 正常获取")
    void getUsernameFromToken_shouldReturnCorrectUsername() {
        String token = JwtUtil.generateToken("testUser", SECRET, EXPIRE_TIME);

        String username = JwtUtil.getUsernameFromToken(token, SECRET);

        assertEquals("testUser", username);
    }

    @Test
    @DisplayName("从Token获取用户名 - 无效Token返回null")
    void getUsernameFromToken_invalidToken_shouldReturnNull() {
        String username = JwtUtil.getUsernameFromToken("invalid.token.here", SECRET);

        assertNull(username);
    }

    @Test
    @DisplayName("从Token获取用户名 - 错误密钥返回null")
    void getUsernameFromToken_wrongSecret_shouldReturnNull() {
        String token = JwtUtil.generateToken("admin", SECRET, EXPIRE_TIME);

        String username = JwtUtil.getUsernameFromToken(token, "wrongSecretKey12345678901234567890");

        assertNull(username);
    }

    @Test
    @DisplayName("从Token获取用户名 - 空Token返回null")
    void getUsernameFromToken_emptyToken_shouldReturnNull() {
        String username = JwtUtil.getUsernameFromToken("", SECRET);

        assertNull(username);
    }

    // ==================== parseToken Tests ====================

    @Test
    @DisplayName("解析Token - 正常解析")
    void parseToken_shouldReturnValidClaims() {
        String token = JwtUtil.generateToken("admin", SECRET, EXPIRE_TIME);

        Claims claims = JwtUtil.parseToken(token, SECRET);

        assertNotNull(claims);
        assertEquals("admin", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    @DisplayName("解析Token - 带自定义声明的解析")
    void parseToken_withCustomClaims_shouldReturnAllClaims() {
        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("userId", 100);
        customClaims.put("role", "manager");

        String token = JwtUtil.generateToken("manager", customClaims, SECRET, EXPIRE_TIME);
        Claims claims = JwtUtil.parseToken(token, SECRET);

        assertEquals("manager", claims.getSubject());
        assertEquals(100, claims.get("userId"));
        assertEquals("manager", claims.get("role"));
    }

    @Test
    @DisplayName("解析Token - 无效Token应抛异常")
    void parseToken_invalidToken_shouldThrowException() {
        assertThrows(Exception.class, () -> {
            JwtUtil.parseToken("not.a.valid.token", SECRET);
        });
    }

    // ==================== isTokenExpired Tests ====================

    @Test
    @DisplayName("Token过期检查 - 有效Token未过期")
    void isTokenExpired_validToken_shouldReturnFalse() {
        String token = JwtUtil.generateToken("admin", SECRET, EXPIRE_TIME);

        boolean expired = JwtUtil.isTokenExpired(token, SECRET);

        assertFalse(expired);
    }

    @Test
    @DisplayName("Token过期检查 - 无效Token视为过期")
    void isTokenExpired_invalidToken_shouldReturnTrue() {
        boolean expired = JwtUtil.isTokenExpired("invalid.token", SECRET);

        assertTrue(expired);
    }

    @Test
    @DisplayName("Token过期检查 - 错误密钥视为过期")
    void isTokenExpired_wrongSecret_shouldReturnTrue() {
        String token = JwtUtil.generateToken("admin", SECRET, EXPIRE_TIME);

        boolean expired = JwtUtil.isTokenExpired(token, "wrongSecretKey12345678901234567890");

        assertTrue(expired);
    }

    // ==================== validateToken Tests ====================

    @Test
    @DisplayName("验证Token - 有效Token验证通过")
    void validateToken_validToken_shouldReturnTrue() {
        String token = JwtUtil.generateToken("admin", SECRET, EXPIRE_TIME);

        boolean valid = JwtUtil.validateToken(token, "admin", SECRET);

        assertTrue(valid);
    }

    @Test
    @DisplayName("验证Token - 用户名不匹配返回false")
    void validateToken_wrongUsername_shouldReturnFalse() {
        String token = JwtUtil.generateToken("admin", SECRET, EXPIRE_TIME);

        boolean valid = JwtUtil.validateToken(token, "otherUser", SECRET);

        assertFalse(valid);
    }

    @Test
    @DisplayName("验证Token - 无效Token返回false")
    void validateToken_invalidToken_shouldReturnFalse() {
        // getUsernameFromToken returns null for invalid token
        // "admin".equals(null) returns false, so validateToken returns false
        boolean valid = JwtUtil.validateToken("invalid.token", "admin", SECRET);

        assertFalse(valid);
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Token的过期时间正确设置")
    void generateToken_expirationShouldBeSet() {
        long expireMinutes = 60;
        long beforeGeneration = System.currentTimeMillis();

        String token = JwtUtil.generateToken("admin", SECRET, expireMinutes);

        Claims claims = JwtUtil.parseToken(token, SECRET);
        long expirationTime = claims.getExpiration().getTime();
        long expectedMinExpire = beforeGeneration + expireMinutes * 60 * 1000 - 5000; // 5s tolerance
        long expectedMaxExpire = beforeGeneration + expireMinutes * 60 * 1000 + 5000;

        assertTrue(expirationTime >= expectedMinExpire && expirationTime <= expectedMaxExpire,
                "Token expiration should be approximately " + expireMinutes + " minutes from now");
    }

    @Test
    @DisplayName("生成Token - 特殊字符用户名")
    void generateToken_specialCharacters_shouldWork() {
        String specialUsername = "user@domain.com";
        String token = JwtUtil.generateToken(specialUsername, SECRET, EXPIRE_TIME);

        String extracted = JwtUtil.getUsernameFromToken(token, SECRET);
        assertEquals(specialUsername, extracted);
    }

    @Test
    @DisplayName("生成Token - 中文用户名")
    void generateToken_chineseUsername_shouldWork() {
        String chineseUsername = "管理员";
        String token = JwtUtil.generateToken(chineseUsername, SECRET, EXPIRE_TIME);

        String extracted = JwtUtil.getUsernameFromToken(token, SECRET);
        assertEquals(chineseUsername, extracted);
    }
}
