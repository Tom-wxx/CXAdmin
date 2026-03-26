package com.admin.system.service.impl;

import com.admin.system.common.exception.ServiceException;
import com.admin.system.entity.SysJobLog;
import com.admin.system.mapper.SysJobLogMapper;
import com.admin.system.service.ISysJobLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 定时任务调度日志 业务层处理
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements ISysJobLogService {

    private final SysJobLogMapper jobLogMapper;

    /**
     * 删除任务日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobLogById(Long jobLogId) {
        if (jobLogId == null) {
            throw new ServiceException("任务日志ID不能为空");
        }

        SysJobLog jobLog = jobLogMapper.selectById(jobLogId);
        if (jobLog == null) {
            throw new ServiceException("任务日志不存在");
        }

        jobLogMapper.deleteById(jobLogId);
    }

    /**
     * 批量删除任务日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobLogByIds(Long[] jobLogIds) {
        if (jobLogIds == null || jobLogIds.length == 0) {
            throw new ServiceException("任务日志ID不能为空");
        }

        for (Long jobLogId : jobLogIds) {
            deleteJobLogById(jobLogId);
        }
    }

    /**
     * 清空任务日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanJobLog() {
        jobLogMapper.delete(null);
    }
}
