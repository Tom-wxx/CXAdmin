package com.admin.system.service;

import com.admin.system.mapper.SysDeptMapper;
import com.admin.system.mapper.SysLoginLogMapper;
import com.admin.system.mapper.SysNotificationMapper;
import com.admin.system.mapper.SysOperLogMapper;
import com.admin.system.mapper.SysRoleMapper;
import com.admin.system.mapper.SysUserMapper;
import com.admin.system.service.impl.StatisticsServiceImpl;
import com.admin.system.vo.SystemOverviewVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("StatisticsServiceImpl 统计服务测试")
@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SysDeptMapper deptMapper;

    @Mock
    private SysLoginLogMapper loginLogMapper;

    @Mock
    private SysOperLogMapper operLogMapper;

    @Mock
    private SysNotificationMapper notificationMapper;

    @Mock
    private IOnlineUserService onlineUserService;

    @Test
    @DisplayName("系统概览 - 在线人数来自在线用户服务")
    void getSystemOverview_shouldUseOnlineUserServiceCount() {
        when(userMapper.selectCount(any())).thenReturn(10L, 2L);
        when(roleMapper.selectCount(any())).thenReturn(3L);
        when(deptMapper.selectCount(any())).thenReturn(4L);
        when(loginLogMapper.selectCount(any())).thenReturn(5L);
        when(operLogMapper.selectCount(any())).thenReturn(6L);
        // notificationMapper 为可选字段（impl 有 null 检查）；@InjectMocks 构造器注入后不再字段注入它，
        // 此测试只验证在线人数来源，故该 stub 可能未被使用 —— 用 lenient 避免 UnnecessaryStubbing。
        lenient().when(notificationMapper.selectCount(any())).thenReturn(7L, 8L);
        when(onlineUserService.countOnlineUsers()).thenReturn(9L);

        SystemOverviewVO result = statisticsService.getSystemOverview();

        assertEquals(9L, result.getOnlineUserCount());
        verify(onlineUserService).countOnlineUsers();
    }
}
