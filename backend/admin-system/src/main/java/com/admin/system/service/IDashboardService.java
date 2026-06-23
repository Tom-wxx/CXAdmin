package com.admin.system.service;

import com.admin.system.vo.DashboardVO;

/**
 * 仪表板服务接口
 *
 * @author Admin
 */
public interface IDashboardService {

    /**
     * 获取仪表板数据
     *
     * @return 仪表板数据
     */
    DashboardVO getDashboardData();

    /**
     * 获取统计卡片数据
     *
     * @return 统计卡片数据
     */
    DashboardVO.StatCard getStatCard();

    /**
     * 获取用户增长趋势（最近7天）
     *
     * @return 用户增长趋势数据
     */
    DashboardVO.ChartData getUserTrend();

    /**
     * 获取登录统计（最近7天）
     *
     * @return 登录统计数据
     */
    DashboardVO.ChartData getLoginStats();

    /**
     * 获取部门人员分布
     *
     * @return 部门人员分布数据
     */
    DashboardVO.ChartData getDeptDistribution();
}
