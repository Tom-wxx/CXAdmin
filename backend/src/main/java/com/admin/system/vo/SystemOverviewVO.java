package com.admin.system.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统概览数据 VO
 *
 * @author Admin
 */
@Data
public class SystemOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户总数
     */
    private Long totalUsers;

    /**
     * 今日新增用户
     */
    private Long todayNewUsers;

    /**
     * 角色总数
     */
    private Long totalRoles;

    /**
     * 部门总数
     */
    private Long totalDepts;

    /**
     * 今日登录次数
     */
    private Long todayLoginCount;

    /**
     * 在线用户数
     */
    private Long onlineUserCount;

    /**
     * 今日操作次数
     */
    private Long todayOperationCount;

    /**
     * 通知总数
     */
    private Long totalNotifications;

    /**
     * 未读通知数
     */
    private Long unreadNotifications;

}
