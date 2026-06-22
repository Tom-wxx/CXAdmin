package com.admin.system.service;

import com.admin.system.entity.SysMessageConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 消息配置服务接口
 *
 * @author Admin
 */
public interface ISysMessageConfigService extends IService<SysMessageConfig> {

    /**
     * 获取默认配置
     *
     * @param messageType 消息类型（1邮件 2短信 3站内信 4微信）
     * @return 默认配置
     */
    SysMessageConfig getDefaultConfig(String messageType);
}
