-- =====================================================
-- 信息发送模块数据库设计
-- 功能：支持邮件、短信等多种消息发送方式
-- 作者：Admin System
-- 日期：2025-11-28
-- =====================================================

USE admin_system;

-- ----------------------------
-- 1. 消息模板表
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message` (
  `message_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `message_name` varchar(100) NOT NULL COMMENT '消息名称',
  `message_code` varchar(100) NOT NULL COMMENT '消息编码（唯一标识）',
  `message_type` char(1) NOT NULL DEFAULT '1' COMMENT '消息类型（1邮件 2短信 3站内信 4微信）',
  `subject` varchar(200) DEFAULT NULL COMMENT '消息主题/标题',
  `content` text NOT NULL COMMENT '消息内容模板（支持变量占位符）',
  `variables` varchar(500) DEFAULT NULL COMMENT '可用变量说明（JSON格式）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` int(1) DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`message_id`),
  UNIQUE KEY `uk_message_code` (`message_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='消息模板表';

-- ----------------------------
-- 2. 消息配置表
-- ----------------------------
DROP TABLE IF EXISTS `sys_message_config`;
CREATE TABLE `sys_message_config` (
  `config_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `message_type` char(1) NOT NULL COMMENT '消息类型（1邮件 2短信 3站内信 4微信）',
  `config_name` varchar(100) NOT NULL COMMENT '配置名称',
  `config_data` text NOT NULL COMMENT '配置数据（JSON格式）',
  `is_default` char(1) DEFAULT '0' COMMENT '是否默认配置（0否 1是）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` int(1) DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`config_id`),
  KEY `idx_message_type` (`message_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='消息配置表';

-- ----------------------------
-- 3. 消息发送日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_message_log`;
CREATE TABLE `sys_message_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `message_id` bigint(20) DEFAULT NULL COMMENT '消息模板ID',
  `message_type` char(1) NOT NULL COMMENT '消息类型（1邮件 2短信 3站内信 4微信）',
  `receiver` varchar(200) NOT NULL COMMENT '接收者（邮箱/手机号/用户ID）',
  `subject` varchar(200) DEFAULT NULL COMMENT '消息主题',
  `content` text NOT NULL COMMENT '实际发送内容',
  `send_status` char(1) DEFAULT '0' COMMENT '发送状态（0成功 1失败 2发送中）',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `error_msg` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `retry_count` int(3) DEFAULT 0 COMMENT '重试次数',
  `send_by` bigint(20) DEFAULT NULL COMMENT '发送人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_message_type` (`message_type`),
  KEY `idx_receiver` (`receiver`),
  KEY `idx_send_time` (`send_time`),
  KEY `idx_send_status` (`send_status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='消息发送日志表';

-- ----------------------------
-- 初始化消息模板数据
-- ----------------------------
INSERT INTO `sys_message` VALUES
(1, '用户注册欢迎邮件', 'USER_REGISTER_EMAIL', '1', '欢迎注册管理系统',
 '尊敬的 ${username}：\n\n您好！欢迎注册我们的管理系统。\n\n您的账号信息如下：\n用户名：${username}\n注册时间：${registerTime}\n\n如有任何问题，请随时联系我们。\n\n祝您使用愉快！',
 '{"username":"用户名","registerTime":"注册时间"}', '0', NOW(), 1, NULL, NULL, '用户注册成功后发送的欢迎邮件', 0),

(2, '密码重置邮件', 'PASSWORD_RESET_EMAIL', '1', '密码重置通知',
 '尊敬的 ${username}：\n\n您好！您的密码已重置。\n\n新密码：${newPassword}\n\n为了账号安全，请尽快登录系统修改密码。\n\n如非本人操作，请立即联系管理员。',
 '{"username":"用户名","newPassword":"新密码"}', '0', NOW(), 1, NULL, NULL, '密码重置后发送的通知邮件', 0),

(3, '登录验证码短信', 'LOGIN_CODE_SMS', '2', NULL,
 '【管理系统】您的登录验证码是：${code}，有效期${expireMinutes}分钟，请勿泄露给他人。',
 '{"code":"验证码","expireMinutes":"有效期分钟数"}', '0', NOW(), 1, NULL, NULL, '登录时发送的验证码短信', 0),

(4, '系统公告通知', 'SYSTEM_NOTICE', '3', '${noticeTitle}',
 '${noticeContent}',
 '{"noticeTitle":"公告标题","noticeContent":"公告内容"}', '0', NOW(), 1, NULL, NULL, '系统公告站内信通知', 0);

-- ----------------------------
-- 初始化消息配置数据（示例配置）
-- ----------------------------
INSERT INTO `sys_message_config` VALUES
(1, '1', '默认邮件配置',
 '{"host":"smtp.qq.com","port":"587","username":"your-email@qq.com","password":"your-auth-code","from":"your-email@qq.com","fromName":"管理系统","ssl":true}',
 '1', '0', NOW(), 1, NULL, NULL, 'QQ邮箱SMTP配置示例', 0),

(2, '2', '默认短信配置',
 '{"provider":"aliyun","accessKeyId":"your-access-key","accessKeySecret":"your-access-secret","signName":"管理系统","templateCode":"SMS_123456"}',
 '1', '0', NOW(), 1, NULL, NULL, '阿里云短信配置示例', 0);

-- ----------------------------
-- 查看创建结果
-- ----------------------------
SELECT '✅ 消息系统数据库初始化完成！' AS '执行状态';
SELECT TABLE_NAME AS '表名', TABLE_COMMENT AS '说明'
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'admin_system'
  AND TABLE_NAME IN ('sys_message', 'sys_message_config', 'sys_message_log');
