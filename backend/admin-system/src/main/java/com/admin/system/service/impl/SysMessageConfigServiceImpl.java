package com.admin.system.service.impl;

import com.admin.system.entity.SysMessageConfig;
import com.admin.system.mapper.SysMessageConfigMapper;
import com.admin.system.service.ISysMessageConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 消息配置服务实现
 *
 * @author Admin
 */
@Service
public class SysMessageConfigServiceImpl extends ServiceImpl<SysMessageConfigMapper, SysMessageConfig> implements ISysMessageConfigService {

    @Override
    public SysMessageConfig getDefaultConfig(String messageType) {
        LambdaQueryWrapper<SysMessageConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMessageConfig::getMessageType, messageType);
        queryWrapper.eq(SysMessageConfig::getIsDefault, "1");
        queryWrapper.eq(SysMessageConfig::getStatus, "0");
        queryWrapper.eq(SysMessageConfig::getDeleted, 0);
        return this.getOne(queryWrapper);
    }
}
