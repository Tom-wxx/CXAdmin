package com.admin.system.service;

import com.admin.system.entity.SysMessageLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 消息发送日志服务接口
 *
 * @author Admin
 */
public interface ISysMessageLogService extends IService<SysMessageLog> {

    /**
     * 记录发送日志
     *
     * @param messageLog 日志信息
     */
    void recordLog(SysMessageLog messageLog);
}
