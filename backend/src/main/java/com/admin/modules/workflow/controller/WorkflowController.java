package com.admin.modules.workflow.controller;

import com.admin.modules.workflow.entity.ProcessInstance;
import com.admin.modules.workflow.entity.Task;
import com.admin.modules.workflow.service.IWorkflowService;
import com.admin.system.common.Result;
import com.admin.system.security.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工作流控制器
 *
 * @author Admin
 */
@RestController
@RequestMapping("/workflow")
@Tag(name = "工作流管理")
public class WorkflowController {

    @Autowired
    private IWorkflowService workflowService;

    /**
     * 发起审批流程
     */
    @PostMapping("/start")
    @PreAuthorize("@ss.hasPermi('workflow:process:start')")
    @Operation(summary = "发起审批流程")
    public Result<Long> startProcess(@RequestBody ProcessInstance processInstance,
                                      @AuthenticationPrincipal LoginUser loginUser) {
        processInstance.setSubmitterId(loginUser.getUser().getUserId());
        processInstance.setSubmitterName(loginUser.getUser().getNickname());
        Long instanceId = workflowService.startProcess(processInstance);
        return Result.success("流程发起成功", instanceId);
    }

    /**
     * 审批通过
     */
    @PostMapping("/approve/{taskId}")
    @PreAuthorize("@ss.hasPermi('workflow:task:approve')")
    @Operation(summary = "审批通过")
    public Result<Void> approve(@PathVariable Long taskId,
                                @RequestParam(required = false) String comment,
                                @AuthenticationPrincipal LoginUser loginUser) {
        workflowService.approve(taskId, comment, loginUser.getUser().getUserId());
        return Result.success("审批通过");
    }

    /**
     * 审批驳回
     */
    @PostMapping("/reject/{taskId}")
    @PreAuthorize("@ss.hasPermi('workflow:task:reject')")
    @Operation(summary = "审批驳回")
    public Result<Void> reject(@PathVariable Long taskId,
                               @RequestParam String comment,
                               @AuthenticationPrincipal LoginUser loginUser) {
        workflowService.reject(taskId, comment, loginUser.getUser().getUserId());
        return Result.success("已驳回");
    }

    /**
     * 取消流程
     */
    @PostMapping("/cancel/{instanceId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:cancel')")
    @Operation(summary = "取消流程")
    public Result<Void> cancel(@PathVariable Long instanceId,
                               @AuthenticationPrincipal LoginUser loginUser) {
        workflowService.cancel(instanceId, loginUser.getUser().getUserId());
        return Result.success("流程已取消");
    }

    /**
     * 获取待办任务列表
     */
    @GetMapping("/pending")
    @PreAuthorize("@ss.hasPermi('workflow:task:list')")
    @Operation(summary = "获取待办任务")
    public Result<List<Task>> getPendingTasks(@AuthenticationPrincipal LoginUser loginUser) {
        List<Task> tasks = workflowService.getPendingTasks(loginUser.getUser().getUserId());
        return Result.success(tasks);
    }

    /**
     * 获取已办任务列表
     */
    @GetMapping("/completed")
    @PreAuthorize("@ss.hasPermi('workflow:task:list')")
    @Operation(summary = "获取已办任务")
    public Result<List<Task>> getCompletedTasks(@AuthenticationPrincipal LoginUser loginUser) {
        List<Task> tasks = workflowService.getCompletedTasks(loginUser.getUser().getUserId());
        return Result.success(tasks);
    }

    /**
     * 获取我发起的流程
     */
    @GetMapping("/my-processes")
    @PreAuthorize("@ss.hasPermi('workflow:process:list')")
    @Operation(summary = "获取我发起的流程")
    public Result<List<ProcessInstance>> getMyProcesses(@AuthenticationPrincipal LoginUser loginUser) {
        List<ProcessInstance> processes = workflowService.getMyProcesses(loginUser.getUser().getUserId());
        return Result.success(processes);
    }

    /**
     * 获取流程详情
     */
    @GetMapping("/detail/{instanceId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:query')")
    @Operation(summary = "获取流程详情")
    public Result<Map<String, Object>> getProcessDetail(@PathVariable Long instanceId) {
        Map<String, Object> detail = workflowService.getProcessDetail(instanceId);
        return Result.success(detail);
    }
}
