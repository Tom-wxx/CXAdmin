package com.admin.system.service;

import com.admin.system.vo.StatisticsVO;
import com.admin.system.vo.SystemOverviewVO;

import java.util.List;
import java.util.Map;

/**
 * 统计服务接口
 *
 * @author Admin
 */
public interface IStatisticsService {

    /**
     * 获取系统概览数据
     *
     * @return 系统概览数据
     */
    SystemOverviewVO getSystemOverview();

    /**
     * 获取用户增长趋势（最近30天）
     *
     * @param days 天数
     * @return 用户增长趋势数据
     */
    List<StatisticsVO> getUserGrowthTrend(Integer days);

    /**
     * 获取登录统计（最近30天）
     *
     * @param days 天数
     * @return 登录统计数据
     */
    List<StatisticsVO> getLoginStatistics(Integer days);

    /**
     * 获取登录成功/失败统计
     *
     * @return 登录状态统计
     */
    Map<String, Long> getLoginStatusStatistics();

    /**
     * 获取操作日志统计（最近30天）
     *
     * @param days 天数
     * @return 操作日志统计数据
     */
    List<StatisticsVO> getOperationStatistics(Integer days);

    /**
     * 获取部门人员分布
     *
     * @return 部门人员分布数据
     */
    List<StatisticsVO> getDeptUserDistribution();

    /**
     * 获取角色人员分布
     *
     * @return 角色人员分布数据
     */
    List<StatisticsVO> getRoleUserDistribution();

    /**
     * 获取操作类型统计
     *
     * @return 操作类型统计
     */
    List<StatisticsVO> getOperationTypeStatistics();

}
