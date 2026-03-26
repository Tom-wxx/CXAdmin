package com.admin.system.service.impl;

import com.admin.system.common.exception.ServiceException;
import com.admin.system.entity.SysOperLog;
import com.admin.system.mapper.SysOperLogMapper;
import com.admin.system.service.ISysOperLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 操作日志 业务层处理
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements ISysOperLogService {

    private final SysOperLogMapper operLogMapper;

    /**
     * 分页查询操作日志列表
     */
    @Override
    public Page<SysOperLog> selectOperLogPage(Page<SysOperLog> page, String title, String operName,
                                               Integer businessType, Integer status, String beginTime, String endTime) {
        return operLogMapper.selectOperLogPage(page, title, operName, businessType, status, beginTime, endTime);
    }

    /**
     * 新增操作日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOperLog(SysOperLog operLog) {
        operLogMapper.insert(operLog);
    }

    /**
     * 删除操作日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOperLogById(Long operId) {
        if (operId == null) {
            throw new ServiceException("日志ID不能为空");
        }
        operLogMapper.deleteById(operId);
    }

    /**
     * 批量删除操作日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOperLogByIds(Long[] operIds) {
        if (operIds == null || operIds.length == 0) {
            throw new ServiceException("日志ID不能为空");
        }

        for (Long operId : operIds) {
            deleteOperLogById(operId);
        }
    }

    /**
     * 清空操作日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanOperLog() {
        operLogMapper.cleanOperLog();
    }

    /**
     * 查询操作日志列表（用于导出）
     */
    @Override
    public List<SysOperLog> selectOperLogList(String title, String operName, Integer businessType,
                                               Integer status, String beginTime, String endTime) {
        return operLogMapper.selectOperLogList(title, operName, businessType, status, beginTime, endTime);
    }
}
