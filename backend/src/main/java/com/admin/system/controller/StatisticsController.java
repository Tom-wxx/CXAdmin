package com.admin.system.controller;

import com.admin.system.common.Result;
import com.admin.system.service.IStatisticsService;
import com.admin.system.vo.StatisticsVO;
import com.admin.system.vo.SystemOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 统计分析控制器
 *
 * @author Admin
 */
@RestController
@RequestMapping("/statistics")
@Tag(name = "统计分析")
@RequiredArgsConstructor
public class StatisticsController {

    private final IStatisticsService statisticsService;

    /**
     * 获取系统概览数据
     */
    @GetMapping("/overview")
    @Operation(summary = "获取系统概览数据")
    public Result<SystemOverviewVO> getSystemOverview() {
        SystemOverviewVO overview = statisticsService.getSystemOverview();
        return Result.success(overview);
    }

    /**
     * 获取用户增长趋势
     */
    @GetMapping("/user/growth")
    @PreAuthorize("@ss.hasPermi('statistics:user:growth')")
    @Operation(summary = "获取用户增长趋势")
    public Result<List<StatisticsVO>> getUserGrowthTrend(
            @RequestParam(value = "days", required = false, defaultValue = "30") Integer days) {
        List<StatisticsVO> data = statisticsService.getUserGrowthTrend(days);
        return Result.success(data);
    }

    /**
     * 获取登录统计
     */
    @GetMapping("/login/trend")
    @PreAuthorize("@ss.hasPermi('statistics:login:trend')")
    @Operation(summary = "获取登录统计")
    public Result<List<StatisticsVO>> getLoginStatistics(
            @RequestParam(value = "days", required = false, defaultValue = "30") Integer days) {
        List<StatisticsVO> data = statisticsService.getLoginStatistics(days);
        return Result.success(data);
    }

    /**
     * 获取登录成功/失败统计
     */
    @GetMapping("/login/status")
    @PreAuthorize("@ss.hasPermi('statistics:login:status')")
    @Operation(summary = "获取登录状态统计")
    public Result<Map<String, Long>> getLoginStatusStatistics() {
        Map<String, Long> data = statisticsService.getLoginStatusStatistics();
        return Result.success(data);
    }

    /**
     * 获取操作日志统计
     */
    @GetMapping("/operation/trend")
    @PreAuthorize("@ss.hasPermi('statistics:operation:trend')")
    @Operation(summary = "获取操作日志统计")
    public Result<List<StatisticsVO>> getOperationStatistics(
            @RequestParam(value = "days", required = false, defaultValue = "30") Integer days) {
        List<StatisticsVO> data = statisticsService.getOperationStatistics(days);
        return Result.success(data);
    }

    /**
     * 获取部门人员分布
     */
    @GetMapping("/dept/distribution")
    @PreAuthorize("@ss.hasPermi('statistics:dept:distribution')")
    @Operation(summary = "获取部门人员分布")
    public Result<List<StatisticsVO>> getDeptUserDistribution() {
        List<StatisticsVO> data = statisticsService.getDeptUserDistribution();
        return Result.success(data);
    }

    /**
     * 获取角色人员分布
     */
    @GetMapping("/role/distribution")
    @PreAuthorize("@ss.hasPermi('statistics:role:distribution')")
    @Operation(summary = "获取角色人员分布")
    public Result<List<StatisticsVO>> getRoleUserDistribution() {
        List<StatisticsVO> data = statisticsService.getRoleUserDistribution();
        return Result.success(data);
    }

    /**
     * 获取操作类型统计
     */
    @GetMapping("/operation/type")
    @PreAuthorize("@ss.hasPermi('statistics:operation:type')")
    @Operation(summary = "获取操作类型统计")
    public Result<List<StatisticsVO>> getOperationTypeStatistics() {
        List<StatisticsVO> data = statisticsService.getOperationTypeStatistics();
        return Result.success(data);
    }

}
