package com.admin.system.controller;

import com.admin.system.common.Result;
import com.admin.system.service.IDashboardService;
import com.admin.system.vo.DashboardVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表板控制器
 *
 * @author Admin
 */
@Api(tags = "数据仪表板")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    /**
     * 获取仪表板数据
     */
    @ApiOperation("获取仪表板数据")
    @GetMapping("/data")
    public Result<DashboardVO> getDashboardData() {
        DashboardVO data = dashboardService.getDashboardData();
        return Result.success(data);
    }

    /**
     * 获取统计卡片数据
     */
    @ApiOperation("获取统计卡片数据")
    @GetMapping("/stat-card")
    public Result<DashboardVO.StatCard> getStatCard() {
        DashboardVO.StatCard statCard = dashboardService.getStatCard();
        return Result.success(statCard);
    }

    /**
     * 获取用户增长趋势
     */
    @ApiOperation("获取用户增长趋势")
    @GetMapping("/user-trend")
    public Result<DashboardVO.ChartData> getUserTrend() {
        DashboardVO.ChartData chartData = dashboardService.getUserTrend();
        return Result.success(chartData);
    }

    /**
     * 获取登录统计
     */
    @ApiOperation("获取登录统计")
    @GetMapping("/login-stats")
    public Result<DashboardVO.ChartData> getLoginStats() {
        DashboardVO.ChartData chartData = dashboardService.getLoginStats();
        return Result.success(chartData);
    }

    /**
     * 获取部门人员分布
     */
    @ApiOperation("获取部门人员分布")
    @GetMapping("/dept-distribution")
    public Result<DashboardVO.ChartData> getDeptDistribution() {
        DashboardVO.ChartData chartData = dashboardService.getDeptDistribution();
        return Result.success(chartData);
    }
}
