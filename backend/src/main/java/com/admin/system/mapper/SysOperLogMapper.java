package com.admin.system.mapper;

import com.admin.system.entity.SysOperLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志记录 数据层
 *
 * @author Admin
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {

    /**
     * 分页查询操作日志列表
     */
    Page<SysOperLog> selectOperLogPage(Page<SysOperLog> page,
                                        @Param("title") String title,
                                        @Param("operName") String operName,
                                        @Param("businessType") Integer businessType,
                                        @Param("status") Integer status,
                                        @Param("beginTime") String beginTime,
                                        @Param("endTime") String endTime);

    /**
     * 清空操作日志
     */
    int cleanOperLog();

    /**
     * 查询操作日志列表（用于导出）
     */
    List<SysOperLog> selectOperLogList(@Param("title") String title,
                                        @Param("operName") String operName,
                                        @Param("businessType") Integer businessType,
                                        @Param("status") Integer status,
                                        @Param("beginTime") String beginTime,
                                        @Param("endTime") String endTime);
}
