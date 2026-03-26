package com.admin.modules.workflow.entity;

import com.admin.system.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 审批流程定义实体
 *
 * @author Admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_process_definition")
public class ProcessDefinition extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 流程ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流程标识
     */
    @NotBlank(message = "流程标识不能为空")
    private String processKey;

    /**
     * 流程名称
     */
    @NotBlank(message = "流程名称不能为空")
    private String processName;

    /**
     * 流程类型
     */
    @NotBlank(message = "流程类型不能为空")
    private String processType;

    /**
     * 流程描述
     */
    private String description;

    /**
     * 审批人类型
     */
    private String approverType;

    /**
     * 审批人ID列表
     */
    private String approverIds;

    /**
     * 审批角色ID列表
     */
    private String approverRoles;

    /**
     * 审批层级
     */
    private Integer approvalLevel;

    /**
     * 状态（0停用 1启用）
     */
    private String status;

    /**
     * 排序
     */
    private Integer sortOrder;

}
