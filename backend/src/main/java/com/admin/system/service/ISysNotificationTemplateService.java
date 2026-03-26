package com.admin.system.service;

import com.admin.system.entity.SysNotificationTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 通知模板 Service 接口
 *
 * @author Admin
 */
public interface ISysNotificationTemplateService extends IService<SysNotificationTemplate> {

    /**
     * 根据模板编码查询模板
     *
     * @param templateCode 模板编码
     * @return 模板信息
     */
    SysNotificationTemplate getByTemplateCode(String templateCode);

}
