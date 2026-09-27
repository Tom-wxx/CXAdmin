package com.admin.system.service;

import com.admin.system.service.impl.DashboardServiceImpl;
import com.admin.system.vo.DashboardVO;
import com.admin.system.vo.StatisticsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
    private ISysOperLogService operLogService;

    @Mock
    private ISysNotificationService notificationService;

    @Mock
    private IStatisticsService statisticsService;

    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardServiceImpl(
                userService,
                roleService,
                noticeService,
                onlineUserService,
                operLogService,
                notificationService,
                statisticsService
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

    @Test
    @DisplayName("用户趋势 - 复用统计服务的 7 天聚合结果，标签为 MM-dd，不逐天查库")
    void getUserTrend_shouldReuseStatisticsAggregation() {
        when(statisticsService.getUserGrowthTrend(7)).thenReturn(List.of(
                daily("2026-09-26", 1L), daily("2026-09-27", 3L)));

        DashboardVO.ChartData result = dashboardService.getUserTrend();

        assertEquals(List.of("09-26", "09-27"), result.getLabels());
        assertEquals(List.of(1L, 3L), result.getValues());
        verifyNoInteractions(userService);
    }

    private StatisticsVO daily(String date, Long value) {
        StatisticsVO vo = new StatisticsVO();
        vo.setDate(date);
        vo.setValue(value);
        return vo;
    }
}
