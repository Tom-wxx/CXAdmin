package com.admin.system.service;

import com.admin.system.entity.SysRole;
import com.admin.system.mapper.SysDeptMapper;
import com.admin.system.mapper.SysLoginLogMapper;
import com.admin.system.mapper.SysNotificationMapper;
import com.admin.system.mapper.SysOperLogMapper;
import com.admin.system.mapper.SysRoleMapper;
import com.admin.system.mapper.SysUserMapper;
import com.admin.system.service.impl.StatisticsServiceImpl;
import com.admin.system.vo.GroupCountVO;
import com.admin.system.vo.StatisticsVO;
import com.admin.system.vo.SystemOverviewVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
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

    @Test
    @DisplayName("按天趋势 - 一次聚合查询，缺失日期补 0，窗口从 N-1 天前零点开始")
    void getLoginStatistics_shouldAggregateOnceAndZeroFill() {
        LocalDate today = LocalDate.now();
        when(loginLogMapper.countByDay(any())).thenReturn(List.of(row(today.toString(), 5L)));

        List<StatisticsVO> result = statisticsService.getLoginStatistics(3);

        assertEquals(3, result.size());
        assertEquals(today.minusDays(2).toString(), result.get(0).getDate());
        assertEquals(0L, result.get(0).getValue());
        assertEquals(0L, result.get(1).getValue());
        assertEquals(5L, result.get(2).getValue());
        verify(loginLogMapper).countByDay(today.minusDays(2).atStartOfDay());
        verify(loginLogMapper, never()).selectCount(any());
    }

    @Test
    @DisplayName("角色分布 - 分组计数按角色映射，0 人的角色不展示")
    void getRoleUserDistribution_shouldMapGroupedCounts() {
        when(roleMapper.selectList(any())).thenReturn(List.of(role(1L, "管理员"), role(2L, "访客")));
        when(userMapper.countUsersGroupByRole()).thenReturn(List.of(row("1", 4L)));

        List<StatisticsVO> result = statisticsService.getRoleUserDistribution();

        assertEquals(1, result.size());
        assertEquals("管理员", result.get(0).getName());
        assertEquals(4L, result.get(0).getValue());
        verify(userMapper, never()).countUsersByRoleId(any());
    }

    @Test
    @DisplayName("操作类型分布 - 数据库分组计数，不把日志读进内存")
    void getOperationTypeStatistics_shouldUseGroupedQuery() {
        when(operLogMapper.countByBusinessType(any())).thenReturn(List.of(row("1", 7L), row(null, 2L)));

        List<StatisticsVO> result = statisticsService.getOperationTypeStatistics();

        assertEquals(2, result.size());
        assertEquals("新增", result.get(0).getName());
        assertEquals("其他", result.get(1).getName());
        verify(operLogMapper, never()).selectList(any());
    }

    private GroupCountVO row(String key, Long total) {
        GroupCountVO row = new GroupCountVO();
        row.setGroupKey(key);
        row.setTotal(total);
        return row;
    }

    private SysRole role(Long id, String name) {
        SysRole role = new SysRole();
        role.setRoleId(id);
        role.setRoleName(name);
        return role;
    }
}
