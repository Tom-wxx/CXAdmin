package com.admin.modules.workflow.service;

import com.admin.modules.workflow.entity.ProcessInstance;
import com.admin.modules.workflow.entity.Task;

import java.util.List;
import java.util.Map;

/**
 * 工作流服务接口
 *
 * @author Admin
 */
public interface IWorkflowService {

    /**
     * 发起审批流程
     *
     * @param processInstance 流程实例
     * @return 流程实例ID
     */
    Long startProcess(ProcessInstance processInstance);

    /**
     * 审批通过
     *
     * @param taskId 任务ID
     * @param comment 审批意见
     * @param userId 审批人ID
     * @return 是否成功
     */
    boolean approve(Long taskId, String comment, Long userId);

    /**
     * 审批驳回
     *
     * @param taskId 任务ID
     * @param comment 驳回原因
     * @param userId 审批人ID
     * @return 是否成功
     */
    boolean reject(Long taskId, String comment, Long userId);

    /**
     * 取消流程
     *
     * @param instanceId 流程实例ID
     * @param userId 操作人ID
     * @return 是否成功
     */
    boolean cancel(Long instanceId, Long userId);

    /**
     * 获取待办任务列表
     *
     * @param userId 用户ID
     * @return 任务列表
     */
    List<Task> getPendingTasks(Long userId);

    /**
     * 获取已办任务列表
     *
     * @param userId 用户ID
     * @return 任务列表
     */
    List<Task> getCompletedTasks(Long userId);

    /**
     * 获取我发起的流程
     *
     * @param userId 用户ID
     * @return 流程实例列表
     */
    List<ProcessInstance> getMyProcesses(Long userId);

    /**
     * 获取流程详情（包含审批历史）
     *
     * @param instanceId 流程实例ID
     * @return 流程详情
     */
    Map<String, Object> getProcessDetail(Long instanceId);

}
