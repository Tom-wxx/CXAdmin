package com.admin.system.service;

import com.admin.system.entity.SysNotification;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 站内通知 Service 接口
 *
 * @author Admin
 */
public interface ISysNotificationService extends IService<SysNotification> {

    /**
     * 发送通知给指定用户
     *
     * @param notification 通知信息
     * @return 是否成功
     */
    boolean sendNotification(SysNotification notification);

    /**
     * 发送通知给多个用户
     *
     * @param notification 通知信息
     * @param userIds 用户ID列表
     * @return 是否成功
     */
    boolean sendNotificationToUsers(SysNotification notification, List<Long> userIds);

    /**
     * 使用模板发送通知
     *
     * @param templateCode 模板编码
     * @param userId 用户ID
     * @param params 模板参数
     * @return 是否成功
     */
    boolean sendNotificationByTemplate(String templateCode, Long userId, Map<String, Object> params);

    /**
     * 获取用户未读消息数量
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 标记消息为已读
     *
     * @param id 消息ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAsRead(Long id, Long userId);

    /**
     * 批量标记消息为已读
     *
     * @param ids 消息ID列表
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean batchMarkAsRead(Long[] ids, Long userId);

    /**
     * 全部标记为已读
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAllAsRead(Long userId);

    /**
     * 获取用户的通知列表
     *
     * @param userId 用户ID
     * @return 通知列表
     */
    List<SysNotification> getUserNotifications(Long userId);

}
