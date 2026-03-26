package com.admin.modules.workflow.mapper;

import com.admin.modules.workflow.entity.WorkflowHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批历史 Mapper
 *
 * @author Admin
 */
@Mapper
public interface WorkflowHistoryMapper extends BaseMapper<WorkflowHistory> {
}
