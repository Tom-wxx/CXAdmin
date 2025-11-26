package com.admin.system.service;

import com.admin.system.entity.SysJob;
import com.baomidou.mybatisplus.extension.service.IService;
import org.quartz.SchedulerException;

/**
 * 定时任务调度 业务层
 *
 * @author Admin
 */
public interface ISysJobService extends IService<SysJob> {

    /**
     * 新增任务
     */
    void insertJob(SysJob job) throws SchedulerException;

    /**
     * 更新任务
     */
    void updateJob(SysJob job) throws SchedulerException;

    /**
     * 删除任务
     */
    void deleteJob(Long jobId) throws SchedulerException;

    /**
     * 批量删除任务
     */
    void deleteJobByIds(Long[] jobIds) throws SchedulerException;

    /**
     * 暂停任务
     */
    void pauseJob(SysJob job) throws SchedulerException;

    /**
     * 恢复任务
     */
    void resumeJob(SysJob job) throws SchedulerException;

    /**
     * 立即运行任务
     */
    void run(SysJob job) throws SchedulerException;

    /**
     * 修改任务状态
     */
    void changeStatus(SysJob job) throws SchedulerException;
}
