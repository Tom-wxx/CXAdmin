package com.admin.system.service.impl;

import com.admin.system.entity.SysMessageLog;
import com.admin.system.mapper.SysMessageLogMapper;
import com.admin.system.service.ISysMessageLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 消息发送日志服务实现
 *
 * @author Admin
 */
@Service
public class SysMessageLogServiceImpl extends ServiceImpl<SysMessageLogMapper, SysMessageLog> implements ISysMessageLogService {

    @Override
    public void recordLog(SysMessageLog messageLog) {
        this.save(messageLog);
    }
}
