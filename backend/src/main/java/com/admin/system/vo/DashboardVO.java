package com.admin.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "仪表板数据")
public class DashboardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "统计卡片数据")
    private StatCard statCard;

    @Schema(description = "用户增长趋势数据")
    private ChartData userTrend;

    @Schema(description = "登录统计数据")
    private ChartData loginStats;

    @Schema(description = "部门人员分布")
    private ChartData deptDistribution;

    @Schema(description = "近期操作日志")
    private List<RecentLog> recentLogs;

    @Schema(description = "快捷访问")
    private QuickAccess quickAccess;

    /**
     * 统计卡片数据
     */
    @Data
    @Schema(description = "统计卡片")
    public static class StatCard implements Serializable {
        @Schema(description = "用户总数")
        private Long totalUsers;

        @Schema(description = "今日新增用户")
        private Long todayUsers;

        @Schema(description = "角色总数")
        private Long totalRoles;

        @Schema(description = "在线用户数")
        private Long onlineUsers;

        @Schema(description = "通知公告数")
        private Long totalNotices;

        @Schema(description = "待办任务数")
        private Long pendingTasks;
    }

    /**
     * 图表数据
     */
    @Data
    @Schema(description = "图表数据")
    public static class ChartData implements Serializable {
        @Schema(description = "X轴数据（日期或分类）")
        private List<String> labels;

        @Schema(description = "Y轴数据")
        private List<Long> values;

        @Schema(description = "多系列数据（可选）")
        private Map<String, List<Long>> series;
    }

    /**
     * 近期日志
     */
    @Data
    @Schema(description = "近期日志")
    public static class RecentLog implements Serializable {
        @Schema(description = "操作人")
        private String operator;

        @Schema(description = "操作类型")
        private String operType;

        @Schema(description = "操作内容")
        private String operContent;

        @Schema(description = "操作时间")
        private String operTime;

        @Schema(description = "操作状态")
        private String status;
    }

    /**
     * 快捷访问
     */
    @Data
    @Schema(description = "快捷访问")
    public static class QuickAccess implements Serializable {
        @Schema(description = "待审批数量")
        private Long pendingApprovals;

        @Schema(description = "待处理消息")
        private Long pendingMessages;

        @Schema(description = "系统告警")
        private Long systemAlerts;
    }
}
