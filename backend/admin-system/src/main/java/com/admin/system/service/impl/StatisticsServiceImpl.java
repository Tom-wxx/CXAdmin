package com.admin.system.service.impl;

import com.admin.system.entity.*;
import com.admin.system.mapper.*;
import com.admin.system.service.IOnlineUserService;
import com.admin.system.service.IStatisticsService;
import com.admin.system.vo.GroupCountVO;
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
    private final IOnlineUserService onlineUserService;

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

        // 在线用户数
        overview.setOnlineUserCount(onlineUserService.countOnlineUsers());

        return overview;
    }

    @Override
    public List<StatisticsVO> getUserGrowthTrend(Integer days) {
        int n = normalizeDays(days);
        return fillDays(userMapper.countCreatedByDay(startOfWindow(n)), n);
    }

    @Override
    public List<StatisticsVO> getLoginStatistics(Integer days) {
        int n = normalizeDays(days);
        return fillDays(loginLogMapper.countByDay(startOfWindow(n)), n);
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
        int n = normalizeDays(days);
        return fillDays(operLogMapper.countByDay(startOfWindow(n)), n);
    }

    @Override
    public List<StatisticsVO> getDeptUserDistribution() {
        // 两条查询：部门列表 + 按部门分组计数（原先是每个部门一条 COUNT）
        List<SysDept> depts = deptMapper.selectList(
                new LambdaQueryWrapper<SysDept>().eq(SysDept::getDeleted, 0)
        );
        Map<String, Long> counts = toCountMap(userMapper.countUsersGroupByDept());

        List<StatisticsVO> result = new ArrayList<>();
        for (SysDept dept : depts) {
            long count = counts.getOrDefault(String.valueOf(dept.getDeptId()), 0L);
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
        List<SysRole> roles = roleMapper.selectList(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getDeleted, 0)
        );
        Map<String, Long> counts = toCountMap(userMapper.countUsersGroupByRole());

        List<StatisticsVO> result = new ArrayList<>();
        for (SysRole role : roles) {
            long count = counts.getOrDefault(String.valueOf(role.getRoleId()), 0L);
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
        // 最近30天按操作类型在数据库里分组计数（原先把30天的日志整表读进内存再分组）
        LocalDateTime startTime = LocalDate.now().minusDays(30).atStartOfDay();

        List<StatisticsVO> result = new ArrayList<>();
        for (GroupCountVO row : operLogMapper.countByBusinessType(startTime)) {
            String type = row.getGroupKey() != null ? row.getGroupKey() : "其他";
            StatisticsVO vo = new StatisticsVO();
            vo.setName(getBusinessTypeName(type));
            vo.setValue(row.getTotal());
            result.add(vo);
        }

        return result;
    }

    private static int normalizeDays(Integer days) {
        return days == null || days <= 0 ? 30 : days;
    }

    /**
     * 统计窗口起点：含今天在内共 days 天的第一天零点
     */
    private static LocalDateTime startOfWindow(int days) {
        return LocalDate.now().minusDays(days - 1L).atStartOfDay();
    }

    /**
     * 把按天分组的结果铺成连续 days 天（无数据的日期补 0）
     */
    private static List<StatisticsVO> fillDays(List<GroupCountVO> rows, int days) {
        Map<String, Long> counts = toCountMap(rows);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<StatisticsVO> result = new ArrayList<>(days);
        for (int i = days - 1; i >= 0; i--) {
            String date = today.minusDays(i).format(formatter);
            StatisticsVO vo = new StatisticsVO();
            vo.setDate(date);
            vo.setValue(counts.getOrDefault(date, 0L));
            result.add(vo);
        }
        return result;
    }

    private static Map<String, Long> toCountMap(List<GroupCountVO> rows) {
        Map<String, Long> counts = new HashMap<>();
        for (GroupCountVO row : rows) {
            counts.put(row.getGroupKey(), row.getTotal());
        }
        return counts;
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
