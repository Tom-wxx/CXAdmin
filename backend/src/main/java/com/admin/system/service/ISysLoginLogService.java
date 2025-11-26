package com.admin.system.service;

import com.admin.system.entity.SysLoginLog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 登录日志 业务层
 *
 * @author Admin
 */
public interface ISysLoginLogService extends IService<SysLoginLog> {

    /**
     * 分页查询登录日志列表
     */
    Page<SysLoginLog> selectLoginLogPage(Page<SysLoginLog> page, String username, String ipaddr,
                                          String status, String beginTime, String endTime);

    /**
     * 新增登录日志
     */
    void insertLoginLog(SysLoginLog loginLog);

    /**
     * 删除登录日志
     */
    void deleteLoginLogById(Long infoId);

    /**
     * 批量删除登录日志
     */
    void deleteLoginLogByIds(Long[] infoIds);

    /**
     * 清空登录日志
     */
    void cleanLoginLog();
}
