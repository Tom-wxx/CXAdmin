package com.admin.system.service.impl;

import com.admin.system.entity.*;
import com.admin.system.mapper.*;
import com.admin.system.service.IStatisticsService;
import com.admin.system.vo.StatisticsVO;
import com.admin.system.vo.SystemOverviewVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements IStatisticsService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysDeptMapper deptMapper;
    private final SysLoginLogMapper loginLogMapper;
    private final SysOperLogMapper operLogMapper;

    @Autowired(required = false)
    private SysNotificationMapper notificationMapper;

    @Override
    public SystemOverviewVO getSystemOverview() {
        SystemOverviewVO overview = new SystemOverviewVO();

        // 用户总数
        Long totalUsers = userMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeleted, 0));
        overview.setTotalUsers(totalUsers);

        // 今日新增用户
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        Long todayNewUsers = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeleted, 0)
                        .ge(SysUser::getCreateTime, todayStart)
        );
        overview.setTodayNewUsers(todayNewUsers);

        // 角色总数
        Long totalRoles = roleMapper.selectCount(new LambdaQueryWrapper<SysRole>().eq(SysRole::getDeleted, 0));
        overview.setTotalRoles(totalRoles);

        // 部门总数
        Long totalDepts = deptMapper.selectCount(new LambdaQueryWrapper<SysDept>().eq(SysDept::getDeleted, 0));
        overview.setTotalDepts(totalDepts);

        // 今日登录次数
        Long todayLoginCount = loginLogMapper.selectCount(
                new LambdaQueryWrapper<SysLoginLog>()
                        .ge(SysLoginLog::getLoginTime, todayStart)
        );
        overview.setTodayLoginCount(todayLoginCount);

        // 今日操作次数
        Long todayOperationCount = operLogMapper.selectCount(
                new LambdaQueryWrapper<SysOperLog>()
                        .ge(SysOperLog::getOperTime, todayStart)
        );
        overview.setTodayOperationCount(todayOperationCount);

        // 通知统计（如果通知模块存在）
        if (notificationMapper != null) {
            Long totalNotifications = notificationMapper.selectCount(
                    new LambdaQueryWrapper<SysNotification>().eq(SysNotification::getDeleted, 0)
            );
            overview.setTotalNotifications(totalNotifications);

            Long unreadNotifications = notificationMapper.selectCount(
                    new LambdaQueryWrapper<SysNotification>()
                            .eq(SysNotification::getDeleted, 0)
                            .eq(SysNotification::getStatus, "unread")
            );
            overview.setUnreadNotifications(unreadNotifications);
        } else {
            overview.setTotalNotifications(0L);
            overview.setUnreadNotifications(0L);
        }

        // 在线用户数（简化实现，可以后续通过 Redis 统计）
        overview.setOnlineUserCount(0L);

        return overview;
    }

    @Override
    public List<StatisticsVO> getUserGrowthTrend(Integer days) {
        if (days == null || days <= 0) {
            days = 30;
        }

        List<StatisticsVO> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startTime = date.atStartOfDay();
            LocalDateTime endTime = date.plusDays(1).atStartOfDay();

            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getDeleted, 0)
                            .ge(SysUser::getCreateTime, startTime)
                            .lt(SysUser::getCreateTime, endTime)
            );

            StatisticsVO vo = new StatisticsVO();
            vo.setDate(date.format(formatter));
            vo.setValue(count);
            result.add(vo);
        }

        return result;
    }

    @Override
    public List<StatisticsVO> getLoginStatistics(Integer days) {
        if (days == null || days <= 0) {
            days = 30;
        }

        List<StatisticsVO> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startTime = date.atStartOfDay();
            LocalDateTime endTime = date.plusDays(1).atStartOfDay();

            Long count = loginLogMapper.selectCount(
                    new LambdaQueryWrapper<SysLoginLog>()
                            .ge(SysLoginLog::getLoginTime, startTime)
                            .lt(SysLoginLog::getLoginTime, endTime)
            );

            StatisticsVO vo = new StatisticsVO();
            vo.setDate(date.format(formatter));
            vo.setValue(count);
            result.add(vo);
        }

        return result;
    }

    @Override
    public Map<String, Long> getLoginStatusStatistics() {
        Map<String, Long> result = new HashMap<>();

        // 登录成功数量
        Long successCount = loginLogMapper.selectCount(
                new LambdaQueryWrapper<SysLoginLog>().eq(SysLoginLog::getStatus, "0")
        );
        result.put("success", successCount);

        // 登录失败数量
        Long failCount = loginLogMapper.selectCount(
                new LambdaQueryWrapper<SysLoginLog>().eq(SysLoginLog::getStatus, "1")
        );
        result.put("fail", failCount);

        return result;
    }

    @Override
    public List<StatisticsVO> getOperationStatistics(Integer days) {
        if (days == null || days <= 0) {
            days = 30;
        }

        List<StatisticsVO> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startTime = date.atStartOfDay();
            LocalDateTime endTime = date.plusDays(1).atStartOfDay();

            Long count = operLogMapper.selectCount(
                    new LambdaQueryWrapper<SysOperLog>()
                            .ge(SysOperLog::getOperTime, startTime)
                            .lt(SysOperLog::getOperTime, endTime)
            );

            StatisticsVO vo = new StatisticsVO();
            vo.setDate(date.format(formatter));
            vo.setValue(count);
            result.add(vo);
        }

        return result;
    }

    @Override
    public List<StatisticsVO> getDeptUserDistribution() {
        // 查询所有部门
        List<SysDept> depts = deptMapper.selectList(
                new LambdaQueryWrapper<SysDept>().eq(SysDept::getDeleted, 0)
        );

        List<StatisticsVO> result = new ArrayList<>();
        for (SysDept dept : depts) {
            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getDeleted, 0)
                            .eq(SysUser::getDeptId, dept.getDeptId())
            );
            if (count > 0) {
                StatisticsVO vo = new StatisticsVO();
                vo.setName(dept.getDeptName());
                vo.setValue(count);
                result.add(vo);
            }
        }

        return result;
    }

    @Override
    public List<StatisticsVO> getRoleUserDistribution() {
        // 查询所有角色
        List<SysRole> roles = roleMapper.selectList(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getDeleted, 0)
        );

        List<StatisticsVO> result = new ArrayList<>();
        for (SysRole role : roles) {
            // 通过 sys_user_role 表统计该角色的用户数
            Long count = userMapper.countUsersByRoleId(role.getRoleId());
            if (count > 0) {
                StatisticsVO vo = new StatisticsVO();
                vo.setName(role.getRoleName());
                vo.setValue(count);
                result.add(vo);
            }
        }

        return result;
    }

    @Override
    public List<StatisticsVO> getOperationTypeStatistics() {
        // 查询最近30天的操作日志
        LocalDateTime startTime = LocalDate.now().minusDays(30).atStartOfDay();
        List<SysOperLog> logs = operLogMapper.selectList(
                new LambdaQueryWrapper<SysOperLog>().ge(SysOperLog::getOperTime, startTime)
        );

        // 按操作类型分组统计
        Map<String, Long> typeCountMap = logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getBusinessType() != null ? String.valueOf(log.getBusinessType()) : "其他",
                        Collectors.counting()
                ));

        List<StatisticsVO> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : typeCountMap.entrySet()) {
            String typeName = getBusinessTypeName(entry.getKey());
            StatisticsVO vo = new StatisticsVO();
            vo.setName(typeName);
            vo.setValue(entry.getValue());
            result.add(vo);
        }

        return result;
    }

    /**
     * 获取业务类型名称
     */
    private String getBusinessTypeName(String type) {
        switch (type) {
            case "0":
                return "其他";
            case "1":
                return "新增";
            case "2":
                return "修改";
            case "3":
                return "删除";
            case "4":
                return "授权";
            case "5":
                return "导出";
            case "6":
                return "导入";
            case "7":
                return "强退";
            case "8":
                return "清空";
            default:
                return "其他";
        }
    }

}
