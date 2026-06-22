package com.admin.system.service.impl;

import com.admin.system.entity.SysOperLog;
import com.admin.system.service.*;
import com.admin.system.vo.DashboardVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仪表板服务实现
 *
 * @author Admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final ISysUserService userService;
    private final ISysRoleService roleService;
    private final ISysNoticeService noticeService;
    private final IOnlineUserService onlineUserService;
    private final ISysDeptService deptService;
    private final ISysOperLogService operLogService;
    private final ISysLoginLogService loginLogService;

    @Override
    public DashboardVO getDashboardData() {
        DashboardVO dashboard = new DashboardVO();

        // 获取统计卡片数据
        dashboard.setStatCard(getStatCard());

        // 获取用户增长趋势
        dashboard.setUserTrend(getUserTrend());

        // 获取登录统计
        dashboard.setLoginStats(getLoginStats());

        // 获取部门人员分布
        dashboard.setDeptDistribution(getDeptDistribution());

        // 获取近期操作日志（最近10条）
        dashboard.setRecentLogs(getRecentLogs());

        // 获取快捷访问数据
        dashboard.setQuickAccess(getQuickAccess());

        return dashboard;
    }

    @Override
    public DashboardVO.StatCard getStatCard() {
        DashboardVO.StatCard statCard = new DashboardVO.StatCard();

        // 用户总数
        long totalUsers = userService.count();
        statCard.setTotalUsers(totalUsers);

        // 今日新增用户
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long todayUsers = userService.count(
            new LambdaQueryWrapper<com.admin.system.entity.SysUser>()
                .ge(com.admin.system.entity.SysUser::getCreateTime, startOfDay)
        );
        statCard.setTodayUsers(todayUsers);

        // 角色总数
        long totalRoles = roleService.count();
        statCard.setTotalRoles(totalRoles);

        // 在线用户数
        long onlineUsers = onlineUserService.selectOnlineUserList(null, null).size();
        statCard.setOnlineUsers(onlineUsers);

        // 通知公告数
        long totalNotices = noticeService.count();
        statCard.setTotalNotices(totalNotices);

        // 待办任务数（暂时设为0，后续可以扩展）
        statCard.setPendingTasks(0L);

        return statCard;
    }

    @Override
    public DashboardVO.ChartData getUserTrend() {
        DashboardVO.ChartData chartData = new DashboardVO.ChartData();

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        // 统计最近7天的用户注册数
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            labels.add(date.format(formatter));

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            long count = userService.count(
                new LambdaQueryWrapper<com.admin.system.entity.SysUser>()
                    .ge(com.admin.system.entity.SysUser::getCreateTime, startOfDay)
                    .lt(com.admin.system.entity.SysUser::getCreateTime, endOfDay)
            );
            values.add(count);
        }

        chartData.setLabels(labels);
        chartData.setValues(values);

        return chartData;
    }

    @Override
    public DashboardVO.ChartData getLoginStats() {
        DashboardVO.ChartData chartData = new DashboardVO.ChartData();

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        // 统计最近7天的登录次数
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            labels.add(date.format(formatter));

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            long count = loginLogService.count(
                new LambdaQueryWrapper<com.admin.system.entity.SysLoginLog>()
                    .ge(com.admin.system.entity.SysLoginLog::getLoginTime, startOfDay)
                    .lt(com.admin.system.entity.SysLoginLog::getLoginTime, endOfDay)
            );
            values.add(count);
        }

        chartData.setLabels(labels);
        chartData.setValues(values);

        return chartData;
    }

    @Override
    public DashboardVO.ChartData getDeptDistribution() {
        DashboardVO.ChartData chartData = new DashboardVO.ChartData();

        // 获取所有部门
        List<com.admin.system.entity.SysDept> deptList = deptService.list();

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        // 统计每个部门的人数
        for (com.admin.system.entity.SysDept dept : deptList) {
            long count = userService.count(
                new LambdaQueryWrapper<com.admin.system.entity.SysUser>()
                    .eq(com.admin.system.entity.SysUser::getDeptId, dept.getDeptId())
            );
            if (count > 0) {
                labels.add(dept.getDeptName());
                values.add(count);
            }
        }

        chartData.setLabels(labels);
        chartData.setValues(values);

        return chartData;
    }

    /**
     * 获取近期操作日志
     */
    private List<DashboardVO.RecentLog> getRecentLogs() {
        List<DashboardVO.RecentLog> recentLogs = new ArrayList<>();

        // 查询最近10条操作日志
        List<SysOperLog> logs = operLogService.list(
            new LambdaQueryWrapper<SysOperLog>()
                .orderByDesc(SysOperLog::getOperTime)
                .last("LIMIT 10")
        );

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SysOperLog log : logs) {
            DashboardVO.RecentLog recentLog = new DashboardVO.RecentLog();
            recentLog.setOperator(log.getOperName());
            recentLog.setOperType(getOperTypeName(log.getBusinessType()));
            recentLog.setOperContent(log.getTitle());
            recentLog.setOperTime(log.getOperTime() != null ?
                sdf.format(log.getOperTime()) : "");
            recentLog.setStatus(log.getStatus() == 0 ? "成功" : "失败");
            recentLogs.add(recentLog);
        }

        return recentLogs;
    }

    /**
     * 获取快捷访问数据
     */
    private DashboardVO.QuickAccess getQuickAccess() {
        DashboardVO.QuickAccess quickAccess = new DashboardVO.QuickAccess();

        // 这些数据可以根据实际业务需求扩展
        quickAccess.setPendingApprovals(0L);
        quickAccess.setPendingMessages(0L);
        quickAccess.setSystemAlerts(0L);

        return quickAccess;
    }

    /**
     * 获取操作类型名称
     */
    private String getOperTypeName(Integer businessType) {
        if (businessType == null) {
            return "其他";
        }
        switch (businessType) {
            case 0: return "其他";
            case 1: return "新增";
            case 2: return "修改";
            case 3: return "删除";
            case 4: return "授权";
            case 5: return "导出";
            case 6: return "导入";
            case 7: return "强退";
            case 8: return "清空";
            default: return "未知";
        }
    }
}
