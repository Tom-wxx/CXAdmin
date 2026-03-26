package com.admin.modules.workflow.service.impl;

import com.admin.modules.workflow.entity.*;
import com.admin.modules.workflow.mapper.*;
import com.admin.modules.workflow.service.IWorkflowService;
import com.admin.system.entity.SysUser;
import com.admin.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作流服务实现
 *
 * @author Admin
 */
@Service
public class WorkflowServiceImpl implements IWorkflowService {

    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_APPROVED = "approved";
    private static final String STATUS_REJECTED = "rejected";
    private static final String STATUS_CANCELLED = "cancelled";

    @Autowired
    private ProcessDefinitionMapper processDefinitionMapper;

    @Autowired
    private ProcessInstanceMapper processInstanceMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private WorkflowHistoryMapper historyMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Override
    @Transactional
    public Long startProcess(ProcessInstance processInstance) {
        // 1. 获取流程定义
        ProcessDefinition processDef = processDefinitionMapper.selectById(processInstance.getProcessDefId());
        if (processDef == null) {
            throw new RuntimeException("流程定义不存在");
        }
        if (!isDefinitionActive(processDef)) {
            throw new RuntimeException("流程定义未发布或已停用");
        }

        // 2. 生成实例编号
        String instanceNo = generateInstanceNo(processDef.getProcessType());
        processInstance.setInstanceNo(instanceNo);
        processInstance.setProcessKey(processDef.getProcessKey());
        processInstance.setProcessName(processDef.getProcessName());
        processInstance.setCurrentLevel(1);
        processInstance.setTotalLevel(processDef.getApprovalLevel());
        processInstance.setStatus(STATUS_PENDING);
        processInstance.setSubmitTime(new Date());

        // 3. 保存流程实例
        processInstanceMapper.insert(processInstance);

        // 4. 创建第一级审批任务
        createApprovalTasks(processInstance.getId(), processDef, 1);

        // 5. 记录历史
        saveHistory(processInstance.getId(), null, "submit", processInstance.getSubmitterId(),
                processInstance.getSubmitterName(), "发起申请");

        return processInstance.getId();
    }

    @Override
    @Transactional
    public boolean approve(Long taskId, String comment, Long userId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null || !task.getApproverId().equals(userId)) {
            throw new RuntimeException("任务不存在或无权限");
        }
        if (!STATUS_PENDING.equalsIgnoreCase(task.getStatus())) {
            throw new RuntimeException("任务已处理");
        }

        ProcessInstance instance = processInstanceMapper.selectById(task.getProcessInstanceId());
        if (instance == null) {
            throw new RuntimeException("流程不存在");
        }
        if (!STATUS_PENDING.equalsIgnoreCase(instance.getStatus())) {
            throw new RuntimeException("流程已结束或不可操作");
        }

        task.setStatus(STATUS_APPROVED);
        task.setApprovalTime(new Date());
        task.setApprovalComment(comment);
        taskMapper.updateById(task);

        saveHistory(task.getProcessInstanceId(), taskId, "approve", userId, task.getApproverName(), comment);

        if (isLevelCompleted(task.getProcessInstanceId(), task.getTaskLevel())) {
            if (task.getTaskLevel() < instance.getTotalLevel()) {
                ProcessDefinition processDef = processDefinitionMapper.selectById(instance.getProcessDefId());
                createApprovalTasks(instance.getId(), processDef, task.getTaskLevel() + 1);
                instance.setCurrentLevel(task.getTaskLevel() + 1);
                processInstanceMapper.updateById(instance);
            } else {
                instance.setStatus(STATUS_APPROVED);
                instance.setFinishTime(new Date());
                processInstanceMapper.updateById(instance);
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean reject(Long taskId, String comment, Long userId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null || !task.getApproverId().equals(userId)) {
            throw new RuntimeException("任务不存在或无权限");
        }
        if (!STATUS_PENDING.equalsIgnoreCase(task.getStatus())) {
            throw new RuntimeException("任务已处理");
        }

        ProcessInstance instance = processInstanceMapper.selectById(task.getProcessInstanceId());
        if (instance == null) {
            throw new RuntimeException("流程不存在");
        }
        if (!STATUS_PENDING.equalsIgnoreCase(instance.getStatus())) {
            throw new RuntimeException("流程已结束或不可操作");
        }

        task.setStatus(STATUS_REJECTED);
        task.setApprovalTime(new Date());
        task.setApprovalComment(comment);
        taskMapper.updateById(task);

        instance.setStatus(STATUS_REJECTED);
        instance.setFinishTime(new Date());
        processInstanceMapper.updateById(instance);

        saveHistory(task.getProcessInstanceId(), taskId, "reject", userId, task.getApproverName(), comment);

        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getProcessInstanceId, task.getProcessInstanceId())
                .eq(Task::getStatus, STATUS_PENDING);
        List<Task> pendingTasks = taskMapper.selectList(queryWrapper);
        for (Task pendingTask : pendingTasks) {
            pendingTask.setStatus(STATUS_REJECTED);
            taskMapper.updateById(pendingTask);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean cancel(Long instanceId, Long userId) {
        ProcessInstance instance = processInstanceMapper.selectById(instanceId);
        if (instance == null || !instance.getSubmitterId().equals(userId)) {
            throw new RuntimeException("流程不存在或无权限");
        }
        if (!STATUS_PENDING.equalsIgnoreCase(instance.getStatus())) {
            throw new RuntimeException("流程已结束或不可操作");
        }

        instance.setStatus(STATUS_CANCELLED);
        instance.setFinishTime(new Date());
        processInstanceMapper.updateById(instance);

        LambdaQueryWrapper<Task> query = new LambdaQueryWrapper<>();
        query.eq(Task::getProcessInstanceId, instanceId)
                .eq(Task::getStatus, STATUS_PENDING);
        List<Task> pendingTasks = taskMapper.selectList(query);
        for (Task task : pendingTasks) {
            task.setStatus(STATUS_CANCELLED);
            taskMapper.updateById(task);
        }

        saveHistory(instanceId, null, "cancel", userId, instance.getSubmitterName(), "取消申请");

        return true;
    }

    @Override
    public List<Task> getPendingTasks(Long userId) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getApproverId, userId)
                .eq(Task::getStatus, STATUS_PENDING)
                .orderByDesc(Task::getCreateTime);
        return taskMapper.selectList(queryWrapper);
    }

    @Override
    public List<Task> getCompletedTasks(Long userId) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getApproverId, userId)
                .in(Task::getStatus, Arrays.asList(STATUS_APPROVED, STATUS_REJECTED))
                .orderByDesc(Task::getApprovalTime);
        return taskMapper.selectList(queryWrapper);
    }

    @Override
    public List<ProcessInstance> getMyProcesses(Long userId) {
        LambdaQueryWrapper<ProcessInstance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProcessInstance::getSubmitterId, userId)
                .orderByDesc(ProcessInstance::getSubmitTime);
        return processInstanceMapper.selectList(queryWrapper);
    }

    @Override
    public Map<String, Object> getProcessDetail(Long instanceId) {
        Map<String, Object> result = new HashMap<>();

        ProcessInstance instance = processInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new RuntimeException("流程不存在");
        }
        result.put("instance", instance);

        LambdaQueryWrapper<Task> taskQuery = new LambdaQueryWrapper<>();
        taskQuery.eq(Task::getProcessInstanceId, instanceId)
                .orderByAsc(Task::getTaskLevel);
        List<Task> tasks = taskMapper.selectList(taskQuery);
        result.put("tasks", tasks);

        LambdaQueryWrapper<WorkflowHistory> historyQuery = new LambdaQueryWrapper<>();
        historyQuery.eq(WorkflowHistory::getProcessInstanceId, instanceId)
                .orderByAsc(WorkflowHistory::getOperationTime);
        List<WorkflowHistory> histories = historyMapper.selectList(historyQuery);
        result.put("histories", histories);

        return result;
    }

    private void createApprovalTasks(Long instanceId, ProcessDefinition processDef, int level) {
        List<SysUser> approvers = resolveApprovers(processDef);
        for (SysUser approver : approvers) {
            Task task = new Task();
            task.setProcessInstanceId(instanceId);
            task.setTaskName(processDef.getProcessName() + "-第" + level + "级审批");
            task.setTaskLevel(level);
            task.setApproverId(approver.getUserId());
            task.setApproverName(StringUtils.defaultIfBlank(approver.getNickname(), approver.getUsername()));
            task.setStatus(STATUS_PENDING);
            task.setCreateTime(new Date());
            taskMapper.insert(task);
        }
    }

    private boolean isLevelCompleted(Long instanceId, int level) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getProcessInstanceId, instanceId)
                .eq(Task::getTaskLevel, level)
                .eq(Task::getStatus, STATUS_PENDING);
        return taskMapper.selectCount(queryWrapper) == 0;
    }

    private void saveHistory(Long instanceId, Long taskId, String action, Long userId, String userName, String comment) {
        WorkflowHistory history = new WorkflowHistory();
        history.setProcessInstanceId(instanceId);
        history.setTaskId(taskId);
        history.setAction(action);
        history.setOperatorId(userId);
        history.setOperatorName(userName);
        history.setComment(comment);
        history.setOperationTime(new Date());
        historyMapper.insert(history);
    }

    private String generateInstanceNo(String processType) {
        return processType.toUpperCase() + "-" + System.currentTimeMillis();
    }

    private boolean isDefinitionActive(ProcessDefinition processDef) {
        String status = StringUtils.defaultString(processDef.getStatus());
        return "published".equalsIgnoreCase(status) || "1".equals(status) || "enabled".equalsIgnoreCase(status);
    }

    private List<Long> parseIds(String ids) {
        if (StringUtils.isBlank(ids)) {
            return Collections.emptyList();
        }
        return Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<SysUser> resolveApprovers(ProcessDefinition processDef) {
        String approverType = StringUtils.defaultIfBlank(processDef.getApproverType(), "role");
        if ("specified".equalsIgnoreCase(approverType)) {
            List<Long> userIds = parseIds(processDef.getApproverIds());
            List<SysUser> users = userIds.stream()
                    .map(userMapper::selectById)
                    .filter(Objects::nonNull)
                    .filter(user -> !"1".equals(user.getStatus()))
                    .collect(Collectors.toList());
            if (users.isEmpty()) {
                throw new RuntimeException("流程未配置有效审批人");
            }
            return users;
        }

        if ("role".equalsIgnoreCase(approverType)) {
            List<Long> roleIds = parseIds(processDef.getApproverRoles());
            if (roleIds.isEmpty()) {
                throw new RuntimeException("流程未配置审批角色");
            }
            List<SysUser> users = userMapper.selectUsersByRoleIds(roleIds);
            if (users == null || users.isEmpty()) {
                throw new RuntimeException("审批角色下暂无可用用户");
            }
            return users;
        }

        throw new RuntimeException("不支持的审批人类型");
    }

}
