package com.admin.system.mapper;

import com.admin.system.entity.SysLoginLog;
import com.admin.system.vo.GroupCountVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志 数据层
 *
 * @author Admin
 */
@Mapper
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

    /**
     * 查询登录日志列表（用于导出）
     */
    List<SysLoginLog> selectLoginLogList(@Param("username") String username,
                                          @Param("ipaddr") String ipaddr,
                                          @Param("status") String status,
                                          @Param("beginTime") String beginTime,
                                          @Param("endTime") String endTime);

    /**
     * 按天统计 since 之后的登录次数（groupKey = yyyy-MM-dd）
     */
    List<GroupCountVO> countByDay(@Param("since") LocalDateTime since);
}
