package com.admin.modules.workflow.mapper;

import com.admin.modules.workflow.entity.ProcessInstance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程实例 Mapper
 *
 * @author Admin
 */
@Mapper
public interface ProcessInstanceMapper extends BaseMapper<ProcessInstance> {
}
