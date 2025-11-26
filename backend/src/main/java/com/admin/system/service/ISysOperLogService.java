package com.admin.system.service;

import com.admin.system.entity.SysOperLog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 操作日志 业务层
 *
 * @author Admin
 */
public interface ISysOperLogService extends IService<SysOperLog> {

    /**
     * 分页查询操作日志列表
     */
    Page<SysOperLog> selectOperLogPage(Page<SysOperLog> page, String title, String operName,
                                        Integer businessType, Integer status, String beginTime, String endTime);

    /**
     * 新增操作日志
     */
    void insertOperLog(SysOperLog operLog);

    /**
     * 删除操作日志
     */
    void deleteOperLogById(Long operId);

    /**
     * 批量删除操作日志
     */
    void deleteOperLogByIds(Long[] operIds);

    /**
     * 清空操作日志
     */
    void cleanOperLog();
}
