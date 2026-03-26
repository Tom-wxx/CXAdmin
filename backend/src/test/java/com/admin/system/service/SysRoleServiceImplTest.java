package com.admin.system.service;

import com.admin.system.common.exception.ServiceException;
import com.admin.system.dto.RoleDTO;
import com.admin.system.entity.SysRole;
import com.admin.system.mapper.SysRoleMapper;
import com.admin.system.mapper.SysUserMapper;
import com.admin.system.service.impl.SysRoleServiceImpl;
import com.admin.system.vo.RoleVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysRoleServiceImpl 单元测试
 */
@DisplayName("SysRoleServiceImpl 角色服务测试")
@ExtendWith(MockitoExtension.class)
class SysRoleServiceImplTest {

    @InjectMocks
    private SysRoleServiceImpl roleService;

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SysUserMapper userMapper;

    private RoleDTO roleDTO;
    private SysRole existingRole;

    @BeforeEach
    void setUp() {
        roleDTO = new RoleDTO();
        roleDTO.setRoleName("Test Role");
        roleDTO.setRoleKey("test_role");
        roleDTO.setRoleSort(1);
        roleDTO.setStatus("0");
        roleDTO.setMenuIds(new Long[]{1L, 2L, 3L});

        existingRole = new SysRole();
        existingRole.setRoleId(1L);
        existingRole.setRoleName("Admin");
        existingRole.setRoleKey("admin");
    }

    // ==================== selectRoleById Tests ====================

    @Test
    @DisplayName("查询角色详情 - 存在")
    void selectRoleById_exists_shouldReturnRoleVO() {
        RoleVO roleVO = new RoleVO();
        roleVO.setRoleId(1L);
        roleVO.setRoleName("Admin");
        when(roleMapper.selectRoleVOById(1L)).thenReturn(roleVO);

        RoleVO result = roleService.selectRoleById(1L);

        assertNotNull(result);
        assertEquals("Admin", result.getRoleName());
    }

    @Test
    @DisplayName("查询角色详情 - 不存在返回null")
    void selectRoleById_notExists_shouldReturnNull() {
        when(roleMapper.selectRoleVOById(999L)).thenReturn(null);

        RoleVO result = roleService.selectRoleById(999L);

        assertNull(result);
    }

    // ==================== selectRoleList Tests ====================

    @Test
    @DisplayName("查询所有角色列表")
    void selectRoleList_shouldReturnList() {
        RoleVO role1 = new RoleVO();
        role1.setRoleName("Admin");
        RoleVO role2 = new RoleVO();
        role2.setRoleName("User");
        when(roleMapper.selectRoleList()).thenReturn(Arrays.asList(role1, role2));

        List<RoleVO> result = roleService.selectRoleList();

        assertEquals(2, result.size());
    }

    // ==================== selectRolesByUserId Tests ====================

    @Test
    @DisplayName("按用户ID查询角色列表")
    void selectRolesByUserId_shouldReturnRoles() {
        SysRole role = new SysRole();
        role.setRoleId(1L);
        role.setRoleName("Admin");
        when(roleMapper.selectRolesByUserId(1L)).thenReturn(Arrays.asList(role));

        List<SysRole> result = roleService.selectRolesByUserId(1L);

        assertEquals(1, result.size());
        assertEquals("Admin", result.get(0).getRoleName());
    }

    // ==================== insertRole Tests ====================

    @Test
    @DisplayName("新增角色 - 成功")
    void insertRole_success() {
        when(roleMapper.checkRoleNameUnique("Test Role")).thenReturn(null);
        when(roleMapper.checkRoleKeyUnique("test_role")).thenReturn(null);
        // Simulate MyBatis Plus auto-setting the roleId after insert
        when(roleMapper.insert(any(SysRole.class))).thenAnswer(invocation -> {
            SysRole role = invocation.getArgument(0);
            role.setRoleId(10L);
            return 1;
        });
        // saveRoleMenus calls deleteRoleMenuByRoleId then batchRoleMenu
        when(roleMapper.deleteRoleMenuByRoleId(10L)).thenReturn(0);
        when(roleMapper.batchRoleMenu(eq(10L), any())).thenReturn(3);

        assertDoesNotThrow(() -> roleService.insertRole(roleDTO));

        verify(roleMapper).insert(any(SysRole.class));
    }

    @Test
    @DisplayName("新增角色 - 角色名称已存在")
    void insertRole_duplicateName_shouldThrowException() {
        SysRole existing = new SysRole();
        existing.setRoleId(99L);
        when(roleMapper.checkRoleNameUnique("Test Role")).thenReturn(existing);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> roleService.insertRole(roleDTO));

        assertTrue(exception.getMessage().contains("角色名称已存在"));
    }

    @Test
    @DisplayName("新增角色 - 角色权限已存在")
    void insertRole_duplicateKey_shouldThrowException() {
        when(roleMapper.checkRoleNameUnique("Test Role")).thenReturn(null);

        SysRole existing = new SysRole();
        existing.setRoleId(99L);
        when(roleMapper.checkRoleKeyUnique("test_role")).thenReturn(existing);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> roleService.insertRole(roleDTO));

        assertTrue(exception.getMessage().contains("角色权限已存在"));
    }

    @Test
    @DisplayName("新增角色 - 无菜单权限")
    void insertRole_noMenuIds_shouldNotCallBatchRoleMenu() {
        roleDTO.setMenuIds(null);
        when(roleMapper.checkRoleNameUnique("Test Role")).thenReturn(null);
        when(roleMapper.checkRoleKeyUnique("test_role")).thenReturn(null);
        when(roleMapper.insert(any(SysRole.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.insertRole(roleDTO));

        verify(roleMapper, never()).batchRoleMenu(any(), any());
    }

    @Test
    @DisplayName("新增角色 - 空菜单数组")
    void insertRole_emptyMenuIds_shouldNotCallBatchRoleMenu() {
        roleDTO.setMenuIds(new Long[]{});
        when(roleMapper.checkRoleNameUnique("Test Role")).thenReturn(null);
        when(roleMapper.checkRoleKeyUnique("test_role")).thenReturn(null);
        when(roleMapper.insert(any(SysRole.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.insertRole(roleDTO));

        verify(roleMapper, never()).batchRoleMenu(any(), any());
    }

    // ==================== updateRole Tests ====================

    @Test
    @DisplayName("修改角色 - 成功")
    void updateRole_success() {
        roleDTO.setRoleId(1L);
        when(roleMapper.checkRoleNameUnique("Test Role")).thenReturn(null);
        when(roleMapper.checkRoleKeyUnique("test_role")).thenReturn(null);
        when(roleMapper.updateById(any(SysRole.class))).thenReturn(1);
        // updateRole calls deleteRoleMenuByRoleId once, then saveRoleMenus calls it again
        when(roleMapper.deleteRoleMenuByRoleId(1L)).thenReturn(3);
        when(roleMapper.batchRoleMenu(any(), any())).thenReturn(3);

        assertDoesNotThrow(() -> roleService.updateRole(roleDTO));

        verify(roleMapper).updateById(any(SysRole.class));
        // deleteRoleMenuByRoleId called twice: once in updateRole, once in saveRoleMenus
        verify(roleMapper, times(2)).deleteRoleMenuByRoleId(1L);
    }

    @Test
    @DisplayName("修改角色 - 角色ID为空")
    void updateRole_nullRoleId_shouldThrowException() {
        roleDTO.setRoleId(null);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> roleService.updateRole(roleDTO));

        assertEquals("角色ID不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("修改角色 - 同一角色名不重复检测")
    void updateRole_sameName_shouldPass() {
        roleDTO.setRoleId(1L);
        roleDTO.setRoleName("Admin");

        SysRole sameRole = new SysRole();
        sameRole.setRoleId(1L); // same ID
        when(roleMapper.checkRoleNameUnique("Admin")).thenReturn(sameRole);
        when(roleMapper.checkRoleKeyUnique("test_role")).thenReturn(null);
        when(roleMapper.updateById(any(SysRole.class))).thenReturn(1);
        when(roleMapper.deleteRoleMenuByRoleId(1L)).thenReturn(0);
        when(roleMapper.batchRoleMenu(any(), any())).thenReturn(3);

        assertDoesNotThrow(() -> roleService.updateRole(roleDTO));
    }

    // ==================== deleteRoleById Tests ====================

    @Test
    @DisplayName("删除角色 - 成功(无用户分配)")
    void deleteRoleById_success() {
        when(userMapper.countUsersByRoleId(1L)).thenReturn(0L);
        when(roleMapper.deleteRoleMenuByRoleId(1L)).thenReturn(3);
        when(roleMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> roleService.deleteRoleById(1L));

        verify(roleMapper).deleteRoleMenuByRoleId(1L);
        verify(roleMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除角色 - 角色ID为空")
    void deleteRoleById_nullRoleId_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> roleService.deleteRoleById(null));

        assertEquals("角色ID不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("删除角色 - 角色已分配用户不能删除")
    void deleteRoleById_hasUsers_shouldThrowException() {
        when(userMapper.countUsersByRoleId(1L)).thenReturn(5L);
        existingRole.setRoleId(1L);
        when(roleMapper.selectById(1L)).thenReturn(existingRole);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> roleService.deleteRoleById(1L));

        assertTrue(exception.getMessage().contains("已分配给"));
        assertTrue(exception.getMessage().contains("不能删除"));
    }

    // ==================== deleteRoleByIds Tests ====================

    @Test
    @DisplayName("批量删除角色 - 成功")
    void deleteRoleByIds_success() {
        Long[] roleIds = {1L, 2L};
        when(userMapper.countUsersByRoleId(anyLong())).thenReturn(0L);
        when(roleMapper.deleteRoleMenuByRoleId(anyLong())).thenReturn(1);
        when(roleMapper.deleteById(anyLong())).thenReturn(1);

        assertDoesNotThrow(() -> roleService.deleteRoleByIds(roleIds));

        verify(roleMapper, times(2)).deleteById(anyLong());
    }

    @Test
    @DisplayName("批量删除角色 - 空数组")
    void deleteRoleByIds_emptyArray_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> roleService.deleteRoleByIds(new Long[]{}));

        assertEquals("角色ID不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("批量删除角色 - null")
    void deleteRoleByIds_null_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> roleService.deleteRoleByIds(null));

        assertEquals("角色ID不能为空", exception.getMessage());
    }

    // ==================== updateRoleStatus Tests ====================

    @Test
    @DisplayName("修改角色状态 - 成功")
    void updateRoleStatus_success() {
        when(roleMapper.updateById(any(SysRole.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRoleStatus(1L, "1"));

        verify(roleMapper).updateById(argThat(role -> {
            assertEquals(1L, role.getRoleId());
            assertEquals("1", role.getStatus());
            return true;
        }));
    }

    @Test
    @DisplayName("修改角色状态 - 角色ID为空")
    void updateRoleStatus_nullRoleId_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> roleService.updateRoleStatus(null, "0"));

        assertEquals("角色ID不能为空", exception.getMessage());
    }

    // ==================== checkRoleNameUnique Tests ====================

    @Test
    @DisplayName("角色名称唯一校验 - 无重复返回true")
    void checkRoleNameUnique_noDuplicate_shouldReturnTrue() {
        when(roleMapper.checkRoleNameUnique("NewRole")).thenReturn(null);

        assertTrue(roleService.checkRoleNameUnique("NewRole", null));
    }

    @Test
    @DisplayName("角色名称唯一校验 - 同一角色返回true")
    void checkRoleNameUnique_sameRole_shouldReturnTrue() {
        existingRole.setRoleId(1L);
        when(roleMapper.checkRoleNameUnique("Admin")).thenReturn(existingRole);

        assertTrue(roleService.checkRoleNameUnique("Admin", 1L));
    }

    @Test
    @DisplayName("角色名称唯一校验 - 不同角色已存在返回false")
    void checkRoleNameUnique_differentRole_shouldReturnFalse() {
        existingRole.setRoleId(2L);
        when(roleMapper.checkRoleNameUnique("Admin")).thenReturn(existingRole);

        assertFalse(roleService.checkRoleNameUnique("Admin", 1L));
    }

    // ==================== checkRoleKeyUnique Tests ====================

    @Test
    @DisplayName("角色权限唯一校验 - 无重复返回true")
    void checkRoleKeyUnique_noDuplicate_shouldReturnTrue() {
        when(roleMapper.checkRoleKeyUnique("new_key")).thenReturn(null);

        assertTrue(roleService.checkRoleKeyUnique("new_key", null));
    }

    @Test
    @DisplayName("角色权限唯一校验 - 不同角色已存在返回false")
    void checkRoleKeyUnique_differentRole_shouldReturnFalse() {
        existingRole.setRoleId(2L);
        when(roleMapper.checkRoleKeyUnique("admin")).thenReturn(existingRole);

        assertFalse(roleService.checkRoleKeyUnique("admin", 1L));
    }

    // ==================== saveRoleMenus Tests ====================

    @Test
    @DisplayName("保存角色菜单权限 - 成功")
    void saveRoleMenus_success() {
        Long[] menuIds = {1L, 2L, 3L};
        when(roleMapper.deleteRoleMenuByRoleId(1L)).thenReturn(0);
        when(roleMapper.batchRoleMenu(1L, menuIds)).thenReturn(3);

        assertDoesNotThrow(() -> roleService.saveRoleMenus(1L, menuIds));

        verify(roleMapper).deleteRoleMenuByRoleId(1L);
        verify(roleMapper).batchRoleMenu(1L, menuIds);
    }

    @Test
    @DisplayName("保存角色菜单权限 - 角色ID为空")
    void saveRoleMenus_nullRoleId_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> roleService.saveRoleMenus(null, new Long[]{1L}));

        assertEquals("角色ID不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("保存角色菜单权限 - 空菜单数组只删除不新增")
    void saveRoleMenus_emptyMenuIds_shouldOnlyDelete() {
        when(roleMapper.deleteRoleMenuByRoleId(1L)).thenReturn(3);

        assertDoesNotThrow(() -> roleService.saveRoleMenus(1L, new Long[]{}));

        verify(roleMapper).deleteRoleMenuByRoleId(1L);
        verify(roleMapper, never()).batchRoleMenu(anyLong(), any());
    }

    @Test
    @DisplayName("保存角色菜单权限 - null菜单数组只删除不新增")
    void saveRoleMenus_nullMenuIds_shouldOnlyDelete() {
        when(roleMapper.deleteRoleMenuByRoleId(1L)).thenReturn(3);

        assertDoesNotThrow(() -> roleService.saveRoleMenus(1L, null));

        verify(roleMapper).deleteRoleMenuByRoleId(1L);
        verify(roleMapper, never()).batchRoleMenu(anyLong(), any());
    }

    // ==================== selectMenuIdsByRoleId Tests ====================

    @Test
    @DisplayName("查询角色菜单ID列表")
    void selectMenuIdsByRoleId_shouldReturnList() {
        List<Long> menuIds = Arrays.asList(1L, 2L, 3L);
        when(roleMapper.selectMenuIdsByRoleId(1L)).thenReturn(menuIds);

        List<Long> result = roleService.selectMenuIdsByRoleId(1L);

        assertEquals(3, result.size());
    }
}
