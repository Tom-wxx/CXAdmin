package com.admin.system.quartz;

import com.admin.system.entity.SysJob;
import com.admin.system.entity.SysJobLog;
import com.admin.system.service.ISysJobLogService;
import com.admin.system.utils.SpringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 抽象quartz调用
 *
 * @author Admin
 */
public abstract class AbstractQuartzJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(AbstractQuartzJob.class);

    /**
     * 线程本地变量
     */
    private static ThreadLocal<Date> threadLocal = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SysJob sysJob = (SysJob) context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES);
        try {
            before(context, sysJob);
            doExecute(context, sysJob);
            after(context, sysJob, null);
        } catch (Exception e) {
            log.error("任务执行异常  - ：", e);
            after(context, sysJob, e);
        }
    }

    /**
     * 执行前
     */
    protected void before(JobExecutionContext context, SysJob sysJob) {
        threadLocal.set(new Date());
    }

    /**
     * 执行后
     */
    protected void after(JobExecutionContext context, SysJob sysJob, Exception e) {
        Date startTime = threadLocal.get();
        threadLocal.remove();

        final SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setJobName(sysJob.getJobName());
        sysJobLog.setJobGroup(sysJob.getJobGroup());
        sysJobLog.setInvokeTarget(sysJob.getInvokeTarget());
        sysJobLog.setCreateTime(startTime);

        long runMs = new Date().getTime() - startTime.getTime();
        sysJobLog.setJobMessage(sysJob.getJobName() + " 总共耗时：" + runMs + "毫秒");

        if (e != null) {
            sysJobLog.setStatus("1");
            String errorMsg = e.toString();
            sysJobLog.setExceptionInfo(errorMsg.length() > 2000 ? errorMsg.substring(0, 2000) : errorMsg);
        } else {
            sysJobLog.setStatus("0");
        }

        // 写入数据库
        ISysJobLogService jobLogService = SpringUtils.getBean(ISysJobLogService.class);
        jobLogService.save(sysJobLog);
    }

    /**
     * 执行方法，由子类重载
     */
    protected abstract void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception;
}
