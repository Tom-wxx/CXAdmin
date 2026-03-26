package com.admin.system.service.impl;

import com.admin.system.common.exception.ServiceException;
import com.admin.system.entity.SysNotice;
import com.admin.system.mapper.SysNoticeMapper;
import com.admin.system.service.ISysNoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知公告 业务层处理
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements ISysNoticeService {

    private final SysNoticeMapper noticeMapper;

    /**
     * 新增通知公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNotice(SysNotice notice) {
        noticeMapper.insert(notice);
    }

    /**
     * 修改通知公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(SysNotice notice) {
        if (notice.getNoticeId() == null) {
            throw new ServiceException("公告ID不能为空");
        }

        SysNotice existNotice = noticeMapper.selectById(notice.getNoticeId());
        if (existNotice == null) {
            throw new ServiceException("公告不存在");
        }

        noticeMapper.updateById(notice);
    }

    /**
     * 删除通知公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNoticeById(Long noticeId) {
        if (noticeId == null) {
            throw new ServiceException("公告ID不能为空");
        }

        SysNotice notice = noticeMapper.selectById(noticeId);
        if (notice == null) {
            throw new ServiceException("公告不存在");
        }

        noticeMapper.deleteById(noticeId);
    }

    /**
     * 批量删除通知公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNoticeByIds(Long[] noticeIds) {
        if (noticeIds == null || noticeIds.length == 0) {
            throw new ServiceException("公告ID不能为空");
        }

        for (Long noticeId : noticeIds) {
            deleteNoticeById(noticeId);
        }
    }
}
