package com.admin.system.service;

import com.admin.system.service.impl.DashboardServiceImpl;
import com.admin.system.vo.DashboardVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("DashboardServiceImpl 仪表盘服务测试")
@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private ISysUserService userService;

    @Mock
    private ISysRoleService roleService;

    @Mock
    private ISysNoticeService noticeService;

    @Mock
    private IOnlineUserService onlineUserService;

    @Mock
    private ISysDeptService deptService;

    @Mock
    private ISysOperLogService operLogService;

    @Mock
    private ISysLoginLogService loginLogService;

    @Mock
    private ISysNotificationService notificationService;

    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardServiceImpl(
                userService,
                roleService,
                noticeService,
                onlineUserService,
                deptService,
                operLogService,
                loginLogService,
                notificationService
        );
    }

    @Test
    @DisplayName("统计卡片 - 待办数来自站内通知待办口径")
    void getStatCard_shouldUseNotificationPendingTaskCount() {
        when(userService.count()).thenReturn(10L);
        when(userService.count(any(LambdaQueryWrapper.class))).thenReturn(2L);
        when(roleService.count()).thenReturn(3L);
        when(onlineUserService.countOnlineUsers()).thenReturn(4L);
        when(noticeService.count()).thenReturn(5L);
        when(notificationService.countPendingTasks()).thenReturn(6L);

        DashboardVO.StatCard result = dashboardService.getStatCard();

        assertEquals(4L, result.getOnlineUsers());
        assertEquals(6L, result.getPendingTasks());
        verify(onlineUserService).countOnlineUsers();
        verify(notificationService).countPendingTasks();
    }
}
