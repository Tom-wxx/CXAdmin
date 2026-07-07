package com.admin.system.service;

import com.admin.common.exception.ServiceException;
import com.admin.system.datascope.DataScopeChecker;
import com.admin.system.dto.UserDTO;
import com.admin.system.entity.SysUser;
import com.admin.system.mapper.SysUserMapper;
import com.admin.system.security.LoginUser;
import com.admin.system.security.SecurityUtils;
import com.admin.system.service.impl.SysUserServiceImpl;
import com.admin.system.vo.UserVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysUserServiceImpl 单元测试
 */
@DisplayName("SysUserServiceImpl 用户服务测试")
@ExtendWith(MockitoExtension.class)
class SysUserServiceImplTest {

    @InjectMocks
    private SysUserServiceImpl userService;

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private DataScopeChecker dataScopeChecker;

    private UserDTO userDTO;
    private SysUser existingUser;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUsername("newuser");
        userDTO.setNickname("New User");
        userDTO.setPassword("password123");
        userDTO.setPhone("13800138001");
        userDTO.setEmail("newuser@example.com");
        userDTO.setGender("0");
        userDTO.setRoleIds(new Long[]{2L});
        userDTO.setPostIds(new Long[]{1L});

        existingUser = new SysUser();
        existingUser.setUserId(1L);
        existingUser.setUsername("admin");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ==================== selectUserByUsername Tests ====================

    @Test
    @DisplayName("按用户名查询 - 用户存在")
    void selectUserByUsername_exists_shouldReturnUser() {
        when(userMapper.selectUserByUsername("admin")).thenReturn(existingUser);

        SysUser result = userService.selectUserByUsername("admin");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
    }

    @Test
    @DisplayName("按用户名查询 - 用户不存在")
    void selectUserByUsername_notExists_shouldReturnNull() {
        when(userMapper.selectUserByUsername("nonexistent")).thenReturn(null);

        SysUser result = userService.selectUserByUsername("nonexistent");

        assertNull(result);
    }

    // ==================== insertUser Tests ====================

    @Test
    @DisplayName("新增用户 - 成功")
    void insertUser_success() {
        when(userMapper.checkUsernameUnique("newuser")).thenReturn(null);
        when(userMapper.checkPhoneUnique("13800138001")).thenReturn(null);
        when(userMapper.checkEmailUnique("newuser@example.com")).thenReturn(null);
        when(userMapper.insert(any(SysUser.class))).thenReturn(1);
        when(userMapper.batchUserRole(any(), anyList())).thenReturn(1);
        when(userMapper.batchUserPost(any(), anyList())).thenReturn(1);

        assertDoesNotThrow(() -> userService.insertUser(userDTO));

        verify(userMapper).insert(argThat((SysUser user) -> {
            assertEquals("newuser", user.getUsername());
            assertEquals("13800138001", user.getPhonenumber());
            assertEquals("0", user.getSex());
            assertNotNull(user.getPassword());
            assertTrue(user.getPassword().startsWith("$2a$"));
            return true;
        }));
    }

    @Test
    @DisplayName("新增用户 - 用户名已存在")
    void insertUser_duplicateUsername_shouldThrowException() {
        SysUser existing = new SysUser();
        existing.setUserId(99L);
        existing.setUsername("newuser");
        when(userMapper.checkUsernameUnique("newuser")).thenReturn(existing);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.insertUser(userDTO));

        assertTrue(exception.getMessage().contains("用户名已存在"));
    }

    @Test
    @DisplayName("新增用户 - 手机号已存在")
    void insertUser_duplicatePhone_shouldThrowException() {
        when(userMapper.checkUsernameUnique("newuser")).thenReturn(null);

        SysUser phoneUser = new SysUser();
        phoneUser.setUserId(88L);
        when(userMapper.checkPhoneUnique("13800138001")).thenReturn(phoneUser);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.insertUser(userDTO));

        assertTrue(exception.getMessage().contains("手机号已存在"));
    }

    @Test
    @DisplayName("新增用户 - 邮箱已存在")
    void insertUser_duplicateEmail_shouldThrowException() {
        when(userMapper.checkUsernameUnique("newuser")).thenReturn(null);
        when(userMapper.checkPhoneUnique("13800138001")).thenReturn(null);

        SysUser emailUser = new SysUser();
        emailUser.setUserId(77L);
        when(userMapper.checkEmailUnique("newuser@example.com")).thenReturn(emailUser);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.insertUser(userDTO));

        assertTrue(exception.getMessage().contains("邮箱已存在"));
    }

    @Test
    @DisplayName("新增用户 - 未提供密码使用默认密码")
    void insertUser_noPassword_shouldUseDefault() {
        userDTO.setPassword(null);
        when(userMapper.checkUsernameUnique("newuser")).thenReturn(null);
        when(userMapper.checkPhoneUnique("13800138001")).thenReturn(null);
        when(userMapper.checkEmailUnique("newuser@example.com")).thenReturn(null);
        when(userMapper.insert(any(SysUser.class))).thenReturn(1);
        when(userMapper.batchUserRole(any(), anyList())).thenReturn(1);
        when(userMapper.batchUserPost(any(), anyList())).thenReturn(1);

        assertDoesNotThrow(() -> userService.insertUser(userDTO));

        verify(userMapper).insert(argThat((SysUser user) -> {
            assertNotNull(user.getPassword());
            assertTrue(user.getPassword().startsWith("$2a$"));
            return true;
        }));
    }

    @Test
    @DisplayName("新增用户 - 无角色和岗位")
    void insertUser_noRolesOrPosts_shouldNotCallBatch() {
        userDTO.setRoleIds(null);
        userDTO.setPostIds(null);
        when(userMapper.checkUsernameUnique("newuser")).thenReturn(null);
        when(userMapper.checkPhoneUnique("13800138001")).thenReturn(null);
        when(userMapper.checkEmailUnique("newuser@example.com")).thenReturn(null);
        when(userMapper.insert(any(SysUser.class))).thenReturn(1);

        assertDoesNotThrow(() -> userService.insertUser(userDTO));

        verify(userMapper, never()).batchUserRole(any(), anyList());
        verify(userMapper, never()).batchUserPost(any(), anyList());
    }

    // ==================== updateUser Tests ====================

    @Test
    @DisplayName("修改用户 - 成功")
    void updateUser_success() {
        userDTO.setUserId(2L);
        when(dataScopeChecker.countUserInScope(any())).thenReturn(1L);
        when(userMapper.checkUsernameUnique("newuser")).thenReturn(null);
        when(userMapper.checkPhoneUnique("13800138001")).thenReturn(null);
        when(userMapper.checkEmailUnique("newuser@example.com")).thenReturn(null);
        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);
        when(userMapper.deleteUserRoleByUserId(2L)).thenReturn(1);
        when(userMapper.deleteUserPostByUserId(2L)).thenReturn(1);
        when(userMapper.batchUserRole(any(), anyList())).thenReturn(1);
        when(userMapper.batchUserPost(any(), anyList())).thenReturn(1);

        assertDoesNotThrow(() -> userService.updateUser(userDTO));

        verify(userMapper).updateById(argThat((SysUser user) -> {
            assertNull(user.getPassword(), "Password should be null during update");
            return true;
        }));
    }

    @Test
    @DisplayName("修改用户 - 用户ID为空")
    void updateUser_nullUserId_shouldThrowException() {
        userDTO.setUserId(null);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.updateUser(userDTO));

        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("修改用户 - 同一用户名不重复检测")
    void updateUser_sameUsername_shouldPass() {
        userDTO.setUserId(2L);
        userDTO.setUsername("admin");
        when(dataScopeChecker.countUserInScope(any())).thenReturn(1L);

        SysUser sameUser = new SysUser();
        sameUser.setUserId(2L); // same user ID
        sameUser.setUsername("admin");
        when(userMapper.checkUsernameUnique("admin")).thenReturn(sameUser);
        when(userMapper.checkPhoneUnique("13800138001")).thenReturn(null);
        when(userMapper.checkEmailUnique("newuser@example.com")).thenReturn(null);
        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);
        when(userMapper.deleteUserRoleByUserId(2L)).thenReturn(1);
        when(userMapper.deleteUserPostByUserId(2L)).thenReturn(1);
        when(userMapper.batchUserRole(any(), anyList())).thenReturn(1);
        when(userMapper.batchUserPost(any(), anyList())).thenReturn(1);

        assertDoesNotThrow(() -> userService.updateUser(userDTO));
    }

    // ==================== deleteUserById Tests ====================

    @Test
    @DisplayName("删除用户 - 成功(非管理员)")
    void deleteUserById_success() {
        when(dataScopeChecker.countUserInScope(any())).thenReturn(1L);
        when(userMapper.deleteUserRoleByUserId(2L)).thenReturn(1);
        when(userMapper.deleteUserPostByUserId(2L)).thenReturn(0);
        when(userMapper.deleteById(2L)).thenReturn(1);

        assertDoesNotThrow(() -> userService.deleteUserById(2L));

        verify(userMapper).deleteUserRoleByUserId(2L);
        verify(userMapper).deleteUserPostByUserId(2L);
        verify(userMapper).deleteById(2L);
    }

    @Test
    @DisplayName("删除用户 - 用户ID为空")
    void deleteUserById_nullUserId_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.deleteUserById(null));

        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("删除用户 - 超级管理员不允许删除")
    void deleteUserById_superAdmin_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.deleteUserById(1L));

        assertTrue(exception.getMessage().contains("超级管理员"));
    }

    // ==================== deleteUserByIds Tests ====================

    @Test
    @DisplayName("批量删除用户 - 成功(非管理员)")
    void deleteUserByIds_success() {
        Long[] userIds = {2L, 3L, 4L};
        when(dataScopeChecker.countUserInScope(any())).thenReturn(1L);
        when(userMapper.deleteUserRoleByUserId(anyLong())).thenReturn(1);
        when(userMapper.deleteUserPostByUserId(anyLong())).thenReturn(1);
        when(userMapper.deleteById(anyLong())).thenReturn(1);

        assertDoesNotThrow(() -> userService.deleteUserByIds(userIds));

        verify(userMapper, times(3)).deleteById(anyLong());
    }

    @Test
    @DisplayName("批量删除用户 - 包含管理员应抛异常")
    void deleteUserByIds_containsSuperAdmin_shouldThrowException() {
        Long[] userIds = {1L, 2L};

        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.deleteUserByIds(userIds));

        assertTrue(exception.getMessage().contains("超级管理员"));
    }

    @Test
    @DisplayName("批量删除用户 - 空数组")
    void deleteUserByIds_emptyArray_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.deleteUserByIds(new Long[]{}));

        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("批量删除用户 - null数组")
    void deleteUserByIds_null_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.deleteUserByIds(null));

        assertEquals("用户ID不能为空", exception.getMessage());
    }

    // ==================== resetPassword Tests ====================

    @Test
    @DisplayName("重置密码 - 成功")
    void resetPassword_success() {
        when(dataScopeChecker.countUserInScope(any())).thenReturn(1L);
        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

        assertDoesNotThrow(() -> userService.resetPassword(2L, "newPass123"));

        verify(userMapper).updateById(argThat((SysUser user) -> {
            assertEquals(2L, user.getUserId());
            assertNotNull(user.getPassword());
            assertTrue(user.getPassword().startsWith("$2a$"));
            return true;
        }));
    }

    @Test
    @DisplayName("重置密码 - 用户ID为空")
    void resetPassword_nullUserId_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.resetPassword(null, "newPass"));

        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("重置密码 - 新密码为空")
    void resetPassword_emptyPassword_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.resetPassword(1L, ""));

        assertEquals("新密码不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("重置密码 - 新密码为null")
    void resetPassword_nullPassword_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.resetPassword(1L, null));

        assertEquals("新密码不能为空", exception.getMessage());
    }

    // ==================== updateUserStatus Tests ====================

    @Test
    @DisplayName("修改用户状态 - 成功")
    void updateUserStatus_success() {
        when(dataScopeChecker.countUserInScope(any())).thenReturn(1L);
        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

        assertDoesNotThrow(() -> userService.updateUserStatus(2L, "1"));

        verify(userMapper).updateById(argThat((SysUser user) -> {
            assertEquals(2L, user.getUserId());
            assertEquals("1", user.getStatus());
            return true;
        }));
    }

    @Test
    @DisplayName("修改用户状态 - 用户ID为空")
    void updateUserStatus_nullUserId_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.updateUserStatus(null, "0"));

        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("修改用户状态 - 状态为空")
    void updateUserStatus_emptyStatus_shouldThrowException() {
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.updateUserStatus(1L, ""));

        assertEquals("用户状态不能为空", exception.getMessage());
    }

    // ==================== checkUsernameUnique Tests ====================

    @Test
    @DisplayName("用户名唯一校验 - 无重复返回true")
    void checkUsernameUnique_noDuplicate_shouldReturnTrue() {
        when(userMapper.checkUsernameUnique("newuser")).thenReturn(null);

        assertTrue(userService.checkUsernameUnique("newuser", null));
    }

    @Test
    @DisplayName("用户名唯一校验 - 同一用户返回true")
    void checkUsernameUnique_sameUser_shouldReturnTrue() {
        existingUser.setUserId(1L);
        when(userMapper.checkUsernameUnique("admin")).thenReturn(existingUser);

        assertTrue(userService.checkUsernameUnique("admin", 1L));
    }

    @Test
    @DisplayName("用户名唯一校验 - 不同用户已存在返回false")
    void checkUsernameUnique_differentUser_shouldReturnFalse() {
        existingUser.setUserId(2L);
        when(userMapper.checkUsernameUnique("admin")).thenReturn(existingUser);

        assertFalse(userService.checkUsernameUnique("admin", 1L));
    }

    // ==================== checkPhoneUnique Tests ====================

    @Test
    @DisplayName("手机号唯一校验 - 无重复返回true")
    void checkPhoneUnique_noDuplicate_shouldReturnTrue() {
        when(userMapper.checkPhoneUnique("13800138000")).thenReturn(null);

        assertTrue(userService.checkPhoneUnique("13800138000", null));
    }

    @Test
    @DisplayName("手机号唯一校验 - 不同用户已存在返回false")
    void checkPhoneUnique_differentUser_shouldReturnFalse() {
        SysUser phoneUser = new SysUser();
        phoneUser.setUserId(2L);
        when(userMapper.checkPhoneUnique("13800138000")).thenReturn(phoneUser);

        assertFalse(userService.checkPhoneUnique("13800138000", 1L));
    }

    // ==================== checkEmailUnique Tests ====================

    @Test
    @DisplayName("邮箱唯一校验 - 无重复返回true")
    void checkEmailUnique_noDuplicate_shouldReturnTrue() {
        when(userMapper.checkEmailUnique("test@example.com")).thenReturn(null);

        assertTrue(userService.checkEmailUnique("test@example.com", null));
    }

    @Test
    @DisplayName("邮箱唯一校验 - 不同用户已存在返回false")
    void checkEmailUnique_differentUser_shouldReturnFalse() {
        SysUser emailUser = new SysUser();
        emailUser.setUserId(2L);
        when(userMapper.checkEmailUnique("test@example.com")).thenReturn(emailUser);

        assertFalse(userService.checkEmailUnique("test@example.com", 1L));
    }

    // ==================== selectPostIdsByUserId / selectRoleIdsByUserId Tests ====================

    @Test
    @DisplayName("查询用户岗位ID列表")
    void selectPostIdsByUserId_shouldReturnList() {
        List<Long> postIds = Arrays.asList(1L, 2L);
        when(userMapper.selectPostIdsByUserId(1L)).thenReturn(postIds);

        List<Long> result = userService.selectPostIdsByUserId(1L);

        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
    }

    @Test
    @DisplayName("查询用户角色ID列表")
    void selectRoleIdsByUserId_shouldReturnList() {
        List<Long> roleIds = Arrays.asList(1L, 3L);
        when(userMapper.selectRoleIdsByUserId(1L)).thenReturn(roleIds);

        List<Long> result = userService.selectRoleIdsByUserId(1L);

        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(3L));
    }

    // ==================== 越权防护 Tests（垂直 + 水平）====================

    @Test
    @DisplayName("垂直越权防护 - 重置超级管理员密码被拒")
    void resetPassword_superAdmin_shouldThrow() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.resetPassword(1L, "newPass123"));
        assertTrue(ex.getMessage().contains("超级管理员"));
    }

    @Test
    @DisplayName("垂直越权防护 - 停用超级管理员被拒")
    void updateUserStatus_superAdmin_shouldThrow() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.updateUserStatus(1L, "1"));
        assertTrue(ex.getMessage().contains("超级管理员"));
    }

    @Test
    @DisplayName("垂直越权防护 - 修改超级管理员被拒")
    void updateUser_superAdmin_shouldThrow() {
        userDTO.setUserId(1L);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.updateUser(userDTO));
        assertTrue(ex.getMessage().contains("超级管理员"));
    }

    @Test
    @DisplayName("水平越权防护 - 目标用户不在数据范围内则拒绝重置密码")
    void resetPassword_outOfScope_shouldThrow() {
        when(dataScopeChecker.countUserInScope(any())).thenReturn(0L);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.resetPassword(2L, "newPass123"));
        assertTrue(ex.getMessage().contains("权限"));
    }

    @Test
    @DisplayName("水平越权防护 - 目标用户不在数据范围内则拒绝查看详情")
    void selectUserById_outOfScope_shouldThrow() {
        when(dataScopeChecker.countUserInScope(any())).thenReturn(0L);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.selectUserById(2L));
        assertTrue(ex.getMessage().contains("权限"));
    }

    // ==================== current user profile Tests ====================

    @Test
    @DisplayName("current profile uses authenticated user id")
    void selectCurrentUserProfile_shouldUseAuthenticatedUserId() {
        setAuthentication(7L, "profileUser");
        UserVO profile = new UserVO();
        profile.setUserId(7L);
        profile.setUsername("profileUser");
        when(dataScopeChecker.countUserInScope(any())).thenReturn(1L);
        when(userMapper.selectUserVOById(7L)).thenReturn(profile);

        UserVO result = userService.selectCurrentUserProfile();

        assertEquals(7L, result.getUserId());
        verify(userMapper).selectUserVOById(7L);
    }

    @Test
    @DisplayName("current profile update only writes allowed fields")
    void updateCurrentUserProfile_shouldOnlyUpdateAllowedFieldsForAuthenticatedUser() {
        setAuthentication(7L, "profileUser");
        UserDTO dto = new UserDTO();
        dto.setUserId(99L);
        dto.setUsername("attacker");
        dto.setNickname("New Nick");
        dto.setEmail("new@example.com");
        dto.setPhone("13800138002");
        dto.setGender("1");
        dto.setStatus("1");
        dto.setRoleIds(new Long[]{1L});
        dto.setPostIds(new Long[]{2L});

        when(userMapper.checkPhoneUnique("13800138002")).thenReturn(null);
        when(userMapper.checkEmailUnique("new@example.com")).thenReturn(null);
        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

        userService.updateCurrentUserProfile(dto);

        verify(userMapper).updateById(argThat((SysUser user) -> {
            assertEquals(7L, user.getUserId());
            assertNull(user.getUsername());
            assertEquals("New Nick", user.getNickname());
            assertEquals("new@example.com", user.getEmail());
            assertEquals("13800138002", user.getPhonenumber());
            assertEquals("1", user.getSex());
            assertNull(user.getStatus());
            assertNull(user.getRoleIds());
            assertNull(user.getPostIds());
            assertNull(user.getPassword());
            return true;
        }));
    }

    @Test
    @DisplayName("current profile update rejects duplicate phone")
    void updateCurrentUserProfile_duplicatePhoneShouldThrow() {
        setAuthentication(7L, "profileUser");
        UserDTO dto = new UserDTO();
        dto.setNickname("New Nick");
        dto.setPhone("13800138002");

        SysUser other = new SysUser();
        other.setUserId(8L);
        when(userMapper.checkPhoneUnique("13800138002")).thenReturn(other);

        assertThrows(ServiceException.class, () -> userService.updateCurrentUserProfile(dto));
        verify(userMapper, never()).updateById(any(SysUser.class));
    }

    @Test
    @DisplayName("current password update rejects wrong old password")
    void updateCurrentUserPassword_wrongOldPasswordShouldThrow() {
        setAuthentication(7L, "profileUser");
        SysUser current = new SysUser();
        current.setUserId(7L);
        current.setPassword(SecurityUtils.encryptPassword("correctPass"));
        when(userMapper.selectById(7L)).thenReturn(current);

        assertThrows(ServiceException.class,
                () -> userService.updateCurrentUserPassword("wrongPass", "newPass123"));

        verify(userMapper, never()).updateById(any(SysUser.class));
    }

    @Test
    @DisplayName("current password update rejects short new password")
    void updateCurrentUserPassword_shortNewPasswordShouldThrow() {
        setAuthentication(7L, "profileUser");

        assertThrows(ServiceException.class,
                () -> userService.updateCurrentUserPassword("oldPass123", "short"));

        verify(userMapper, never()).selectById(anyLong());
        verify(userMapper, never()).updateById(any(SysUser.class));
    }

    @Test
    @DisplayName("current password update stores new hash")
    void updateCurrentUserPassword_successShouldStoreHash() {
        setAuthentication(7L, "profileUser");
        SysUser current = new SysUser();
        current.setUserId(7L);
        current.setPassword(SecurityUtils.encryptPassword("oldPass123"));
        when(userMapper.selectById(7L)).thenReturn(current);
        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

        userService.updateCurrentUserPassword("oldPass123", "newPass123");

        verify(userMapper).updateById(argThat((SysUser user) -> {
            assertEquals(7L, user.getUserId());
            assertNotNull(user.getPassword());
            assertTrue(SecurityUtils.matchesPassword("newPass123", user.getPassword()));
            return true;
        }));
    }

    private void setAuthentication(Long userId, String username) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUsername(username);
        user.setStatus("0");
        LoginUser loginUser = new LoginUser(user, Collections.emptySet());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
