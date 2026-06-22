package com.admin.system.mapper;

import com.admin.system.entity.SysNotification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 站内通知 Mapper 接口
 *
 * @author Admin
 */
public interface SysNotificationMapper extends BaseMapper<SysNotification> {

    /**
     * 获取用户未读消息数量
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Long countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 批量标记消息为已读
     *
     * @param userId 用户ID
     * @param ids 消息ID列表
     * @return 更新数量
     */
    int batchMarkAsRead(@Param("userId") Long userId, @Param("ids") Long[] ids);

    /**
     * 全部标记为已读
     *
     * @param userId 用户ID
     * @return 更新数量
     */
    int markAllAsRead(@Param("userId") Long userId);

}
