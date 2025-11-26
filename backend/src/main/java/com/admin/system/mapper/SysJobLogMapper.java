package com.admin.system.mapper;

import com.admin.system.entity.SysJobLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务调度日志表 数据层
 *
 * @author Admin
 */
@Mapper
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {

}
