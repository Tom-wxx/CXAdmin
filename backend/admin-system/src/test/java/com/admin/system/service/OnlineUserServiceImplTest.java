package com.admin.system.service;

import com.admin.common.constants.SystemConstants;
import com.admin.system.entity.SysDept;
import com.admin.system.entity.SysUser;
import com.admin.system.security.LoginUser;
import com.admin.system.service.impl.OnlineUserServiceImpl;
import com.admin.common.utils.RedisUtil;
import com.admin.system.vo.OnlineUserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("OnlineUserServiceImpl 在线用户服务测试")
@ExtendWith(MockitoExtension.class)
class OnlineUserServiceImplTest {

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private ISysDeptService deptService;

    private OnlineUserServiceImpl onlineUserService;

    @BeforeEach
    void setUp() {
        onlineUserService = new OnlineUserServiceImpl(redisUtil, deptService);
    }

    @Test
    @DisplayName("统计在线用户数 - 只统计有效登录用户会话")
    void countOnlineUsers_shouldCountOnlyLoginUserSessions() {
        String tokenA = SystemConstants.LOGIN_TOKEN_KEY + "token-a";
        String tokenB = SystemConstants.LOGIN_TOKEN_KEY + "token-b";
        String tokenC = SystemConstants.LOGIN_TOKEN_KEY + "token-c";
        when(redisUtil.scanKeys(SystemConstants.LOGIN_TOKEN_KEY + "*"))
                .thenReturn(new LinkedHashSet<>(List.of(tokenA, tokenB, tokenC)));
        when(redisUtil.multiGet(anyList()))
                .thenReturn(Arrays.asList(loginUser(1L, "admin", null), "stale-session", loginUser(2L, "manager", null)));

        long result = onlineUserService.countOnlineUsers();

        assertEquals(2L, result);
    }

    @Test
    @DisplayName("在线用户列表 - 部门名称一次批量查询，不逐用户查询")
    void selectOnlineUserList_shouldLoadDeptNamesInOneQuery() {
        String tokenA = SystemConstants.LOGIN_TOKEN_KEY + "token-a";
        String tokenB = SystemConstants.LOGIN_TOKEN_KEY + "token-b";
        when(redisUtil.scanKeys(SystemConstants.LOGIN_TOKEN_KEY + "*"))
                .thenReturn(new LinkedHashSet<>(List.of(tokenA, tokenB)));
        when(redisUtil.multiGet(anyList()))
                .thenReturn(Arrays.asList(loginUser(1L, "admin", 10L), loginUser(2L, "manager", 10L)));
        SysDept dept = new SysDept();
        dept.setDeptId(10L);
        dept.setDeptName("研发部");
        when(deptService.listByIds(anyCollection())).thenReturn(List.of(dept));

        List<OnlineUserVO> result = onlineUserService.selectOnlineUserList(null, null);

        assertEquals(2, result.size());
        assertEquals("研发部", result.get(0).getDeptName());
        assertEquals("研发部", result.get(1).getDeptName());
        verify(deptService, times(1)).listByIds(anyCollection());
    }

    private LoginUser loginUser(Long userId, String username, Long deptId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUsername(username);
        user.setNickname(username);
        user.setDeptId(deptId);
        user.setStatus("0");
        LoginUser loginUser = new LoginUser(user, Set.of("system:user:list"));
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(System.currentTimeMillis() + 30 * 60 * 1000);
        return loginUser;
    }
}
