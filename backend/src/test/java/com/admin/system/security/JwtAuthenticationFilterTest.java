package com.admin.system.security;

import com.admin.system.config.JwtProperties;
import com.admin.system.entity.SysUser;
import com.admin.system.utils.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * JwtAuthenticationFilter 单元测试
 */
@DisplayName("JwtAuthenticationFilter 过滤器测试")
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private RedisUtil redisUtil;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }

    // ==================== Token Extraction Tests ====================

    @Test
    @DisplayName("无Authorization头 - 直接放行,不设置认证")
    void doFilter_noAuthHeader_shouldContinueWithoutAuthentication() throws ServletException, IOException {
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Authorization头无Bearer前缀 - 不设置认证")
    void doFilter_noBearerPrefix_shouldContinueWithoutAuthentication() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic sometoken");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Authorization头为空字符串 - 不设置认证")
    void doFilter_emptyAuthHeader_shouldContinueWithoutAuthentication() throws ServletException, IOException {
        request.addHeader("Authorization", "");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // ==================== Valid Token Tests ====================

    @Test
    @DisplayName("有效Token - 设置认证并放行")
    void doFilter_validToken_shouldSetAuthentication() throws ServletException, IOException {
        String token = "valid-jwt-token";
        request.addHeader("Authorization", "Bearer " + token);

        LoginUser loginUser = createLoginUser(token, System.currentTimeMillis() + 60 * 60 * 1000);
        when(redisUtil.get("login_tokens:" + token)).thenReturn(loginUser);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals("admin", principal.getUsername());
    }

    @Test
    @DisplayName("Token在Redis中不存在 - 不设置认证")
    void doFilter_tokenNotInRedis_shouldNotSetAuthentication() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer expired-token");

        when(redisUtil.get("login_tokens:expired-token")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // ==================== Token Refresh Tests ====================

    @Test
    @DisplayName("Token即将过期 - 自动刷新缓存")
    void doFilter_tokenNearExpiry_shouldRefreshToken() throws ServletException, IOException {
        String token = "near-expiry-token";
        request.addHeader("Authorization", "Bearer " + token);

        // Token will expire in 10 minutes (less than 20 minutes threshold)
        long nearExpiryTime = System.currentTimeMillis() + 10 * 60 * 1000;
        LoginUser loginUser = createLoginUser(token, nearExpiryTime);

        when(redisUtil.get("login_tokens:" + token)).thenReturn(loginUser);
        when(jwtProperties.getExpireTime()).thenReturn(30);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify token was refreshed in Redis
        verify(redisUtil).set(eq("login_tokens:" + token), any(LoginUser.class), eq(30L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("Token远未过期 - 不刷新缓存")
    void doFilter_tokenNotNearExpiry_shouldNotRefreshToken() throws ServletException, IOException {
        String token = "valid-token-long-expiry";
        request.addHeader("Authorization", "Bearer " + token);

        // Token expires in 25 minutes (more than 20 minutes threshold)
        long farExpiryTime = System.currentTimeMillis() + 25 * 60 * 1000;
        LoginUser loginUser = createLoginUser(token, farExpiryTime);

        when(redisUtil.get("login_tokens:" + token)).thenReturn(loginUser);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify token was NOT refreshed
        verify(redisUtil, never()).set(anyString(), any(), anyLong(), any(TimeUnit.class));
    }

    // ==================== Filter Chain Tests ====================

    @Test
    @DisplayName("过滤器总是调用chain.doFilter - 不论Token是否有效")
    void doFilter_shouldAlwaysCallChainDoFilter() throws ServletException, IOException {
        // Case 1: No token
        MockFilterChain chain1 = mock(MockFilterChain.class);
        jwtAuthenticationFilter.doFilterInternal(request, response, chain1);
        verify(chain1).doFilter(request, response);

        // Case 2: Invalid token
        SecurityContextHolder.clearContext();
        MockHttpServletRequest request2 = new MockHttpServletRequest();
        request2.addHeader("Authorization", "Bearer invalid-token");
        when(redisUtil.get("login_tokens:invalid-token")).thenReturn(null);
        MockFilterChain chain2 = mock(MockFilterChain.class);
        jwtAuthenticationFilter.doFilterInternal(request2, response, chain2);
        verify(chain2).doFilter(request2, response);
    }

    @Test
    @DisplayName("已有认证信息时 - 不重复设置")
    void doFilter_existingAuthentication_shouldNotOverwrite() throws ServletException, IOException {
        String token = "valid-token";
        request.addHeader("Authorization", "Bearer " + token);

        LoginUser loginUser = createLoginUser(token, System.currentTimeMillis() + 60 * 60 * 1000);
        when(redisUtil.get("login_tokens:" + token)).thenReturn(loginUser);

        // Pre-set authentication
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken existingAuth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Should still have the original authentication
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Redis异常时 - 清除认证上下文并继续")
    void doFilter_redisException_shouldClearContextAndContinue() throws ServletException, IOException {
        String token = "some-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(redisUtil.get("login_tokens:" + token)).thenThrow(new RuntimeException("Redis connection failed"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Authentication should be cleared after exception
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // ==================== Helper Methods ====================

    private LoginUser createLoginUser(String token, long expireTime) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(1L);
        sysUser.setUsername("admin");
        sysUser.setDeptId(100L);
        sysUser.setStatus("0");

        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");

        LoginUser loginUser = new LoginUser(sysUser, permissions);
        loginUser.setToken(token);
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(expireTime);
        return loginUser;
    }
}
