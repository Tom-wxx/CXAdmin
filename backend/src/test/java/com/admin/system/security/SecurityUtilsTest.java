package com.admin.system.security;

import com.admin.system.entity.SysUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SecurityUtils 单元测试
 */
@DisplayName("SecurityUtils 工具类测试")
class SecurityUtilsTest {

    private LoginUser loginUser;
    private SysUser sysUser;

    @BeforeEach
    void setUp() {
        sysUser = new SysUser();
        sysUser.setUserId(1L);
        sysUser.setUsername("admin");
        sysUser.setDeptId(100L);
        sysUser.setStatus("0");

        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        permissions.add("system:user:add");

        loginUser = new LoginUser(sysUser, permissions);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ==================== getLoginUser Tests ====================

    @Test
    @DisplayName("获取登录用户 - SecurityContext中有认证信息")
    void getLoginUser_whenAuthenticated_shouldReturnLoginUser() {
        setAuthentication(loginUser);

        LoginUser result = SecurityUtils.getLoginUser();

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals(1L, result.getUserId());
    }

    @Test
    @DisplayName("获取登录用户 - SecurityContext为空返回null")
    void getLoginUser_whenNoAuthentication_shouldReturnNull() {
        SecurityContextHolder.clearContext();

        LoginUser result = SecurityUtils.getLoginUser();

        assertNull(result);
    }

    // ==================== getUsername Tests ====================

    @Test
    @DisplayName("获取用户名 - 已认证用户")
    void getUsername_whenAuthenticated_shouldReturnUsername() {
        setAuthentication(loginUser);

        String username = SecurityUtils.getUsername();

        assertEquals("admin", username);
    }

    @Test
    @DisplayName("获取用户名 - 未认证返回系统")
    void getUsername_whenNoAuthentication_shouldReturnSystem() {
        SecurityContextHolder.clearContext();

        String username = SecurityUtils.getUsername();

        assertEquals("系统", username);
    }

    // ==================== getUserId Tests ====================

    @Test
    @DisplayName("获取用户ID - 已认证用户")
    void getUserId_whenAuthenticated_shouldReturnUserId() {
        setAuthentication(loginUser);

        Long userId = SecurityUtils.getUserId();

        assertEquals(1L, userId);
    }

    @Test
    @DisplayName("获取用户ID - 未认证返回null")
    void getUserId_whenNoAuthentication_shouldReturnNull() {
        SecurityContextHolder.clearContext();

        Long userId = SecurityUtils.getUserId();

        assertNull(userId);
    }

    // ==================== encryptPassword Tests ====================

    @Test
    @DisplayName("密码加密 - 生成BCrypt密文")
    void encryptPassword_shouldReturnBCryptHash() {
        String rawPassword = "admin123";

        String encrypted = SecurityUtils.encryptPassword(rawPassword);

        assertNotNull(encrypted);
        assertTrue(encrypted.startsWith("$2a$"));
    }

    @Test
    @DisplayName("密码加密 - 同一密码每次生成不同密文")
    void encryptPassword_samePlaintext_shouldProduceDifferentHashes() {
        String rawPassword = "testPassword";

        String encrypted1 = SecurityUtils.encryptPassword(rawPassword);
        String encrypted2 = SecurityUtils.encryptPassword(rawPassword);

        assertNotEquals(encrypted1, encrypted2, "BCrypt should produce different hashes each time due to salt");
    }

    @Test
    @DisplayName("密码加密 - 空密码也能加密")
    void encryptPassword_emptyString_shouldWork() {
        String encrypted = SecurityUtils.encryptPassword("");

        assertNotNull(encrypted);
        assertTrue(encrypted.startsWith("$2a$"));
    }

    // ==================== matchesPassword Tests ====================

    @Test
    @DisplayName("密码匹配 - 正确密码匹配成功")
    void matchesPassword_correctPassword_shouldReturnTrue() {
        String rawPassword = "admin123";
        String encoded = SecurityUtils.encryptPassword(rawPassword);

        boolean matches = SecurityUtils.matchesPassword(rawPassword, encoded);

        assertTrue(matches);
    }

    @Test
    @DisplayName("密码匹配 - 错误密码匹配失败")
    void matchesPassword_wrongPassword_shouldReturnFalse() {
        String encoded = SecurityUtils.encryptPassword("admin123");

        boolean matches = SecurityUtils.matchesPassword("wrongPassword", encoded);

        assertFalse(matches);
    }

    @Test
    @DisplayName("密码匹配 - 大小写敏感")
    void matchesPassword_caseSensitive_shouldReturnFalse() {
        String encoded = SecurityUtils.encryptPassword("Admin123");

        boolean matches = SecurityUtils.matchesPassword("admin123", encoded);

        assertFalse(matches);
    }

    @Test
    @DisplayName("密码匹配 - 特殊字符密码")
    void matchesPassword_specialCharacters_shouldWork() {
        String specialPassword = "P@ssw0rd!#$%";
        String encoded = SecurityUtils.encryptPassword(specialPassword);

        assertTrue(SecurityUtils.matchesPassword(specialPassword, encoded));
        assertFalse(SecurityUtils.matchesPassword("P@ssw0rd!#$", encoded));
    }

    @Test
    @DisplayName("密码匹配 - 中文密码")
    void matchesPassword_chinesePassword_shouldWork() {
        String chinesePassword = "密码测试123";
        String encoded = SecurityUtils.encryptPassword(chinesePassword);

        assertTrue(SecurityUtils.matchesPassword(chinesePassword, encoded));
    }

    // ==================== getAuthentication Tests ====================

    @Test
    @DisplayName("获取Authentication - 已设置认证")
    void getAuthentication_whenSet_shouldReturnAuthentication() {
        setAuthentication(loginUser);

        assertNotNull(SecurityUtils.getAuthentication());
    }

    @Test
    @DisplayName("获取Authentication - 未设置返回null")
    void getAuthentication_whenNotSet_shouldReturnNull() {
        SecurityContextHolder.clearContext();

        assertNull(SecurityUtils.getAuthentication());
    }

    // ==================== Helper Methods ====================

    private void setAuthentication(LoginUser loginUser) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
