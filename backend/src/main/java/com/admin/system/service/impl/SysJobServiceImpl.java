package com.admin.system.service.impl;

import com.admin.system.common.exception.ServiceException;
import com.admin.system.entity.SysJob;
import com.admin.system.mapper.SysJobMapper;
import com.admin.system.quartz.ScheduleConstants;
import com.admin.system.quartz.ScheduleUtils;
import com.admin.system.service.ISysJobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 定时任务调度 业务层处理
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements ISysJobService {

    private final Scheduler scheduler;
    private final SysJobMapper jobMapper;

    /**
     * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理
     */
    @PostConstruct
    public void init() throws SchedulerException {
        scheduler.clear();
        List<SysJob> jobList = jobMapper.selectList(null);
        for (SysJob job : jobList) {
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
    }

    /**
     * 新增任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertJob(SysJob job) throws SchedulerException {
        job.setStatus("1"); // 新建任务默认暂停
        jobMapper.insert(job);
        ScheduleUtils.createScheduleJob(scheduler, job);
    }

    /**
     * 更新任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJob(SysJob job) throws SchedulerException {
        if (job.getJobId() == null) {
            throw new ServiceException("任务ID不能为空");
        }

        SysJob existJob = jobMapper.selectById(job.getJobId());
        if (existJob == null) {
            throw new ServiceException("任务不存在");
        }

        jobMapper.updateById(job);

        // 更新调度器中的任务
        updateSchedulerJob(job, existJob.getJobGroup());
    }

    /**
     * 更新任务调度信息
     */
    public void updateSchedulerJob(SysJob job, String jobGroup) throws SchedulerException {
        Long jobId = job.getJobId();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            // 先删除
            scheduler.deleteJob(jobKey);
        }
        // 再创建
        ScheduleUtils.createScheduleJob(scheduler, job);
    }

    /**
     * 删除任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJob(Long jobId) throws SchedulerException {
        if (jobId == null) {
            throw new ServiceException("任务ID不能为空");
        }

        SysJob job = jobMapper.selectById(jobId);
        if (job == null) {
            throw new ServiceException("任务不存在");
        }

        jobMapper.deleteById(jobId);
        scheduler.deleteJob(ScheduleUtils.getJobKey(jobId, job.getJobGroup()));
    }

    /**
     * 批量删除任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobByIds(Long[] jobIds) throws SchedulerException {
        if (jobIds == null || jobIds.length == 0) {
            throw new ServiceException("任务ID不能为空");
        }

        for (Long jobId : jobIds) {
            deleteJob(jobId);
        }
    }

    /**
     * 暂停任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pauseJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        jobMapper.updateById(job);
        scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
    }

    /**
     * 恢复任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resumeJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        jobMapper.updateById(job);
        scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
    }

    /**
     * 立即运行任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, job);
        scheduler.triggerJob(ScheduleUtils.getJobKey(jobId, jobGroup), dataMap);
    }

    /**
     * 修改任务状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(SysJob job) throws SchedulerException {
        String status = job.getStatus();
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
            resumeJob(job);
        } else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
            pauseJob(job);
        }
    }
}
