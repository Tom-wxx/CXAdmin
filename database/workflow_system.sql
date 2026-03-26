-- ====================================================================
-- 工作流审批系统数据库脚本
-- ====================================================================

-- ----------------------------
-- 1. 审批流程定义表
-- ----------------------------
DROP TABLE IF EXISTS `wf_process_definition`;
CREATE TABLE `wf_process_definition` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '流程ID',
    `process_key` VARCHAR(100) NOT NULL COMMENT '流程标识',
    `process_name` VARCHAR(200) NOT NULL COMMENT '流程名称',
    `process_type` VARCHAR(50) NOT NULL COMMENT '流程类型（leave:请假, expense:报销, purchase:采购）',
    `description` TEXT COMMENT '流程描述',
    `approver_type` VARCHAR(20) NOT NULL DEFAULT 'specified' COMMENT '审批人类型（specified:指定人员, role:指定角色, dept_leader:部门领导, custom:自定义）',
    `approver_ids` VARCHAR(500) COMMENT '审批人ID列表（逗号分隔）',
    `approver_roles` VARCHAR(500) COMMENT '审批角色ID列表（逗号分隔）',
    `approval_level` INT(2) DEFAULT 1 COMMENT '审批层级（1:一级, 2:二级, 3:三级）',
    `status` VARCHAR(20) NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
    `sort_order` INT(4) DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted` INT(1) DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_process_key` (`process_key`),
    KEY `idx_process_type` (`process_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批流程定义表';

-- ----------------------------
-- 2. 审批流程实例表
-- ----------------------------
DROP TABLE IF EXISTS `wf_process_instance`;
CREATE TABLE `wf_process_instance` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '实例ID',
    `process_def_id` BIGINT(20) NOT NULL COMMENT '流程定义ID',
    `process_key` VARCHAR(100) NOT NULL COMMENT '流程标识',
    `process_name` VARCHAR(200) NOT NULL COMMENT '流程名称',
    `instance_no` VARCHAR(50) NOT NULL COMMENT '实例编号',
    `title` VARCHAR(200) NOT NULL COMMENT '申请标题',
    `content` TEXT COMMENT '申请内容',
    `form_data` TEXT COMMENT '表单数据（JSON格式）',
    `current_level` INT(2) DEFAULT 1 COMMENT '当前审批层级',
    `total_level` INT(2) DEFAULT 1 COMMENT '总审批层级',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态（pending:待审批, approved:已通过, rejected:已驳回, cancelled:已取消）',
    `submitter_id` BIGINT(20) NOT NULL COMMENT '提交人ID',
    `submitter_name` VARCHAR(64) COMMENT '提交人姓名',
    `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `finish_time` DATETIME COMMENT '完成时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted` INT(1) DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_instance_no` (`instance_no`),
    KEY `idx_process_def_id` (`process_def_id`),
    KEY `idx_submitter_id` (`submitter_id`),
    KEY `idx_status` (`status`),
    KEY `idx_submit_time` (`submit_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批流程实例表';

-- ----------------------------
-- 3. 审批任务表
-- ----------------------------
DROP TABLE IF EXISTS `wf_task`;
CREATE TABLE `wf_task` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `process_instance_id` BIGINT(20) NOT NULL COMMENT '流程实例ID',
    `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
    `task_level` INT(2) NOT NULL COMMENT '任务层级',
    `approver_id` BIGINT(20) NOT NULL COMMENT '审批人ID',
    `approver_name` VARCHAR(64) COMMENT '审批人姓名',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态（pending:待处理, approved:已通过, rejected:已驳回）',
    `approval_time` DATETIME COMMENT '审批时间',
    `approval_comment` TEXT COMMENT '审批意见',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `deleted` INT(1) DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (`id`),
    KEY `idx_process_instance_id` (`process_instance_id`),
    KEY `idx_approver_id` (`approver_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批任务表';

-- ----------------------------
-- 4. 审批历史记录表
-- ----------------------------
DROP TABLE IF EXISTS `wf_history`;
CREATE TABLE `wf_history` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `process_instance_id` BIGINT(20) NOT NULL COMMENT '流程实例ID',
    `task_id` BIGINT(20) COMMENT '任务ID',
    `action` VARCHAR(50) NOT NULL COMMENT '操作类型（submit:提交, approve:通过, reject:驳回, cancel:取消）',
    `operator_id` BIGINT(20) NOT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(64) COMMENT '操作人姓名',
    `comment` TEXT COMMENT '操作意见',
    `operation_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `deleted` INT(1) DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (`id`),
    KEY `idx_process_instance_id` (`process_instance_id`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批历史记录表';

-- ----------------------------
-- 5. 初始化流程定义数据
-- ----------------------------
INSERT INTO `wf_process_definition` VALUES
(1, 'leave_process', '请假审批流程', 'leave', '员工请假申请流程', 'role', NULL, '2', 1, '1', 1, NOW(), 1, NOW(), NULL, '部门主管审批', 0),
(2, 'expense_process', '报销审批流程', 'expense', '费用报销申请流程', 'role', NULL, '2', 2, '1', 2, NOW(), 1, NOW(), NULL, '一级审批：部门主管，二级审批：财务', 0),
(3, 'purchase_process', '采购审批流程', 'purchase', '物品采购申请流程', 'role', NULL, '2', 2, '1', 3, NOW(), 1, NOW(), NULL, '一级审批：部门主管，二级审批：总经理', 0);
