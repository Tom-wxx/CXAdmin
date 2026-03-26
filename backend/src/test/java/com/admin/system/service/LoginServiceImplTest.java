package com.admin.system.service;

import com.admin.system.config.JwtProperties;
import com.admin.system.dto.LoginDTO;
import com.admin.system.entity.SysUser;
import com.admin.system.security.LoginUser;
import com.admin.system.service.impl.LoginServiceImpl;
import com.admin.system.utils.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * LoginServiceImpl 单元测试
 */
@DisplayName("LoginServiceImpl 登录服务测试")
@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @InjectMocks
    private LoginServiceImpl loginService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private ISysUserService userService;

    @Mock
    private ISysMenuService menuService;

    @Mock
    private ISysFileService fileService;

    @Mock
    private ISysLoginLogService loginLogService;

    private LoginDTO loginDTO;
    private LoginUser loginUser;
    private SysUser sysUser;

    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("admin123");
        loginDTO.setCode("abcd");
        loginDTO.setUuid("test-uuid");

        sysUser = new SysUser();
        sysUser.setUserId(1L);
        sysUser.setUsername("admin");
        sysUser.setDeptId(100L);
        sysUser.setStatus("0");

        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        loginUser = new LoginUser(sysUser, permissions);
    }

    // ==================== Login Tests ====================

    @Test
    @DisplayName("登录成功 - 返回Token")
    void login_success_shouldReturnToken() {
        // Mock captcha validation
        when(redisUtil.get("captcha:test-uuid")).thenReturn("abcd");

        // Mock login retry check (no previous failures)
        when(redisUtil.get("login_retry:admin")).thenReturn(null);

        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(loginUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Mock JWT properties
        when(jwtProperties.getSecret()).thenReturn("testSecretKey1234567890123456789012345678901234567890");
        when(jwtProperties.getExpireTime()).thenReturn(30);

        Map<String, Object> result = loginService.login(loginDTO);

        assertNotNull(result);
        assertNotNull(result.get("token"));
        assertTrue(result.get("token").toString().length() > 0);

        // Verify captcha deleted
        verify(redisUtil).delete("captcha:test-uuid");
        // Verify login retry cleared
        verify(redisUtil).delete("login_retry:admin");
        // Verify token stored in Redis
        verify(redisUtil).set(startsWith("login_tokens:"), any(LoginUser.class), eq(30L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("登录失败 - 验证码过期")
    void login_captchaExpired_shouldThrowException() {
        when(redisUtil.get("captcha:test-uuid")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> loginService.login(loginDTO));

        assertEquals("验证码已过期", exception.getMessage());
    }

    @Test
    @DisplayName("登录失败 - 验证码错误")
    void login_captchaWrong_shouldThrowException() {
        when(redisUtil.get("captcha:test-uuid")).thenReturn("wxyz");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> loginService.login(loginDTO));

        assertEquals("验证码错误", exception.getMessage());
    }

    @Test
    @DisplayName("登录成功 - 验证码不区分大小写")
    void login_captchaCaseInsensitive_shouldPass() {
        loginDTO.setCode("ABCD");
        when(redisUtil.get("captcha:test-uuid")).thenReturn("abcd");
        when(redisUtil.get("login_retry:admin")).thenReturn(null);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(loginUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtProperties.getSecret()).thenReturn("testSecretKey1234567890123456789012345678901234567890");
        when(jwtProperties.getExpireTime()).thenReturn(30);

        Map<String, Object> result = loginService.login(loginDTO);

        assertNotNull(result);
        assertNotNull(result.get("token"));
    }

    @Test
    @DisplayName("登录失败 - 用户名或密码错误")
    void login_badCredentials_shouldThrowException() {
        when(redisUtil.get("captcha:test-uuid")).thenReturn("abcd");
        when(redisUtil.get("login_retry:admin")).thenReturn(null);
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> loginService.login(loginDTO));
    }

    @Test
    @DisplayName("登录成功 - Token存入Redis并设置过期时间")
    void login_success_shouldStoreTokenInRedis() {
        when(redisUtil.get("captcha:test-uuid")).thenReturn("abcd");
        when(redisUtil.get("login_retry:admin")).thenReturn(null);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(loginUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtProperties.getSecret()).thenReturn("testSecretKey1234567890123456789012345678901234567890");
        when(jwtProperties.getExpireTime()).thenReturn(60);

        loginService.login(loginDTO);

        verify(redisUtil).set(
                argThat(key -> key.startsWith("login_tokens:")),
                argThat(value -> value instanceof LoginUser),
                eq(60L),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    @DisplayName("登录失败 - 登录重试次数超限")
    void login_retryLimitExceeded_shouldThrowException() {
        when(redisUtil.get("captcha:test-uuid")).thenReturn("abcd");
        when(redisUtil.get("login_retry:admin")).thenReturn("5");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> loginService.login(loginDTO));

        assertTrue(exception.getMessage().contains("登录失败次数过多"));
    }

    @Test
    @DisplayName("登录失败 - 认证失败增加重试计数")
    void login_authFailed_shouldIncrementRetryCount() {
        when(redisUtil.get("captcha:test-uuid")).thenReturn("abcd");
        when(redisUtil.get("login_retry:admin")).thenReturn(null);
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        when(redisUtil.increment("login_retry:admin")).thenReturn(1L);

        assertThrows(BadCredentialsException.class,
                () -> loginService.login(loginDTO));

        verify(redisUtil).increment("login_retry:admin");
    }

    // ==================== Logout Tests ====================

    @Test
    @DisplayName("登出 - 无登录用户不报错")
    void logout_noLoginUser_shouldNotThrow() {
        // SecurityContextHolder has no authentication set, getLoginUser returns null
        assertDoesNotThrow(() -> loginService.logout());
    }

    // ==================== GetCaptcha Tests ====================

    @Test
    @DisplayName("获取验证码 - 返回UUID和图片")
    void getCaptcha_shouldReturnUuidAndImage() {
        Map<String, Object> result = loginService.getCaptcha();

        assertNotNull(result);
        assertNotNull(result.get("uuid"));
        assertNotNull(result.get("img"));

        // Verify captcha stored in Redis with 2-minute expiry
        verify(redisUtil).set(
                argThat(key -> key.startsWith("captcha:")),
                anyString(),
                eq(2L),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    @DisplayName("获取验证码 - 每次UUID不同")
    void getCaptcha_shouldGenerateUniqueUuids() {
        Map<String, Object> result1 = loginService.getCaptcha();
        Map<String, Object> result2 = loginService.getCaptcha();

        assertNotEquals(result1.get("uuid"), result2.get("uuid"));
    }
}
