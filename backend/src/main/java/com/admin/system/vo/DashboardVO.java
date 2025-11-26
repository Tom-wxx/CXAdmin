package com.admin.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 仪表板数据视图对象
 *
 * @author Admin
 */
@Data
@ApiModel("仪表板数据")
public class DashboardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("统计卡片数据")
    private StatCard statCard;

    @ApiModelProperty("用户增长趋势数据")
    private ChartData userTrend;

    @ApiModelProperty("登录统计数据")
    private ChartData loginStats;

    @ApiModelProperty("部门人员分布")
    private ChartData deptDistribution;

    @ApiModelProperty("近期操作日志")
    private List<RecentLog> recentLogs;

    @ApiModelProperty("快捷访问")
    private QuickAccess quickAccess;

    /**
     * 统计卡片数据
     */
    @Data
    @ApiModel("统计卡片")
    public static class StatCard implements Serializable {
        @ApiModelProperty("用户总数")
        private Long totalUsers;

        @ApiModelProperty("今日新增用户")
        private Long todayUsers;

        @ApiModelProperty("角色总数")
        private Long totalRoles;

        @ApiModelProperty("在线用户数")
        private Long onlineUsers;

        @ApiModelProperty("通知公告数")
        private Long totalNotices;

        @ApiModelProperty("待办任务数")
        private Long pendingTasks;
    }

    /**
     * 图表数据
     */
    @Data
    @ApiModel("图表数据")
    public static class ChartData implements Serializable {
        @ApiModelProperty("X轴数据（日期或分类）")
        private List<String> labels;

        @ApiModelProperty("Y轴数据")
        private List<Long> values;

        @ApiModelProperty("多系列数据（可选）")
        private Map<String, List<Long>> series;
    }

    /**
     * 近期日志
     */
    @Data
    @ApiModel("近期日志")
    public static class RecentLog implements Serializable {
        @ApiModelProperty("操作人")
        private String operator;

        @ApiModelProperty("操作类型")
        private String operType;

        @ApiModelProperty("操作内容")
        private String operContent;

        @ApiModelProperty("操作时间")
        private String operTime;

        @ApiModelProperty("操作状态")
        private String status;
    }

    /**
     * 快捷访问
     */
    @Data
    @ApiModel("快捷访问")
    public static class QuickAccess implements Serializable {
        @ApiModelProperty("待审批数量")
        private Long pendingApprovals;

        @ApiModelProperty("待处理消息")
        private Long pendingMessages;

        @ApiModelProperty("系统告警")
        private Long systemAlerts;
    }
}
