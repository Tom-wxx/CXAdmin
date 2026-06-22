package com.admin.system.service;

import com.admin.system.entity.SysNotice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 通知公告 业务层
 *
 * @author Admin
 */
public interface ISysNoticeService extends IService<SysNotice> {

    /**
     * 新增通知公告
     */
    void insertNotice(SysNotice notice);

    /**
     * 修改通知公告
     */
    void updateNotice(SysNotice notice);

    /**
     * 删除通知公告
     */
    void deleteNoticeById(Long noticeId);

    /**
     * 批量删除通知公告
     */
    void deleteNoticeByIds(Long[] noticeIds);
}
