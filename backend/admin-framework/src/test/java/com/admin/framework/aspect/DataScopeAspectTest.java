package com.admin.framework.aspect;

import com.admin.system.entity.SysRole;
import com.admin.system.entity.SysUser;
import com.admin.system.security.LoginUser;
import com.admin.system.service.ISysRoleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * DataScopeAspect 数据权限切面单元测试（纯单测，无 Spring 容器）
 */
@DisplayName("DataScopeAspect 数据权限切面测试")
@ExtendWith(MockitoExtension.class)
class DataScopeAspectTest {

    @InjectMocks
    private DataScopeAspect aspect;

    @Mock
    private ISysRoleService roleService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /** 以指定 userId / deptId / username 登入安全上下文 */
    private void login(Long userId, Long deptId, String username) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setDeptId(deptId);
        user.setUsername(username);
        LoginUser loginUser = new LoginUser(user, new HashSet<>());
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private SysRole role(Long roleId, String dataScope) {
        SysRole r = new SysRole();
        r.setRoleId(roleId);
        r.setDataScope(dataScope);
        return r;
    }

    @Test
    @DisplayName("超级管理员 - 不附加任何限制")
    void superAdmin_noFilter() {
        login(1L, 100L, "admin");
        assertEquals("", aspect.buildFilter("d", "u"));
    }

    @Test
    @DisplayName("未登录 - 不附加限制（接口已被鉴权拦截）")
    void noLogin_noFilter() {
        SecurityContextHolder.clearContext();
        assertEquals("", aspect.buildFilter("d", "u"));
    }

    @Test
    @DisplayName("全部数据权限(1) - 不附加限制")
    void scopeAll_noFilter() {
        login(2L, 100L, "zhangsan");
        when(roleService.selectRolesByUserId(2L)).thenReturn(List.of(role(10L, "1")));
        assertEquals("", aspect.buildFilter("d", "u"));
    }

    @Test
    @DisplayName("本部门(3) - 按 dept_id 过滤")
    void scopeDept() {
        login(2L, 100L, "zhangsan");
        when(roleService.selectRolesByUserId(2L)).thenReturn(List.of(role(10L, "3")));
        assertEquals(" AND (d.dept_id = 100)", aspect.buildFilter("d", "u"));
    }

    @Test
    @DisplayName("本部门及以下(4) - 用 ancestors 递归")
    void scopeDeptAndChild() {
        login(2L, 100L, "zhangsan");
        when(roleService.selectRolesByUserId(2L)).thenReturn(List.of(role(10L, "4")));
        assertEquals(
                " AND (d.dept_id IN (SELECT dept_id FROM sys_dept WHERE dept_id = 100 OR FIND_IN_SET(100, ancestors)))",
                aspect.buildFilter("d", "u"));
    }

    @Test
    @DisplayName("自定义(2) - 子查询 sys_role_dept")
    void scopeCustom() {
        login(2L, 100L, "zhangsan");
        when(roleService.selectRolesByUserId(2L)).thenReturn(List.of(role(7L, "2")));
        assertEquals(
                " AND (d.dept_id IN (SELECT dept_id FROM sys_role_dept WHERE role_id = 7))",
                aspect.buildFilter("d", "u"));
    }

    @Test
    @DisplayName("仅本人(5) - 按 user_id 过滤")
    void scopeSelf() {
        login(2L, 100L, "zhangsan");
        when(roleService.selectRolesByUserId(2L)).thenReturn(List.of(role(10L, "5")));
        assertEquals(" AND (u.user_id = 2)", aspect.buildFilter("d", "u"));
    }

    @Test
    @DisplayName("仅本人(5) + 空 userAlias（部门列表场景）- 退化为看不到任何数据")
    void scopeSelf_blankUserAlias_denies() {
        login(2L, 100L, "zhangsan");
        when(roleService.selectRolesByUserId(2L)).thenReturn(List.of(role(10L, "5")));
        assertEquals(" AND (d.dept_id = 0)", aspect.buildFilter("d", ""));
    }

    @Test
    @DisplayName("多角色 - 本部门(3) OR 仅本人(5) 合并")
    void multiRole_orCombined() {
        login(2L, 100L, "zhangsan");
        when(roleService.selectRolesByUserId(2L)).thenReturn(List.of(role(10L, "3"), role(11L, "5")));
        assertEquals(" AND (d.dept_id = 100 OR u.user_id = 2)", aspect.buildFilter("d", "u"));
    }

    @Test
    @DisplayName("无角色 - 看不到任何数据")
    void noRoles_denyAll() {
        login(2L, 100L, "zhangsan");
        when(roleService.selectRolesByUserId(2L)).thenReturn(Collections.emptyList());
        assertEquals(" AND (1 = 0)", aspect.buildFilter("d", "u"));
    }

    @Test
    @DisplayName("注入 - 片段写入载体 params.dataScope")
    void handleDataScope_injectsIntoCarrier() {
        login(2L, 100L, "zhangsan");
        when(roleService.selectRolesByUserId(2L)).thenReturn(List.of(role(10L, "3")));

        SysUser carrier = new SysUser();
        aspect.handleDataScope(new Object[]{carrier}, "d", "u");

        assertEquals(" AND (d.dept_id = 100)", carrier.getParams().get(DataScopeAspect.DATA_SCOPE_PARAM));
    }

    @Test
    @DisplayName("注入 - 无 BaseEntity 入参时安全跳过")
    void handleDataScope_noCarrier_noop() {
        login(2L, 100L, "zhangsan");
        assertDoesNotThrow(() -> aspect.handleDataScope(new Object[]{"x", 1}, "d", "u"));
    }
}
