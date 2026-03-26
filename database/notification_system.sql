-- ====================================================================
-- 站内消息/通知中心模块数据库脚本
-- ====================================================================

-- ----------------------------
-- 1. 站内通知表
-- ----------------------------
DROP TABLE IF EXISTS `sys_notification`;
CREATE TABLE `sys_notification` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT COMMENT '通知内容',
    `type` VARCHAR(50) NOT NULL DEFAULT 'system' COMMENT '通知类型（system:系统通知, todo:待办提醒, approval:审批消息, announce:公告）',
    `priority` VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '优先级（normal:普通, important:重要, urgent:紧急）',
    `status` VARCHAR(20) NOT NULL DEFAULT 'unread' COMMENT '状态（unread:未读, read:已读）',
    `user_id` BIGINT(20) NOT NULL COMMENT '接收用户ID',
    `sender_id` BIGINT(20) COMMENT '发送者ID（系统消息可为空）',
    `sender_name` VARCHAR(64) COMMENT '发送者名称',
    `link_url` VARCHAR(500) COMMENT '关联链接',
    `link_type` VARCHAR(50) COMMENT '链接类型',
    `read_time` DATETIME COMMENT '阅读时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted` INT(1) DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_type` (`type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='站内通知表';

-- ----------------------------
-- 2. 通知模板表
-- ----------------------------
DROP TABLE IF EXISTS `sys_notification_template`;
CREATE TABLE `sys_notification_template` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '模板ID',
    `template_code` VARCHAR(100) NOT NULL COMMENT '模板编码',
    `template_name` VARCHAR(200) NOT NULL COMMENT '模板名称',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题模板',
    `content` TEXT NOT NULL COMMENT '通知内容模板',
    `type` VARCHAR(50) NOT NULL DEFAULT 'system' COMMENT '通知类型',
    `priority` VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '优先级',
    `status` VARCHAR(20) NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新者',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted` INT(1) DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='通知模板表';

-- ----------------------------
-- 3. 初始化通知模板数据
-- 字段顺序: id, template_code, template_name, title, content, type, priority, status, create_time, create_by, update_time, update_by, remark, deleted
-- ----------------------------
INSERT INTO `sys_notification_template` VALUES
(1, 'USER_REGISTER', '用户注册通知', '欢迎加入系统', '尊敬的{userName}，欢迎您注册成为我们的用户！', 'system', 'normal', '1', NOW(), 1, NOW(), NULL, NULL, 0),
(2, 'PASSWORD_RESET', '密码重置通知', '密码已重置', '{userName}，您的密码已被管理员重置，请及时修改密码。', 'system', 'important', '1', NOW(), 1, NOW(), NULL, NULL, 0),
(3, 'ROLE_CHANGE', '角色变更通知', '您的角色已变更', '{userName}，您的角色已被修改为：{roleName}', 'system', 'normal', '1', NOW(), 1, NOW(), NULL, NULL, 0),
(4, 'APPROVAL_PENDING', '待审批通知', '您有新的审批待处理', '{userName}，您有一条来自{senderName}的审批待处理，请及时处理。', 'approval', 'important', '1', NOW(), 1, NOW(), NULL, NULL, 0),
(5, 'APPROVAL_APPROVED', '审批通过通知', '您的审批已通过', '{userName}，您提交的审批已通过。', 'approval', 'normal', '1', NOW(), 1, NOW(), NULL, NULL, 0),
(6, 'APPROVAL_REJECTED', '审批驳回通知', '您的审批已驳回', '{userName}，您提交的审批已被驳回，驳回原因：{reason}', 'approval', 'important', '1', NOW(), 1, NOW(), NULL, NULL, 0),
(7, 'SYSTEM_ANNOUNCE', '系统公告', '系统公告', '{content}', 'announce', 'urgent', '1', NOW(), 1, NOW(), NULL, NULL, 0);

-- ----------------------------
-- 4. 插入测试数据
-- 字段顺序: id, title, content, type, priority, status, user_id, sender_id, sender_name, link_url, link_type, read_time, create_time, create_by, update_time, update_by, remark, deleted
-- ----------------------------
INSERT INTO `sys_notification` VALUES
(1, '欢迎使用系统', '欢迎您使用本系统，如有任何问题请联系管理员。', 'system', 'normal', 'unread', 1, NULL, '系统', NULL, NULL, NULL, NOW(), 1, NOW(), NULL, NULL, 0),
(2, '密码安全提示', '为了您的账号安全，建议您定期修改密码。', 'system', 'important', 'unread', 1, NULL, '系统', '/user/profile', 'profile', NULL, NOW(), 1, NOW(), NULL, NULL, 0);
