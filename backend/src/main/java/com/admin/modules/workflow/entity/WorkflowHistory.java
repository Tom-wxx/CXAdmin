package com.admin.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 审批历史记录实体
 *
 * @author Admin
 */
@Data
@TableName("wf_history")
public class WorkflowHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流程实例ID
     */
    private Long processInstanceId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作意见
     */
    private String comment;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operationTime;

    /**
     * 删除标志
     */
    private Integer deleted;

}
