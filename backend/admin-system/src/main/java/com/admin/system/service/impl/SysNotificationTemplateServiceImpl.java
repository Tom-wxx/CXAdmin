package com.admin.system.service.impl;

import com.admin.system.entity.SysNotificationTemplate;
import com.admin.system.mapper.SysNotificationTemplateMapper;
import com.admin.system.service.ISysNotificationTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 通知模板 Service 实现类
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class SysNotificationTemplateServiceImpl extends ServiceImpl<SysNotificationTemplateMapper, SysNotificationTemplate>
        implements ISysNotificationTemplateService {

    private final SysNotificationTemplateMapper templateMapper;

    @Override
    public SysNotificationTemplate getByTemplateCode(String templateCode) {
        return templateMapper.selectByTemplateCode(templateCode);
    }

}
