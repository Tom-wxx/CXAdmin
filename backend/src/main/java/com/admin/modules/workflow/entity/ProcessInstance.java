package com.admin.modules.workflow.entity;

import com.admin.system.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 审批流程实例实体
 *
 * @author Admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_process_instance")
public class ProcessInstance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 实例ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流程定义ID
     */
    @NotNull(message = "流程定义ID不能为空")
    private Long processDefId;

    /**
     * 流程标识
     */
    private String processKey;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 实例编号
     */
    private String instanceNo;

    /**
     * 申请标题
     */
    @NotBlank(message = "申请标题不能为空")
    private String title;

    /**
     * 申请内容
     */
    private String content;

    /**
     * 表单数据（JSON格式）
     */
    private String formData;

    /**
     * 当前审批层级
     */
    private Integer currentLevel;

    /**
     * 总审批层级
     */
    private Integer totalLevel;

    /**
     * 状态
     */
    private String status;

    /**
     * 提交人ID
     */
    private Long submitterId;

    /**
     * 提交人姓名
     */
    private String submitterName;

    /**
     * 提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

}
