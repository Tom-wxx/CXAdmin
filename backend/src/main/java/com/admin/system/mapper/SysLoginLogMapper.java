package com.admin.system.mapper;

import com.admin.system.entity.SysLoginLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 登录日志 数据层
 *
 * @author Admin
 */
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {

    /**
     * 分页查询登录日志列表
     */
    Page<SysLoginLog> selectLoginLogPage(Page<SysLoginLog> page,
                                          @Param("username") String username,
                                          @Param("ipaddr") String ipaddr,
                                          @Param("status") String status,
                                          @Param("beginTime") String beginTime,
                                          @Param("endTime") String endTime);

    /**
     * 清空登录日志
     */
    int cleanLoginLog();
}
