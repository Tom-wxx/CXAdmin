package com.admin.system.security;

import com.admin.common.config.JwtProperties;
import com.admin.common.constants.SystemConstants;
import com.admin.common.utils.RedisUtil;
import com.admin.system.entity.SysUser;
import com.admin.system.mapper.SysMenuMapper;
import com.admin.system.mapper.SysUserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@DisplayName("LoginSessionManager 会话管理测试")
@ExtendWith(MockitoExtension.class)
class LoginSessionManagerTest {

    private static final String INDEX_KEY = SystemConstants.LOGIN_USER_TOKENS_KEY + "7";

    @InjectMocks
    private LoginSessionManager sessionManager;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SysMenuMapper menuMapper;

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    @DisplayName("保存会话 - 写入会话并登记用户索引，索引 TTL 与会话一致")
    void save_shouldWriteSessionAndIndex() {
        when(jwtProperties.getExpireTime()).thenReturn(30);
        LoginUser loginUser = loginUser("tok-a");

        sessionManager.save(loginUser);

        verify(redisUtil).set(SystemConstants.LOGIN_TOKEN_KEY + "tok-a", loginUser, 30L, TimeUnit.MINUTES);
        verify(redisUtil).sAdd(INDEX_KEY, "tok-a");
        verify(redisUtil).expire(INDEX_KEY, 30L, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("按用户踢下线 - 保留指定的当前会话")
    void removeByUserId_shouldKeepCurrentToken() {
        when(redisUtil.sMembers(INDEX_KEY)).thenReturn(Set.of("tok-a", "tok-b"));

        sessionManager.removeByUserId(7L, "tok-a");

        verify(redisUtil).delete(List.of(SystemConstants.LOGIN_TOKEN_KEY + "tok-b"));
        verify(redisUtil).sRemove(INDEX_KEY, "tok-b");
    }

    @Test
    @DisplayName("同步会话 - 用户已停用则全部踢下线")
    void syncByUserId_disabledUser_shouldRemoveAllSessions() {
        when(redisUtil.sMembers(INDEX_KEY)).thenReturn(Set.of("tok-a"));
        SysUser disabled = user();
        disabled.setStatus(SystemConstants.STATUS_DISABLE);
        when(userMapper.selectById(7L)).thenReturn(disabled);

        sessionManager.syncByUserId(7L);

        verify(redisUtil).delete(List.of(SystemConstants.LOGIN_TOKEN_KEY + "tok-a"));
        verify(menuMapper, never()).selectMenuPermsByUserId(any());
    }

    @Test
    @DisplayName("同步会话 - 用户已删除（逻辑删除查不到）则全部踢下线")
    void syncByUserId_deletedUser_shouldRemoveAllSessions() {
        when(redisUtil.sMembers(INDEX_KEY)).thenReturn(Set.of("tok-a"));
        when(userMapper.selectById(7L)).thenReturn(null);

        sessionManager.syncByUserId(7L);

        verify(redisUtil).delete(List.of(SystemConstants.LOGIN_TOKEN_KEY + "tok-a"));
    }

    @Test
    @DisplayName("同步会话 - 正常用户刷新权限，保留剩余有效期且不带密码")
    void syncByUserId_activeUser_shouldRefreshPermissionsKeepingTtl() {
        String key = SystemConstants.LOGIN_TOKEN_KEY + "tok-a";
        when(redisUtil.sMembers(INDEX_KEY)).thenReturn(Set.of("tok-a"));
        SysUser fresh = user();
        fresh.setDeptId(200L);
        fresh.setPassword("$2a$hash");
        when(userMapper.selectById(7L)).thenReturn(fresh);
        when(menuMapper.selectMenuPermsByUserId(7L)).thenReturn(Set.of("system:role:list"));
        LoginUser session = loginUser("tok-a");
        when(redisUtil.get(key)).thenReturn(session);
        when(redisUtil.getExpire(key)).thenReturn(600L);

        sessionManager.syncByUserId(7L);

        verify(redisUtil).set(eq(key), argThat(value -> {
            LoginUser u = (LoginUser) value;
            assertEquals(Set.of("system:role:list"), u.getPermissions());
            assertEquals(200L, u.getDeptId());
            assertNull(u.getUser().getPassword());
            return true;
        }), eq(600L), eq(TimeUnit.SECONDS));
        verify(redisUtil, never()).delete(anyCollection());
    }

    @Test
    @DisplayName("同步会话 - 索引中已过期的 token 被清理")
    void syncByUserId_expiredToken_shouldBePrunedFromIndex() {
        when(redisUtil.sMembers(INDEX_KEY)).thenReturn(Set.of("tok-gone"));
        when(userMapper.selectById(7L)).thenReturn(user());
        when(redisUtil.get(SystemConstants.LOGIN_TOKEN_KEY + "tok-gone")).thenReturn(null);

        sessionManager.syncByUserId(7L);

        verify(redisUtil).sRemove(INDEX_KEY, "tok-gone");
    }

    @Test
    @DisplayName("按角色同步 - 逐个同步该角色下的用户")
    void syncByRoleId_shouldSyncEveryUserOfRole() {
        when(userMapper.selectUserIdsByRoleId(3L)).thenReturn(List.of(7L, 8L));

        sessionManager.syncByRoleId(3L);

        verify(redisUtil).sMembers(INDEX_KEY);
        verify(redisUtil).sMembers(SystemConstants.LOGIN_USER_TOKENS_KEY + "8");
    }

    @Test
    @DisplayName("事务中 - 失效动作推迟到提交之后执行")
    void removeByUserId_inTransaction_shouldRunAfterCommit() {
        TransactionSynchronizationManager.initSynchronization();

        sessionManager.removeByUserId(7L);

        verifyNoInteractions(redisUtil);
        List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
        assertEquals(1, synchronizations.size());

        when(redisUtil.sMembers(INDEX_KEY)).thenReturn(Set.of("tok-a"));
        synchronizations.forEach(TransactionSynchronization::afterCommit);

        verify(redisUtil).delete(List.of(SystemConstants.LOGIN_TOKEN_KEY + "tok-a"));
    }

    @Test
    @DisplayName("同步失败 - 只记录错误，不向业务调用方抛出")
    void syncByUserId_redisFailure_shouldNotPropagate() {
        when(redisUtil.sMembers(INDEX_KEY)).thenThrow(new RuntimeException("Redis down"));

        // 业务数据已提交，会话同步失败只记日志，不能让调用方收到异常
        assertDoesNotThrow(() -> sessionManager.syncByUserId(7L));
    }

    private SysUser user() {
        SysUser user = new SysUser();
        user.setUserId(7L);
        user.setUsername("u7");
        user.setStatus(SystemConstants.STATUS_NORMAL);
        return user;
    }

    private LoginUser loginUser(String token) {
        LoginUser loginUser = new LoginUser(user(), Set.of("system:user:list"));
        loginUser.setToken(token);
        return loginUser;
    }
}
