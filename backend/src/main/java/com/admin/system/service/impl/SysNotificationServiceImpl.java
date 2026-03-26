package com.admin.system.service.impl;

import com.admin.system.entity.SysNotification;
import com.admin.system.entity.SysNotificationTemplate;
import com.admin.system.mapper.SysNotificationMapper;
import com.admin.system.service.ISysNotificationService;
import com.admin.system.service.ISysNotificationTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 站内通知 Service 实现类
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class SysNotificationServiceImpl extends ServiceImpl<SysNotificationMapper, SysNotification>
        implements ISysNotificationService {

    private final SysNotificationMapper notificationMapper;
    private final ISysNotificationTemplateService templateService;

    @Override
    @Transactional
    public boolean sendNotification(SysNotification notification) {
        if (notification.getStatus() == null) {
            notification.setStatus("unread");
        }
        if (notification.getPriority() == null) {
            notification.setPriority("normal");
        }
        return this.save(notification);
    }

    @Override
    @Transactional
    public boolean sendNotificationToUsers(SysNotification notification, List<Long> userIds) {
        List<SysNotification> notifications = new ArrayList<>();
        for (Long userId : userIds) {
            SysNotification notif = new SysNotification();
            notif.setTitle(notification.getTitle());
            notif.setContent(notification.getContent());
            notif.setType(notification.getType());
            notif.setPriority(notification.getPriority());
            notif.setStatus("unread");
            notif.setUserId(userId);
            notif.setSenderId(notification.getSenderId());
            notif.setSenderName(notification.getSenderName());
            notif.setLinkUrl(notification.getLinkUrl());
            notif.setLinkType(notification.getLinkType());
            notifications.add(notif);
        }
        return this.saveBatch(notifications);
    }

    @Override
    @Transactional
    public boolean sendNotificationByTemplate(String templateCode, Long userId, Map<String, Object> params) {
        // 获取模板
        SysNotificationTemplate template = templateService.getByTemplateCode(templateCode);
        if (template == null) {
            return false;
        }

        // 替换模板参数
        String title = replacePlaceholders(template.getTitle(), params);
        String content = replacePlaceholders(template.getContent(), params);

        // 创建通知
        SysNotification notification = new SysNotification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(template.getType());
        notification.setPriority(template.getPriority());
        notification.setStatus("unread");
        notification.setUserId(userId);

        return this.sendNotification(notification);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return notificationMapper.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public boolean markAsRead(Long id, Long userId) {
        LambdaUpdateWrapper<SysNotification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysNotification::getId, id)
                .eq(SysNotification::getUserId, userId)
                .set(SysNotification::getStatus, "read")
                .set(SysNotification::getReadTime, new Date());
        return this.update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean batchMarkAsRead(Long[] ids, Long userId) {
        if (ids == null || ids.length == 0) {
            return false;
        }
        return notificationMapper.batchMarkAsRead(userId, ids) > 0;
    }

    @Override
    @Transactional
    public boolean markAllAsRead(Long userId) {
        return notificationMapper.markAllAsRead(userId) > 0;
    }

    @Override
    public List<SysNotification> getUserNotifications(Long userId) {
        LambdaQueryWrapper<SysNotification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNotification::getUserId, userId)
                .orderByDesc(SysNotification::getCreateTime);
        return this.list(queryWrapper);
    }

    /**
     * 替换模板占位符
     *
     * @param template 模板内容
     * @param params 参数
     * @return 替换后的内容
     */
    private String replacePlaceholders(String template, Map<String, Object> params) {
        if (template == null || params == null) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }

}
