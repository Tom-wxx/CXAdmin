package com.admin.system.security;

import com.admin.system.entity.SysUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LoginUser 单元测试
 */
@DisplayName("LoginUser 实体测试")
class LoginUserTest {

    private SysUser sysUser;
    private Set<String> permissions;

    @BeforeEach
    void setUp() {
        sysUser = new SysUser();
        sysUser.setUserId(1L);
        sysUser.setUsername("admin");
        sysUser.setPassword("$2a$10$encoded");
        sysUser.setDeptId(100L);
        sysUser.setStatus("0");
        sysUser.setNickname("Administrator");

        permissions = new HashSet<>();
        permissions.add("system:user:list");
        permissions.add("system:user:add");
        permissions.add("system:role:list");
    }

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("构造函数 - 正确初始化属性")
    void constructor_shouldInitializeFields() {
        LoginUser loginUser = new LoginUser(sysUser, permissions);

        assertEquals(1L, loginUser.getUserId());
        assertEquals(100L, loginUser.getDeptId());
        assertEquals(sysUser, loginUser.getUser());
        assertEquals(permissions, loginUser.getPermissions());
    }

    @Test
    @DisplayName("构造函数 - 无参构造")
    void noArgConstructor_shouldCreateEmptyInstance() {
        LoginUser loginUser = new LoginUser();

        assertNull(loginUser.getUserId());
        assertNull(loginUser.getUser());
        assertNull(loginUser.getPermissions());
    }

    // ==================== UserDetails Interface Tests ====================

    @Test
    @DisplayName("getAuthorities - 权限列表正确转换")
    void getAuthorities_shouldConvertPermissionsToAuthorities() {
        LoginUser loginUser = new LoginUser(sysUser, permissions);

        Collection<? extends GrantedAuthority> authorities = loginUser.getAuthorities();

        assertNotNull(authorities);
        assertEquals(3, authorities.size());
    }

    @Test
    @DisplayName("getUsername - 返回用户名")
    void getUsername_shouldReturnUsername() {
        LoginUser loginUser = new LoginUser(sysUser, permissions);

        assertEquals("admin", loginUser.getUsername());
    }

    @Test
    @DisplayName("getPassword - 返回密码")
    void getPassword_shouldReturnPassword() {
        LoginUser loginUser = new LoginUser(sysUser, permissions);

        assertEquals("$2a$10$encoded", loginUser.getPassword());
    }

    @Test
    @DisplayName("isAccountNonExpired - 返回true")
    void isAccountNonExpired_shouldReturnTrue() {
        LoginUser loginUser = new LoginUser(sysUser, permissions);

        assertTrue(loginUser.isAccountNonExpired());
    }

    @Test
    @DisplayName("isAccountNonLocked - 返回true")
    void isAccountNonLocked_shouldReturnTrue() {
        LoginUser loginUser = new LoginUser(sysUser, permissions);

        assertTrue(loginUser.isAccountNonLocked());
    }

    @Test
    @DisplayName("isCredentialsNonExpired - 返回true")
    void isCredentialsNonExpired_shouldReturnTrue() {
        LoginUser loginUser = new LoginUser(sysUser, permissions);

        assertTrue(loginUser.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("isEnabled - 状态为0时返回true")
    void isEnabled_statusZero_shouldReturnTrue() {
        sysUser.setStatus("0");
        LoginUser loginUser = new LoginUser(sysUser, permissions);

        assertTrue(loginUser.isEnabled());
    }

    @Test
    @DisplayName("isEnabled - 状态为1时返回false（停用）")
    void isEnabled_statusOne_shouldReturnFalse() {
        sysUser.setStatus("1");
        LoginUser loginUser = new LoginUser(sysUser, permissions);

        assertFalse(loginUser.isEnabled());
    }

    // ==================== Token & Time Tests ====================

    @Test
    @DisplayName("Token - 设置和获取Token")
    void token_shouldSetAndGet() {
        LoginUser loginUser = new LoginUser(sysUser, permissions);
        loginUser.setToken("test-jwt-token");

        assertEquals("test-jwt-token", loginUser.getToken());
    }

    @Test
    @DisplayName("登录时间 - 设置和获取")
    void loginTime_shouldSetAndGet() {
        LoginUser loginUser = new LoginUser(sysUser, permissions);
        long now = System.currentTimeMillis();
        loginUser.setLoginTime(now);

        assertEquals(now, loginUser.getLoginTime());
    }

    @Test
    @DisplayName("过期时间 - 设置和获取")
    void expireTime_shouldSetAndGet() {
        LoginUser loginUser = new LoginUser(sysUser, permissions);
        long expiry = System.currentTimeMillis() + 30 * 60 * 1000;
        loginUser.setExpireTime(expiry);

        assertEquals(expiry, loginUser.getExpireTime());
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("空权限集 - getAuthorities返回空列表")
    void getAuthorities_emptyPermissions_shouldReturnEmptyList() {
        LoginUser loginUser = new LoginUser(sysUser, new HashSet<>());

        Collection<? extends GrantedAuthority> authorities = loginUser.getAuthorities();

        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }
}
