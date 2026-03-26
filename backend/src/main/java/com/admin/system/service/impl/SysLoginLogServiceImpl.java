package com.admin.system.service.impl;

import com.admin.system.common.exception.ServiceException;
import com.admin.system.entity.SysLoginLog;
import com.admin.system.mapper.SysLoginLogMapper;
import com.admin.system.service.ISysLoginLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 登录日志 业务层处理
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements ISysLoginLogService {

    private final SysLoginLogMapper loginLogMapper;

    /**
     * 分页查询登录日志列表
     */
    @Override
    public Page<SysLoginLog> selectLoginLogPage(Page<SysLoginLog> page, String username, String ipaddr,
                                                 String status, String beginTime, String endTime) {
        return loginLogMapper.selectLoginLogPage(page, username, ipaddr, status, beginTime, endTime);
    }

    /**
     * 新增登录日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertLoginLog(SysLoginLog loginLog) {
        loginLogMapper.insert(loginLog);
    }

    /**
     * 删除登录日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLoginLogById(Long infoId) {
        if (infoId == null) {
            throw new ServiceException("日志ID不能为空");
        }
        loginLogMapper.deleteById(infoId);
    }

    /**
     * 批量删除登录日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLoginLogByIds(Long[] infoIds) {
        if (infoIds == null || infoIds.length == 0) {
            throw new ServiceException("日志ID不能为空");
        }

        for (Long infoId : infoIds) {
            deleteLoginLogById(infoId);
        }
    }

    /**
     * 清空登录日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanLoginLog() {
        loginLogMapper.cleanLoginLog();
    }

    /**
     * 查询登录日志列表（用于导出）
     */
    @Override
    public List<SysLoginLog> selectLoginLogList(String username, String ipaddr, String status,
                                                 String beginTime, String endTime) {
        return loginLogMapper.selectLoginLogList(username, ipaddr, status, beginTime, endTime);
    }
}
