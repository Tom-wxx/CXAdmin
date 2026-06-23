package com.admin.system.service;

import com.admin.system.entity.SysJobLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 定时任务调度日志 业务层
 *
 * @author Admin
 */
public interface ISysJobLogService extends IService<SysJobLog> {

    /**
     * 删除任务日志
     */
    void deleteJobLogById(Long jobLogId);

    /**
     * 批量删除任务日志
     */
    void deleteJobLogByIds(Long[] jobLogIds);

    /**
     * 清空任务日志
     */
    void cleanJobLog();
}
