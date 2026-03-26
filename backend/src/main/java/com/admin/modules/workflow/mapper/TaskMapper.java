package com.admin.modules.workflow.mapper;

import com.admin.modules.workflow.entity.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批任务 Mapper
 *
 * @author Admin
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
