package com.admin.system.service;

import com.admin.common.constants.SystemConstants;
import com.admin.system.entity.SysUser;
import com.admin.system.security.LoginUser;
import com.admin.system.service.impl.OnlineUserServiceImpl;
import com.admin.common.utils.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@DisplayName("OnlineUserServiceImpl 在线用户服务测试")
@ExtendWith(MockitoExtension.class)
class OnlineUserServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private ISysDeptService deptService;

    private OnlineUserServiceImpl onlineUserService;

    @BeforeEach
    void setUp() {
        onlineUserService = new OnlineUserServiceImpl(redisTemplate, redisUtil, deptService);
    }

    @Test
    @DisplayName("统计在线用户数 - 只统计有效登录用户会话")
    void countOnlineUsers_shouldCountOnlyLoginUserSessions() {
        String tokenA = SystemConstants.LOGIN_TOKEN_KEY + "token-a";
        String tokenB = SystemConstants.LOGIN_TOKEN_KEY + "token-b";
        String tokenC = SystemConstants.LOGIN_TOKEN_KEY + "token-c";
        when(redisTemplate.keys(SystemConstants.LOGIN_TOKEN_KEY + "*"))
                .thenReturn(Set.of(tokenA, tokenB, tokenC));
        when(redisUtil.get(tokenA)).thenReturn(loginUser(1L, "admin"));
        when(redisUtil.get(tokenB)).thenReturn("stale-session");
        when(redisUtil.get(tokenC)).thenReturn(loginUser(2L, "manager"));

        long result = onlineUserService.countOnlineUsers();

        assertEquals(2L, result);
    }

    private LoginUser loginUser(Long userId, String username) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUsername(username);
        user.setNickname(username);
        user.setStatus("0");
        LoginUser loginUser = new LoginUser(user, Set.of("system:user:list"));
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(System.currentTimeMillis() + 30 * 60 * 1000);
        return loginUser;
    }
}
