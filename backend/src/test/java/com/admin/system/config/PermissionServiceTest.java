package com.admin.system.config;

import com.admin.system.entity.SysUser;
import com.admin.system.security.LoginUser;
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
 * PermissionService 单元测试
 */
@DisplayName("PermissionService 权限验证测试")
class PermissionServiceTest {

    private PermissionService permissionService;

    @BeforeEach
    void setUp() {
        permissionService = new PermissionService();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ==================== hasPermi - Normal User Tests ====================

    @Test
    @DisplayName("普通用户 - 有对应权限返回true")
    void hasPermi_normalUserWithPermission_shouldReturnTrue() {
        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        permissions.add("system:user:add");
        setAuthentication(2L, "normalUser", permissions);

        assertTrue(permissionService.hasPermi("system:user:list"));
        assertTrue(permissionService.hasPermi("system:user:add"));
    }

    @Test
    @DisplayName("普通用户 - 无对应权限返回false")
    void hasPermi_normalUserWithoutPermission_shouldReturnFalse() {
        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        setAuthentication(2L, "normalUser", permissions);

        assertFalse(permissionService.hasPermi("system:user:delete"));
        assertFalse(permissionService.hasPermi("system:role:list"));
    }

    @Test
    @DisplayName("普通用户 - 拥有全部权限标识")
    void hasPermi_normalUserWithAllPermission_shouldReturnTrue() {
        Set<String> permissions = new HashSet<>();
        permissions.add("*:*:*");
        setAuthentication(2L, "normalUser", permissions);

        assertTrue(permissionService.hasPermi("system:user:delete"));
        assertTrue(permissionService.hasPermi("any:module:action"));
    }

    // ==================== hasPermi - Super Admin Tests ====================

    @Test
    @DisplayName("超级管理员(ID=1) - 直接放行任意权限")
    void hasPermi_superAdminById_shouldAlwaysReturnTrue() {
        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list"); // even with limited permissions
        setAuthentication(1L, "superadmin", permissions);

        assertTrue(permissionService.hasPermi("system:user:delete"));
        assertTrue(permissionService.hasPermi("any:random:permission"));
    }

    @Test
    @DisplayName("超级管理员(username=admin) - 直接放行任意权限")
    void hasPermi_superAdminByName_shouldAlwaysReturnTrue() {
        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        setAuthentication(999L, "admin", permissions);

        assertTrue(permissionService.hasPermi("system:user:delete"));
        assertTrue(permissionService.hasPermi("any:random:permission"));
    }

    @Test
    @DisplayName("超级管理员(username=Admin大小写不敏感) - 直接放行")
    void hasPermi_superAdminCaseInsensitive_shouldAlwaysReturnTrue() {
        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        setAuthentication(999L, "Admin", permissions);

        assertTrue(permissionService.hasPermi("system:user:delete"));
    }

    // ==================== hasPermi - Edge Cases ====================

    @Test
    @DisplayName("权限为null - 返回false")
    void hasPermi_nullPermission_shouldReturnFalse() {
        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        setAuthentication(2L, "user", permissions);

        assertFalse(permissionService.hasPermi(null));
    }

    @Test
    @DisplayName("权限为空字符串 - 返回false")
    void hasPermi_emptyPermission_shouldReturnFalse() {
        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        setAuthentication(2L, "user", permissions);

        assertFalse(permissionService.hasPermi(""));
    }

    @Test
    @DisplayName("未登录用户 - 返回false")
    void hasPermi_noAuthentication_shouldReturnFalse() {
        SecurityContextHolder.clearContext();

        assertFalse(permissionService.hasPermi("system:user:list"));
    }

    @Test
    @DisplayName("用户权限集为空 - 返回false")
    void hasPermi_emptyPermissions_shouldReturnFalse() {
        setAuthentication(2L, "user", new HashSet<>());

        assertFalse(permissionService.hasPermi("system:user:list"));
    }

    @Test
    @DisplayName("权限精确匹配 - 部分匹配不通过")
    void hasPermi_partialMatch_shouldReturnFalse() {
        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        setAuthentication(2L, "user", permissions);

        assertFalse(permissionService.hasPermi("system:user"));
        assertFalse(permissionService.hasPermi("system:user:lis"));
        assertFalse(permissionService.hasPermi("system:user:list:extra"));
    }

    // ==================== Helper Methods ====================

    private void setAuthentication(Long userId, String username, Set<String> permissions) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setUsername(username);
        sysUser.setStatus("0");

        LoginUser loginUser = new LoginUser(sysUser, permissions);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
