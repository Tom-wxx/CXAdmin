package com.admin.modules.workflow.mapper;

import com.admin.modules.workflow.entity.ProcessDefinition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程定义 Mapper
 *
 * @author Admin
 */
@Mapper
public interface ProcessDefinitionMapper extends BaseMapper<ProcessDefinition> {
}
