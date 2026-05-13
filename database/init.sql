-- ====================================================================
-- Admin System - Consolidated Database Init Script
-- ====================================================================
-- Merged from: initData.sql, *_system.sql, add_*.sql (in dependency order)
-- Usage:
--   mysql -u root -p -e "CREATE DATABASE admin_system DEFAULT CHARACTER SET utf8mb4;"
--   mysql -u root -p admin_system < database/init.sql
--
-- Order:
--   1. initData.sql                      Core RBAC schema + seed data
--   2. notification_system.sql           Notification module tables
--   3. message_system.sql                Message module tables
--   4. workflow_system.sql               Workflow module tables
--   5. add_notification_permissions.sql  Notification menus / perms
--   6. add_message_permissions.sql       Message menus / perms
--   7. add_workflow_permissions.sql      Workflow menus / perms
--   8. add_cache_monitor_permissions.sql Cache-monitor menu / perms
--   9. add_job_permissions.sql           Job module button perms
--  10. add_statistics_permissions.sql    Statistics menus / perms
--  11. add_schedule_jobs.sql             Sample scheduled job rows
-- ====================================================================



-- ====================================================================
-- Section: initData.sql
-- ====================================================================

/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50717
 Source Host           : localhost:3306
 Source Schema         : admin_system

 Target Server Type    : MySQL
 Target Server Version : 50717
 File Encoding         : 65001

 Date: 28/11/2025 13:17:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者（用户ID）',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者（用户ID）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` int(1) NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', '2025-11-25 11:11:39', 1, NULL, NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow', 0);
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', '2025-11-25 11:11:39', 1, NULL, NULL, '初始化密码 123456', 0);
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', '2025-11-25 11:11:39', 1, NULL, NULL, '深色主题theme-dark，浅色主题theme-light', 0);

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者（用户ID）',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者（用户ID）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` int(1) NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 108 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '0', '科技公司', 0, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '深圳总公司', 1, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '北京分公司', 2, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (103, 101, '0,100,101', '研发部门', 1, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (104, 101, '0,100,101', '市场部门', 2, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (105, 101, '0,100,101', '测试部门', 3, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (106, 101, '0,100,101', '财务部门', 4, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (107, 101, '0,100,101', '运维部门', 5, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者（用户ID）',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者（用户ID）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` int(1) NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '性别男', 0);
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '性别女', 0);
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '性别未知', 0);
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '显示菜单', 0);
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '隐藏菜单', 0);
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '正常状态', 0);
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '停用状态', 0);
INSERT INTO `sys_dict_data` VALUES (8, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '系统默认是', 0);
INSERT INTO `sys_dict_data` VALUES (9, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '系统默认否', 0);
INSERT INTO `sys_dict_data` VALUES (10, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '通知', 0);
INSERT INTO `sys_dict_data` VALUES (11, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '公告', 0);
INSERT INTO `sys_dict_data` VALUES (12, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '正常状态', 0);
INSERT INTO `sys_dict_data` VALUES (13, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '关闭状态', 0);
INSERT INTO `sys_dict_data` VALUES (14, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '新增操作', 0);
INSERT INTO `sys_dict_data` VALUES (15, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '修改操作', 0);
INSERT INTO `sys_dict_data` VALUES (16, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '删除操作', 0);
INSERT INTO `sys_dict_data` VALUES (17, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '授权操作', 0);
INSERT INTO `sys_dict_data` VALUES (18, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '导出操作', 0);
INSERT INTO `sys_dict_data` VALUES (19, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '导入操作', 0);
INSERT INTO `sys_dict_data` VALUES (20, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '强退操作', 0);
INSERT INTO `sys_dict_data` VALUES (21, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '生成操作', 0);
INSERT INTO `sys_dict_data` VALUES (22, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '清空操作', 0);
INSERT INTO `sys_dict_data` VALUES (23, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '正常状态', 0);
INSERT INTO `sys_dict_data` VALUES (24, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '停用状态', 0);

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者（用户ID）',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者（用户ID）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` int(1) NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '用户性别列表', 0);
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '菜单状态列表', 0);
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '系统开关列表', 0);
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '任务状态列表', 0);
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '任务分组列表', 0);
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '系统是否列表', 0);
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '通知类型列表', 0);
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '通知状态列表', 0);
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '操作类型列表', 0);
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '登录状态列表', 0);

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `file_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名称',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件存储路径',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件访问URL',
  `file_size` bigint(20) NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `file_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件类型（MIME类型）',
  `file_ext` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件扩展名',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'other' COMMENT '文件分类（image/document/video/audio/other）',
  `storage_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'local' COMMENT '存储类型（local/oss/cos等）',
  `md5` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件MD5值',
  `download_count` int(11) NULL DEFAULT 0 COMMENT '下载次数',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1禁用）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者（用户ID）',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者（用户ID）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` int(1) NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `idx_file_name`(`file_name`) USING BTREE,
  INDEX `idx_category`(`category`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_md5`(`md5`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_file
-- ----------------------------
INSERT INTO `sys_file` VALUES (2, '5987656f784b4e5ea9b39b09844d4e87.jpg', 'v2-d6f44389971daab7e688e5b37046e4e4_720w.jpg', 'D:\\uploads\\2025\\11\\26\\5987656f784b4e5ea9b39b09844d4e87.jpg', '/uploads/2025/11/26/5987656f784b4e5ea9b39b09844d4e87.jpg', 43915, 'image/jpeg', 'jpg', 'image', 'local', 'a8e54d5eea7c910d03660ed14d537767', 0, '0', '2025-11-26 16:25:38', 1, '2025-11-28 12:14:38', 1, NULL, 1);
INSERT INTO `sys_file` VALUES (3, '3c02bc9c68ac4d51aeea2145941a7dd9.jpg', 'v2-d6f44389971daab7e688e5b37046e4e4_720w.jpg', 'C:\\uploads\\2025\\11\\28\\3c02bc9c68ac4d51aeea2145941a7dd9.jpg', '/api/uploads/2025/11/28/3c02bc9c68ac4d51aeea2145941a7dd9.jpg', 43915, 'image/jpeg', 'jpg', 'image', 'local', 'a8e54d5eea7c910d03660ed14d537767', 0, '0', '2025-11-28 12:14:56', 1, '2025-11-28 12:35:54', 1, NULL, 1);
INSERT INTO `sys_file` VALUES (4, 'cddf1ba9c3fb4601b9ed9441ddf7b4f9.jpg', '微信图片_20251123182925_17_2.jpg', 'C:\\uploads\\2025\\11\\28\\cddf1ba9c3fb4601b9ed9441ddf7b4f9.jpg', '/api/uploads/2025/11/28/cddf1ba9c3fb4601b9ed9441ddf7b4f9.jpg', 747589, 'image/jpeg', 'jpg', 'image', 'local', 'c5641cc63c832f52929f93b4e7a95778', 0, '0', '2025-11-28 12:15:45', 1, '2025-11-28 12:36:20', 1, NULL, 1);
INSERT INTO `sys_file` VALUES (5, 'bcdf3a3472b14380a4984421fb0c7466.jpg', 'v2-d6f44389971daab7e688e5b37046e4e4_720w.jpg', 'D:\\uploads\\2025\\11\\28\\bcdf3a3472b14380a4984421fb0c7466.jpg', '/api/uploads/2025/11/28/bcdf3a3472b14380a4984421fb0c7466.jpg', 43915, 'image/jpeg', 'jpg', 'image', 'local', 'a8e54d5eea7c910d03660ed14d537767', 0, '0', '2025-11-28 12:35:54', 1, '2025-11-28 13:00:51', 1, '头像展示 - 管理员', 0);
INSERT INTO `sys_file` VALUES (6, '6e0a52c268d14af19e72fb0828d85549.jpg', '微信图片_20251123182925_17_2.jpg', 'D:\\uploads\\2025\\11\\28\\6e0a52c268d14af19e72fb0828d85549.jpg', '/api/uploads/2025/11/28/6e0a52c268d14af19e72fb0828d85549.jpg', 747589, 'image/jpeg', 'jpg', 'image', 'local', 'c5641cc63c832f52929f93b4e7a95778', 0, '0', '2025-11-28 12:36:20', 1, '2025-11-28 12:36:47', 1, '头像展示 - 管理员', 1);

-- ----------------------------
-- Table structure for sys_file_category
-- ----------------------------
DROP TABLE IF EXISTS `sys_file_category`;
CREATE TABLE `sys_file_category`  (
  `category_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `category_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类编码',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父分类ID',
  `sort_order` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类图标',
  `max_size` bigint(20) NULL DEFAULT NULL COMMENT '该分类允许的最大文件大小（字节）',
  `allowed_types` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '允许的文件类型（逗号分隔）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者（用户ID）',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者（用户ID）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` int(1) NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `uk_category_code`(`category_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_file_category
-- ----------------------------
INSERT INTO `sys_file_category` VALUES (1, '图片文件', 'image', 0, 1, 'el-icon-picture', 10485760, 'jpg,jpeg,png,gif,bmp,webp', '0', '2025-11-26 10:59:10', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_file_category` VALUES (2, '文档文件', 'document', 0, 2, 'el-icon-document', 52428800, 'doc,docx,xls,xlsx,ppt,pptx,pdf,txt', '0', '2025-11-26 10:59:10', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_file_category` VALUES (3, '视频文件', 'video', 0, 3, 'el-icon-video-camera', 104857600, 'mp4,avi,mov,wmv,flv,mkv', '0', '2025-11-26 10:59:10', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_file_category` VALUES (4, '音频文件', 'audio', 0, 4, 'el-icon-headset', 20971520, 'mp3,wav,flac,aac,wma', '0', '2025-11-26 10:59:10', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_file_category` VALUES (5, '压缩文件', 'archive', 0, 5, 'el-icon-folder-opened', 104857600, 'zip,rar,7z,tar,gz', '0', '2025-11-26 10:59:10', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_file_category` VALUES (6, '其他文件', 'other', 0, 6, 'el-icon-files', 52428800, '*', '0', '2025-11-26 10:59:10', 1, NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for sys_file_statistics
-- ----------------------------
DROP TABLE IF EXISTS `sys_file_statistics`;
CREATE TABLE `sys_file_statistics`  (
  `stat_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `total_files` bigint(20) NULL DEFAULT 0 COMMENT '文件总数',
  `total_size` bigint(20) NULL DEFAULT 0 COMMENT '总大小（字节）',
  `image_count` bigint(20) NULL DEFAULT 0 COMMENT '图片数量',
  `image_size` bigint(20) NULL DEFAULT 0 COMMENT '图片大小',
  `document_count` bigint(20) NULL DEFAULT 0 COMMENT '文档数量',
  `document_size` bigint(20) NULL DEFAULT 0 COMMENT '文档大小',
  `video_count` bigint(20) NULL DEFAULT 0 COMMENT '视频数量',
  `video_size` bigint(20) NULL DEFAULT 0 COMMENT '视频大小',
  `audio_count` bigint(20) NULL DEFAULT 0 COMMENT '音频数量',
  `audio_size` bigint(20) NULL DEFAULT 0 COMMENT '音频大小',
  `other_count` bigint(20) NULL DEFAULT 0 COMMENT '其他数量',
  `other_size` bigint(20) NULL DEFAULT 0 COMMENT '其他大小',
  PRIMARY KEY (`stat_id`) USING BTREE,
  UNIQUE INDEX `uk_stat_date`(`stat_date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者（用户ID）',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者（用户ID）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注信息',
  `deleted` int(1) NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '异常信息',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`  (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime(0) NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由参数',
  `is_frame` int(1) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者（用户ID）',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者（用户ID）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  `deleted` int(1) NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1142 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'setting', '2025-11-25 11:11:39', 1, NULL, NULL, '系统管理目录', 0);
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', 1, 0, 'M', '0', '0', '', 'monitor', '2025-11-25 11:11:39', 1, NULL, NULL, '系统监控目录', 0);
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 3, 'tool', NULL, '', 1, 0, 'M', '0', '0', '', 'suitcase', '2025-11-25 11:11:39', 1, NULL, NULL, '系统工具目录', 0);
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', '2025-11-25 11:11:39', 1, NULL, NULL, '用户管理菜单', 0);
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'user-solid', '2025-11-25 11:11:39', 1, NULL, NULL, '角色管理菜单', 0);
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'menu', '2025-11-25 11:11:39', 1, NULL, NULL, '菜单管理菜单', 0);
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'office-building', '2025-11-25 11:11:39', 1, NULL, NULL, '部门管理菜单', 0);
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'postcard', '2025-11-25 11:11:39', 1, NULL, NULL, '岗位管理菜单', 0);
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'collection', '2025-11-25 11:11:39', 1, NULL, NULL, '字典管理菜单', 0);
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', '2025-11-25 11:11:39', 1, NULL, NULL, '参数设置菜单', 0);
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'bell', '2025-11-25 11:11:39', 1, NULL, NULL, '通知公告菜单', 0);
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', '', 1, 0, 'M', '0', '0', '', 'document', '2025-11-25 11:11:39', 1, NULL, NULL, '日志管理菜单', 0);
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'user-solid', '2025-11-25 11:11:39', 1, NULL, NULL, '在线用户菜单', 0);
INSERT INTO `sys_menu` VALUES (110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'time', '2025-11-25 11:11:39', 1, NULL, NULL, '定时任务菜单', 0);
INSERT INTO `sys_menu` VALUES (111, 'Druid监控', 2, 3, 'druid', 'monitor/druid/index', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'data-analysis', '2025-11-25 11:11:39', 1, NULL, NULL, '数据监控菜单', 0);
INSERT INTO `sys_menu` VALUES (112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'cpu', '2025-11-25 11:11:39', 1, NULL, NULL, '服务监控菜单', 0);
INSERT INTO `sys_menu` VALUES (113, '表单构建', 3, 1, 'build', 'tool/build/index', '', 1, 0, 'C', '1', '0', 'tool:build:list', 'edit-outline', '2025-11-25 11:11:39', 1, NULL, NULL, '表单构建菜单(暂未实现)', 0);
INSERT INTO `sys_menu` VALUES (114, '代码生成', 3, 2, 'generator', 'tool/generator/index', '', 1, 0, 'C', '0', '0', 'tool:generator:list', 'tickets', '2025-11-25 11:11:39', 1, NULL, NULL, '代码生成菜单', 0);
INSERT INTO `sys_menu` VALUES (115, '系统接口', 2, 6, 'swagger', 'monitor/swagger/index', '', 1, 0, 'C', '0', '0', 'monitor:swagger:list', 'document', '2025-11-25 11:11:39', 1, NULL, NULL, '系统接口菜单（Swagger）', 0);
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'notebook-2', '2025-11-25 11:11:39', 1, NULL, NULL, '操作日志菜单', 0);
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'loginlog', 'monitor/loginlog/index', '', 1, 0, 'C', '0', '0', 'monitor:loginlog:list', 'key', '2025-11-25 11:11:39', 1, NULL, NULL, '登录日志菜单', 0);
INSERT INTO `sys_menu` VALUES (1000, '用户查询', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1001, '用户新增', 100, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1002, '用户修改', 100, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1003, '用户删除', 100, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1004, '用户导出', 100, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1005, '用户导入', 100, 6, '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1006, '重置密码', 100, 7, '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1007, '角色查询', 101, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1008, '角色新增', 101, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1009, '角色修改', 101, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1010, '角色删除', 101, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1011, '角色导出', 101, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1012, '菜单查询', 102, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1013, '菜单新增', 102, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1014, '菜单修改', 102, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1015, '菜单删除', 102, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1016, '部门查询', 103, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1017, '部门新增', 103, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1018, '部门修改', 103, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1019, '部门删除', 103, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1020, '岗位查询', 104, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1021, '岗位新增', 104, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1022, '岗位修改', 104, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1023, '岗位删除', 104, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1024, '岗位导出', 104, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1031, '文件管理', 0, 5, 'file', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'folder-opened', '2025-11-26 11:08:42', 1, NULL, NULL, '文件管理目录', 0);
INSERT INTO `sys_menu` VALUES (1032, '文件列表', 1031, 1, 'list', 'system/file/index', NULL, 1, 0, 'C', '0', '0', 'system:file:list', 'files', '2025-11-26 11:08:42', 1, NULL, NULL, '文件列表菜单', 0);
INSERT INTO `sys_menu` VALUES (1033, '文件上传', 1032, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:file:upload', '#', '2025-11-26 11:08:42', 1, NULL, NULL, '文件上传按钮', 0);
INSERT INTO `sys_menu` VALUES (1034, '文件下载', 1032, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:file:download', '#', '2025-11-26 11:08:42', 1, NULL, NULL, '文件下载按钮', 0);
INSERT INTO `sys_menu` VALUES (1035, '文件删除', 1032, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:file:remove', '#', '2025-11-26 11:08:42', 1, NULL, NULL, '文件删除按钮', 0);
INSERT INTO `sys_menu` VALUES (1036, '文件详情', 1032, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:file:query', '#', '2025-11-26 11:08:42', 1, NULL, NULL, '文件详情按钮', 0);
INSERT INTO `sys_menu` VALUES (1037, '个人中心', 0, 100, 'profile', 'user/profile/index', NULL, 1, 0, 'C', '1', '0', NULL, 'user', '2025-11-26 11:08:42', 1, '2025-11-26 14:39:09', NULL, '个人中心仅通过顶部用户菜单访问，已从侧边栏移除', 1);
INSERT INTO `sys_menu` VALUES (1038, '服务器监控', 2, 1, 'server', 'monitor/server/index', NULL, 1, 0, 'C', '0', '0', 'monitor:server:view', 'monitor', '2025-11-26 11:08:42', 1, NULL, NULL, '服务器监控', 1);
INSERT INTO `sys_menu` VALUES (1140, '代码生成查询', 114, 1, '', '', '', 1, 0, 'F', '0', '0', 'tool:generator:query', '#', '2025-11-27 11:19:47', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1141, '代码生成执行', 114, 2, '', '', '', 1, 0, 'F', '0', '0', 'tool:generator:code', '#', '2025-11-27 11:19:47', 1, NULL, NULL, '', 0);

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者（用户ID）',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者（用户ID）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` int(1) NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int(1) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`oper_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者（用户ID）',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者（用户ID）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` int(1) NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 'ceo', '董事长', 1, '0', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_post` VALUES (2, 'se', '项目经理', 2, '0', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_post` VALUES (3, 'hr', '人力资源', 3, '0', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_post` VALUES (4, 'user', '普通员工', 4, '0', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者（用户ID）',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者（用户ID）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` int(1) NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '2025-11-25 11:11:39', 1, NULL, NULL, '超级管理员', 0);
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '2025-11-25 11:11:39', 1, NULL, NULL, '普通角色', 0);

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1);
INSERT INTO `sys_role_menu` VALUES (1, 2);
INSERT INTO `sys_role_menu` VALUES (1, 3);
INSERT INTO `sys_role_menu` VALUES (1, 100);
INSERT INTO `sys_role_menu` VALUES (1, 101);
INSERT INTO `sys_role_menu` VALUES (1, 102);
INSERT INTO `sys_role_menu` VALUES (1, 103);
INSERT INTO `sys_role_menu` VALUES (1, 104);
INSERT INTO `sys_role_menu` VALUES (1, 105);
INSERT INTO `sys_role_menu` VALUES (1, 106);
INSERT INTO `sys_role_menu` VALUES (1, 107);
INSERT INTO `sys_role_menu` VALUES (1, 108);
INSERT INTO `sys_role_menu` VALUES (1, 109);
INSERT INTO `sys_role_menu` VALUES (1, 110);
INSERT INTO `sys_role_menu` VALUES (1, 111);
INSERT INTO `sys_role_menu` VALUES (1, 112);
INSERT INTO `sys_role_menu` VALUES (1, 113);
INSERT INTO `sys_role_menu` VALUES (1, 114);
INSERT INTO `sys_role_menu` VALUES (1, 115);
INSERT INTO `sys_role_menu` VALUES (1, 500);
INSERT INTO `sys_role_menu` VALUES (1, 501);
INSERT INTO `sys_role_menu` VALUES (1, 1000);
INSERT INTO `sys_role_menu` VALUES (1, 1001);
INSERT INTO `sys_role_menu` VALUES (1, 1002);
INSERT INTO `sys_role_menu` VALUES (1, 1003);
INSERT INTO `sys_role_menu` VALUES (1, 1004);
INSERT INTO `sys_role_menu` VALUES (1, 1005);
INSERT INTO `sys_role_menu` VALUES (1, 1006);
INSERT INTO `sys_role_menu` VALUES (1, 1007);
INSERT INTO `sys_role_menu` VALUES (1, 1008);
INSERT INTO `sys_role_menu` VALUES (1, 1009);
INSERT INTO `sys_role_menu` VALUES (1, 1010);
INSERT INTO `sys_role_menu` VALUES (1, 1011);
INSERT INTO `sys_role_menu` VALUES (1, 1012);
INSERT INTO `sys_role_menu` VALUES (1, 1013);
INSERT INTO `sys_role_menu` VALUES (1, 1014);
INSERT INTO `sys_role_menu` VALUES (1, 1015);
INSERT INTO `sys_role_menu` VALUES (1, 1016);
INSERT INTO `sys_role_menu` VALUES (1, 1017);
INSERT INTO `sys_role_menu` VALUES (1, 1018);
INSERT INTO `sys_role_menu` VALUES (1, 1019);
INSERT INTO `sys_role_menu` VALUES (1, 1020);
INSERT INTO `sys_role_menu` VALUES (1, 1021);
INSERT INTO `sys_role_menu` VALUES (1, 1022);
INSERT INTO `sys_role_menu` VALUES (1, 1023);
INSERT INTO `sys_role_menu` VALUES (1, 1024);
INSERT INTO `sys_role_menu` VALUES (1, 1031);
INSERT INTO `sys_role_menu` VALUES (1, 1032);
INSERT INTO `sys_role_menu` VALUES (1, 1033);
INSERT INTO `sys_role_menu` VALUES (1, 1034);
INSERT INTO `sys_role_menu` VALUES (1, 1035);
INSERT INTO `sys_role_menu` VALUES (1, 1036);
INSERT INTO `sys_role_menu` VALUES (1, 1140);
INSERT INTO `sys_role_menu` VALUES (1, 1141);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `nickname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者（用户ID）',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者（用户ID）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` int(1) NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 103, 'admin', '管理员', '00', 'admin@example.com', '15888888888', '0', '/api/uploads/2025/11/28/bcdf3a3472b14380a4984421fb0c7466.jpg', '$2a$10$rxPGrNZusiutJ9GMHyn55e/SXwFH2xE9t7Sn/2QhnJ1gHN8z3yZju', '0', '127.0.0.1', '2025-11-25 11:11:39', '2025-11-25 11:11:39', 1, '2025-11-28 13:00:51', 1, '管理员账号', 0);
INSERT INTO `sys_user` VALUES (2, 105, 'test', '测试用户', '00', 'test@example.com', '15666666666', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU3YqX0FJxy', '0', '127.0.0.1', '2025-11-25 11:11:39', '2025-11-25 11:11:39', 1, NULL, NULL, '测试账号', 0);
INSERT INTO `sys_user` VALUES (3, 102, 'cs-user', '测试用户', '00', '', '', '2', '', '$2a$10$WnpAvJsz9bU4e5B11mZXWuCW4cLQWZpyt85yrVEjr9bCZqjWXC5.2', '0', '', NULL, '2025-11-28 13:06:04', 1, '2025-11-28 13:06:04', 1, NULL, 0);

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);

SET FOREIGN_KEY_CHECKS = 1;


-- ====================================================================
-- Section: notification_system.sql
-- ====================================================================

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


-- ====================================================================
-- Section: message_system.sql
-- ====================================================================

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


-- ====================================================================
-- Section: workflow_system.sql
-- ====================================================================

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


-- ====================================================================
-- Section: add_notification_permissions.sql
-- ====================================================================

-- ====================================================================
-- 站内消息/通知中心模块权限配置
-- ====================================================================

-- 1. 添加通知中心菜单（一级菜单）
-- 字段顺序: menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, update_time, update_by, remark, deleted
INSERT INTO `sys_menu` VALUES (2100, '消息中心', 0, 6, 'notification', NULL, NULL, 1, 0, 'M', '0', '0', '', 'message', NOW(), 1, NULL, NULL, '', 0);

-- 2. 添加我的通知菜单（二级菜单）
INSERT INTO `sys_menu` VALUES (2101, '我的通知', 2100, 1, 'my-notification', 'system/notification/index', NULL, 1, 0, 'C', '0', '0', 'system:notification:list', 'bell', NOW(), 1, NULL, NULL, '', 0);

-- 3. 添加通知模板菜单（二级菜单，仅管理员可见）
INSERT INTO `sys_menu` VALUES (2102, '通知模板', 2100, 2, 'notification-template', 'system/notification/template', NULL, 1, 0, 'C', '0', '0', 'system:notification:template:list', 'form', NOW(), 1, NULL, NULL, '', 0);

-- 4. 添加通知模板管理按钮权限
INSERT INTO `sys_menu` VALUES (2103, '模板查询', 2102, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:notification:template:query', '#', NOW(), 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (2104, '模板新增', 2102, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:notification:template:add', '#', NOW(), 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (2105, '模板修改', 2102, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:notification:template:edit', '#', NOW(), 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (2106, '模板删除', 2102, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:notification:template:remove', '#', NOW(), 1, NULL, NULL, '', 0);

-- 5. 添加发送通知权限（仅管理员）
INSERT INTO `sys_menu` VALUES (2107, '发送通知', 2101, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:notification:send', '#', NOW(), 1, NULL, NULL, '', 0);

-- 6. 将菜单分配给超级管理员角色（角色ID=1）
INSERT INTO `sys_role_menu` VALUES (1, 2100);
INSERT INTO `sys_role_menu` VALUES (1, 2101);
INSERT INTO `sys_role_menu` VALUES (1, 2102);
INSERT INTO `sys_role_menu` VALUES (1, 2103);
INSERT INTO `sys_role_menu` VALUES (1, 2104);
INSERT INTO `sys_role_menu` VALUES (1, 2105);
INSERT INTO `sys_role_menu` VALUES (1, 2106);
INSERT INTO `sys_role_menu` VALUES (1, 2107);

COMMIT;


-- ====================================================================
-- Section: add_message_permissions.sql
-- ====================================================================

-- =====================================================
-- 消息管理模块菜单权限配置
-- 功能：为消息管理模块添加完整的菜单和操作权限
-- 执行：mysql -u root -proot admin_system < database/add_message_permissions.sql
-- =====================================================

USE admin_system;

-- 删除可能存在的旧权限（避免重复执行时报错）
DELETE FROM `sys_menu` WHERE `menu_id` >= 1200 AND `menu_id` < 1300;

-- =====================================================
-- 1. 创建一级菜单：消息管理（目录）
-- =====================================================
INSERT INTO `sys_menu` VALUES (
    1200, '消息管理', 0, 5, 'message', NULL, NULL, 1, 0, 'M', '0', '0', '',
    'email', NOW(), 1, NULL, NULL, '消息发送管理目录', 0
);

-- =====================================================
-- 2. 创建二级菜单：消息模板、消息配置、消息日志
-- =====================================================

-- 2.1 消息模板管理
INSERT INTO `sys_menu` VALUES (
    1201, '消息模板', 1200, 1, 'template', 'system/message/index', NULL, 1, 0, 'C', '0', '0',
    'system:message:list', 'documentation', NOW(), 1, NULL, NULL, '消息模板管理菜单', 0
);

-- 2.2 消息配置管理
INSERT INTO `sys_menu` VALUES (
    1202, '消息配置', 1200, 2, 'config', 'system/message/config', NULL, 1, 0, 'C', '0', '0',
    'system:message:config:list', 'setting', NOW(), 1, NULL, NULL, '消息配置管理菜单', 0
);

-- 2.3 消息发送日志
INSERT INTO `sys_menu` VALUES (
    1203, '发送日志', 1200, 3, 'log', 'system/message/log', NULL, 1, 0, 'C', '0', '0',
    'system:message:log:list', 'log', NOW(), 1, NULL, NULL, '消息发送日志菜单', 0
);

-- =====================================================
-- 3. 创建按钮权限：消息模板（1201）
-- =====================================================
INSERT INTO `sys_menu` VALUES (1210, '消息模板查询', 1201, 1, '', '', NULL, 1, 0, 'F', '0', '0', 'system:message:query', '#', NOW(), 1, NULL, NULL, '消息模板查询按钮', 0);
INSERT INTO `sys_menu` VALUES (1211, '消息模板新增', 1201, 2, '', '', NULL, 1, 0, 'F', '0', '0', 'system:message:add', '#', NOW(), 1, NULL, NULL, '消息模板新增按钮', 0);
INSERT INTO `sys_menu` VALUES (1212, '消息模板修改', 1201, 3, '', '', NULL, 1, 0, 'F', '0', '0', 'system:message:edit', '#', NOW(), 1, NULL, NULL, '消息模板修改按钮', 0);
INSERT INTO `sys_menu` VALUES (1213, '消息模板删除', 1201, 4, '', '', NULL, 1, 0, 'F', '0', '0', 'system:message:remove', '#', NOW(), 1, NULL, NULL, '消息模板删除按钮', 0);

-- =====================================================
-- 4. 创建按钮权限：消息配置（1202）
-- =====================================================
INSERT INTO `sys_menu` VALUES (1220, '消息配置查询', 1202, 1, '', '', NULL, 1, 0, 'F', '0', '0', 'system:message:config:query', '#', NOW(), 1, NULL, NULL, '消息配置查询按钮', 0);
INSERT INTO `sys_menu` VALUES (1221, '消息配置新增', 1202, 2, '', '', NULL, 1, 0, 'F', '0', '0', 'system:message:config:add', '#', NOW(), 1, NULL, NULL, '消息配置新增按钮', 0);
INSERT INTO `sys_menu` VALUES (1222, '消息配置修改', 1202, 3, '', '', NULL, 1, 0, 'F', '0', '0', 'system:message:config:edit', '#', NOW(), 1, NULL, NULL, '消息配置修改按钮', 0);
INSERT INTO `sys_menu` VALUES (1223, '消息配置删除', 1202, 4, '', '', NULL, 1, 0, 'F', '0', '0', 'system:message:config:remove', '#', NOW(), 1, NULL, NULL, '消息配置删除按钮', 0);
INSERT INTO `sys_menu` VALUES (1224, '消息配置测试', 1202, 5, '', '', NULL, 1, 0, 'F', '0', '0', 'system:message:config:test', '#', NOW(), 1, NULL, NULL, '消息配置测试发送按钮', 0);

-- =====================================================
-- 5. 创建按钮权限：消息发送日志（1203）
-- =====================================================
INSERT INTO `sys_menu` VALUES (1230, '发送日志查询', 1203, 1, '', '', NULL, 1, 0, 'F', '0', '0', 'system:message:log:query', '#', NOW(), 1, NULL, NULL, '发送日志查询按钮', 0);
INSERT INTO `sys_menu` VALUES (1231, '发送日志删除', 1203, 2, '', '', NULL, 1, 0, 'F', '0', '0', 'system:message:log:remove', '#', NOW(), 1, NULL, NULL, '发送日志删除按钮', 0);

-- =====================================================
-- 6. 为超级管理员角色（role_id=1）分配所有消息管理权限
-- =====================================================

-- 先删除可能存在的旧分配
DELETE FROM `sys_role_menu` WHERE `menu_id` >= 1200 AND `menu_id` < 1300 AND `role_id` = 1;

-- 分配一级菜单权限
INSERT INTO `sys_role_menu` VALUES (1, 1200);

-- 分配二级菜单权限
INSERT INTO `sys_role_menu` VALUES (1, 1201);
INSERT INTO `sys_role_menu` VALUES (1, 1202);
INSERT INTO `sys_role_menu` VALUES (1, 1203);

-- 分配消息模板按钮权限
INSERT INTO `sys_role_menu` VALUES (1, 1210);
INSERT INTO `sys_role_menu` VALUES (1, 1211);
INSERT INTO `sys_role_menu` VALUES (1, 1212);
INSERT INTO `sys_role_menu` VALUES (1, 1213);

-- 分配消息配置按钮权限
INSERT INTO `sys_role_menu` VALUES (1, 1220);
INSERT INTO `sys_role_menu` VALUES (1, 1221);
INSERT INTO `sys_role_menu` VALUES (1, 1222);
INSERT INTO `sys_role_menu` VALUES (1, 1223);
INSERT INTO `sys_role_menu` VALUES (1, 1224);

-- 分配消息发送日志按钮权限
INSERT INTO `sys_role_menu` VALUES (1, 1230);
INSERT INTO `sys_role_menu` VALUES (1, 1231);

-- =====================================================
-- 7. 验证权限配置
-- =====================================================
SELECT
    m.menu_id AS '菜单ID',
    m.menu_name AS '菜单名称',
    m.parent_id AS '父级ID',
    m.menu_type AS '类型',
    m.perms AS '权限标识',
    CASE WHEN rm.role_id IS NOT NULL THEN '✓ 已分配' ELSE '✗ 未分配' END AS '超管权限'
FROM sys_menu m
LEFT JOIN sys_role_menu rm ON m.menu_id = rm.menu_id AND rm.role_id = 1
WHERE m.menu_id >= 1200 AND m.menu_id < 1300
ORDER BY m.menu_id;

-- =====================================================
-- 8. 显示执行结果
-- =====================================================
SELECT '✅ 消息管理模块权限配置完成！' AS '执行状态';
SELECT '共创建 1 个一级菜单、3 个二级菜单、13 个按钮权限' AS '统计信息';
SELECT '请重新登录系统，权限将立即生效' AS '温馨提示';
SELECT '访问路径：消息管理 > 消息模板/消息配置/发送日志' AS '使用说明';


-- ====================================================================
-- Section: add_workflow_permissions.sql
-- ====================================================================

-- 工作流模块权限配置

-- 1. 添加工作流顶级菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('工作流', 0, 5, 'workflow', NULL, 1, 0, 'M', '0', '0', '', 'tree', NOW(), 1, '工作流管理目录');

-- 获取刚插入的工作流菜单ID
SET @workflow_menu_id = LAST_INSERT_ID();

-- 2. 添加流程定义管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程定义', @workflow_menu_id, 1, 'definition', 'workflow/definition/index', 1, 0, 'C', '0', '0', 'workflow:definition:list', 'form', NOW(), 1, '流程定义管理菜单');

SET @definition_menu_id = LAST_INSERT_ID();

-- 流程定义按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程定义查询', @definition_menu_id, 1, '', NULL, 1, 0, 'F', '0', '0', 'workflow:definition:query', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程定义新增', @definition_menu_id, 2, '', NULL, 1, 0, 'F', '0', '0', 'workflow:definition:add', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程定义修改', @definition_menu_id, 3, '', NULL, 1, 0, 'F', '0', '0', 'workflow:definition:edit', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程定义删除', @definition_menu_id, 4, '', NULL, 1, 0, 'F', '0', '0', 'workflow:definition:remove', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程定义发布', @definition_menu_id, 5, '', NULL, 1, 0, 'F', '0', '0', 'workflow:definition:publish', '#', NOW(), 1, '');

-- 3. 添加待办任务菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('待办任务', @workflow_menu_id, 2, 'pending', 'workflow/pending/index', 1, 0, 'C', '0', '0', 'workflow:task:list', 'guide', NOW(), 1, '待办任务菜单');

SET @pending_menu_id = LAST_INSERT_ID();

-- 待办任务按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('任务审批', @pending_menu_id, 1, '', NULL, 1, 0, 'F', '0', '0', 'workflow:task:approve', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('任务驳回', @pending_menu_id, 2, '', NULL, 1, 0, 'F', '0', '0', 'workflow:task:reject', '#', NOW(), 1, '');

-- 4. 添加已办任务菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('已办任务', @workflow_menu_id, 3, 'completed', 'workflow/completed/index', 1, 0, 'C', '0', '0', 'workflow:task:list', 'tab', NOW(), 1, '已办任务菜单');

-- 5. 添加我的流程菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('我的流程', @workflow_menu_id, 4, 'myProcess', 'workflow/myProcess/index', 1, 0, 'C', '0', '0', 'workflow:process:list', 'list', NOW(), 1, '我的流程菜单');

SET @myprocess_menu_id = LAST_INSERT_ID();

-- 我的流程按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程查询', @myprocess_menu_id, 1, '', NULL, 1, 0, 'F', '0', '0', 'workflow:process:query', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('发起流程', @myprocess_menu_id, 2, '', NULL, 1, 0, 'F', '0', '0', 'workflow:process:start', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('取消流程', @myprocess_menu_id, 3, '', NULL, 1, 0, 'F', '0', '0', 'workflow:process:cancel', '#', NOW(), 1, '');

-- 6. 添加流程详情菜单（隐藏菜单，不在侧边栏显示）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程详情', @workflow_menu_id, 5, 'detail', 'workflow/detail/index', 1, 0, 'C', '1', '0', 'workflow:process:query', 'eye', NOW(), 1, '流程详情页面');

-- 7. 给管理员角色分配所有工作流权限
-- 首先获取工作流模块下的所有菜单ID
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_id = @workflow_menu_id
UNION ALL
SELECT 1, menu_id FROM sys_menu WHERE parent_id = @workflow_menu_id
UNION ALL
SELECT 1, menu_id FROM sys_menu WHERE parent_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @workflow_menu_id);


-- ====================================================================
-- Section: add_cache_monitor_permissions.sql
-- ====================================================================

-- 添加缓存监控相关权限
-- 执行方式：在MySQL客户端执行此脚本，或使用命令：
-- mysql -u root -proot admin_system < add_cache_monitor_permissions.sql

USE admin_system;

-- 添加缓存监控菜单（menu_id=1142）
INSERT INTO `sys_menu` VALUES (1142, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'coin', NOW(), 1, NULL, NULL, '缓存监控菜单', 0);

-- 添加缓存监控的按钮权限
INSERT INTO `sys_menu` VALUES (1143, '缓存查询', 1142, 1, '', '', '', 1, 0, 'F', '0', '0', 'monitor:cache:query', '#', NOW(), 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1144, '缓存删除', 1142, 2, '', '', '', 1, 0, 'F', '0', '0', 'monitor:cache:remove', '#', NOW(), 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1145, '缓存清空', 1142, 3, '', '', '', 1, 0, 'F', '0', '0', 'monitor:cache:clear', '#', NOW(), 1, NULL, NULL, '', 0);

-- 给管理员角色（role_id=1）分配缓存监控权限
INSERT INTO `sys_role_menu` (role_id, menu_id) VALUES (1, 1142);
INSERT INTO `sys_role_menu` (role_id, menu_id) VALUES (1, 1143);
INSERT INTO `sys_role_menu` (role_id, menu_id) VALUES (1, 1144);
INSERT INTO `sys_role_menu` (role_id, menu_id) VALUES (1, 1145);

SELECT '缓存监控权限添加成功！' AS message;


-- ====================================================================
-- Section: add_job_permissions.sql
-- ====================================================================

-- =====================================================
-- 定时任务按钮权限配置
-- 功能：为定时任务模块添加完整的操作权限
-- 执行：mysql -u root -proot admin_system < database/add_job_permissions.sql
-- =====================================================

USE admin_system;

-- 删除可能存在的旧权限（避免重复执行时报错）
DELETE FROM `sys_menu` WHERE `menu_id` IN (1100, 1101, 1102, 1103, 1104, 1105);

-- 添加定时任务按钮权限
INSERT INTO `sys_menu` VALUES (1100, '定时任务查询', 110, 1, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', NOW(), 1, NULL, NULL, '定时任务查询按钮', 0);
INSERT INTO `sys_menu` VALUES (1101, '定时任务新增', 110, 2, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', NOW(), 1, NULL, NULL, '定时任务新增按钮', 0);
INSERT INTO `sys_menu` VALUES (1102, '定时任务修改', 110, 3, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', NOW(), 1, NULL, NULL, '定时任务修改按钮', 0);
INSERT INTO `sys_menu` VALUES (1103, '定时任务删除', 110, 4, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', NOW(), 1, NULL, NULL, '定时任务删除按钮', 0);
INSERT INTO `sys_menu` VALUES (1104, '定时任务状态修改', 110, 5, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', NOW(), 1, NULL, NULL, '定时任务状态修改按钮', 0);
INSERT INTO `sys_menu` VALUES (1105, '定时任务执行', 110, 6, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:run', '#', NOW(), 1, NULL, NULL, '定时任务立即执行按钮', 0);

-- 为超级管理员角色（role_id=1）分配所有定时任务权限
-- 先删除可能存在的旧分配
DELETE FROM `sys_role_menu` WHERE `menu_id` IN (110, 1100, 1101, 1102, 1103, 1104, 1105) AND `role_id` = 1;

-- 重新分配权限
INSERT INTO `sys_role_menu` VALUES (1, 110);   -- 定时任务菜单
INSERT INTO `sys_role_menu` VALUES (1, 1100);  -- 查询权限
INSERT INTO `sys_role_menu` VALUES (1, 1101);  -- 新增权限
INSERT INTO `sys_role_menu` VALUES (1, 1102);  -- 修改权限
INSERT INTO `sys_role_menu` VALUES (1, 1103);  -- 删除权限
INSERT INTO `sys_role_menu` VALUES (1, 1104);  -- 状态修改权限
INSERT INTO `sys_role_menu` VALUES (1, 1105);  -- 执行权限

-- 验证权限配置
SELECT
    m.menu_id,
    m.menu_name,
    m.parent_id,
    m.menu_type,
    m.perms,
    CASE WHEN rm.role_id IS NOT NULL THEN '已分配' ELSE '未分配' END AS '超管权限状态'
FROM sys_menu m
LEFT JOIN sys_role_menu rm ON m.menu_id = rm.menu_id AND rm.role_id = 1
WHERE m.menu_id = 110 OR m.parent_id = 110
ORDER BY m.menu_id;

-- 显示执行结果
SELECT '✅ 定时任务权限配置完成！' AS '执行状态';
SELECT '请重新登录系统，权限将立即生效' AS '提示';


-- ====================================================================
-- Section: add_statistics_permissions.sql
-- ====================================================================

-- ====================================================================
-- 数据报表统计模块权限配置
-- ====================================================================

-- 1. 添加统计分析菜单（一级菜单）
-- 字段顺序: menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, update_time, update_by, remark, deleted
INSERT INTO `sys_menu` VALUES (2200, '统计分析', 0, 7, 'statistics', NULL, NULL, 1, 0, 'M', '0', '0', '', 'chart', NOW(), 1, NULL, NULL, '', 0);

-- 2. 添加数据报表菜单（二级菜单）
INSERT INTO `sys_menu` VALUES (2201, '数据报表', 2200, 1, 'report', 'statistics/index', NULL, 1, 0, 'C', '0', '0', 'statistics:report:view', 'dashboard', NOW(), 1, NULL, NULL, '', 0);

-- 3. 添加统计分析按钮权限
INSERT INTO `sys_menu` VALUES (2202, '用户增长', 2201, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'statistics:user:growth', '#', NOW(), 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (2203, '登录统计', 2201, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'statistics:login:trend', '#', NOW(), 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (2204, '登录状态', 2201, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'statistics:login:status', '#', NOW(), 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (2205, '操作统计', 2201, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'statistics:operation:trend', '#', NOW(), 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (2206, '部门分布', 2201, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'statistics:dept:distribution', '#', NOW(), 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (2207, '角色分布', 2201, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'statistics:role:distribution', '#', NOW(), 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (2208, '操作类型', 2201, 7, '', NULL, NULL, 1, 0, 'F', '0', '0', 'statistics:operation:type', '#', NOW(), 1, NULL, NULL, '', 0);

-- 4. 将菜单分配给超级管理员角色（角色ID=1）
INSERT INTO `sys_role_menu` VALUES (1, 2200);
INSERT INTO `sys_role_menu` VALUES (1, 2201);
INSERT INTO `sys_role_menu` VALUES (1, 2202);
INSERT INTO `sys_role_menu` VALUES (1, 2203);
INSERT INTO `sys_role_menu` VALUES (1, 2204);
INSERT INTO `sys_role_menu` VALUES (1, 2205);
INSERT INTO `sys_role_menu` VALUES (1, 2206);
INSERT INTO `sys_role_menu` VALUES (1, 2207);
INSERT INTO `sys_role_menu` VALUES (1, 2208);

COMMIT;


-- ====================================================================
-- Section: add_schedule_jobs.sql
-- ====================================================================

-- 添加定时任务配置
-- 执行方式：在MySQL客户端执行此脚本

USE admin_system;

-- 1. 系统信息监控任务（每5分钟执行一次）
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_time`, `create_by`, `remark`)
VALUES
('系统信息监控', 'DEFAULT', 'systemTask.monitorSystemInfo', '0 0/5 * * * ?', '3', '1', '1', NOW(), 1, '监控系统JVM内存、Redis缓存等信息');

-- 2. 清理过期日志任务（每天凌晨3点执行）
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_time`, `create_by`, `remark`)
VALUES
('清理过期日志', 'DEFAULT', 'systemTask.cleanExpiredLogs', '0 0 3 * * ?', '3', '0', '1', NOW(), 1, '清理30天前的操作日志和登录日志');

-- 3. 缓存预热任务（每天早上8点执行）
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_time`, `create_by`, `remark`)
VALUES
('缓存预热', 'DEFAULT', 'systemTask.warmUpCache', '0 0 8 * * ?', '3', '0', '1', NOW(), 1, '预热系统配置、字典等常用缓存数据');

-- 4. 数据统计任务（每天晚上23点执行）
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_time`, `create_by`, `remark`)
VALUES
('数据统计', 'DEFAULT', 'systemTask.statisticsData', '0 0 23 * * ?', '3', '0', '1', NOW(), 1, '统计当日登录次数、操作次数等数据');

-- 5. 清理临时文件任务（每天凌晨2点执行）
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_time`, `create_by`, `remark`)
VALUES
('清理临时文件', 'DEFAULT', 'systemTask.cleanTempFiles', '0 0 2 * * ?', '3', '0', '1', NOW(), 1, '清理上传目录中的临时文件');

-- 6. 健康检查任务（每10分钟执行一次）
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_time`, `create_by`, `remark`)
VALUES
('健康检查', 'DEFAULT', 'systemTask.healthCheck', '0 0/10 * * * ?', '3', '1', '1', NOW(), 1, '检查数据库、Redis等服务的连接状态');

-- 查询创建的任务
SELECT job_id, job_name, job_group, invoke_target, cron_expression, status, remark
FROM sys_job
ORDER BY job_id DESC
LIMIT 6;

-- ============================================================
-- Section 12: SSO 单点登录
-- ============================================================

-- 12.1 SSO 身份认证源
DROP TABLE IF EXISTS `sys_sso_provider`;
CREATE TABLE `sys_sso_provider` (
  `id`                 bigint NOT NULL AUTO_INCREMENT,
  `code`               varchar(50)  NOT NULL COMMENT '标识（URL 用）',
  `name`               varchar(50)  NOT NULL COMMENT '展示名',
  `type`               varchar(20)  NOT NULL DEFAULT 'OAUTH2_GENERIC' COMMENT '策略类型',
  `icon`               varchar(100) DEFAULT NULL COMMENT '图标 class',
  `client_id`          varchar(255) NOT NULL,
  `client_secret`      varchar(500) NOT NULL COMMENT 'AES-GCM(IV||cipher) Base64',
  `authorization_uri`  varchar(500) NOT NULL,
  `token_uri`          varchar(500) NOT NULL,
  `userinfo_uri`       varchar(500) NOT NULL,
  `scope`              varchar(200) DEFAULT NULL,
  `user_field_mapping` varchar(1000) DEFAULT NULL COMMENT '字段映射 JSON',
  `default_role_id`    bigint DEFAULT NULL COMMENT '自动注册默认角色',
  `default_dept_id`    bigint DEFAULT NULL COMMENT '自动注册默认部门',
  `enabled`            tinyint(1) NOT NULL DEFAULT 1,
  `order_num`          int NOT NULL DEFAULT 0,
  `create_by`          bigint(20) DEFAULT NULL,
  `create_time`        datetime DEFAULT NULL,
  `update_by`          bigint(20) DEFAULT NULL,
  `update_time`        datetime DEFAULT NULL,
  `deleted`            tinyint(1) NOT NULL DEFAULT 0,
  `remark`             varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='SSO 身份认证源';

-- 12.2 用户外部身份绑定
DROP TABLE IF EXISTS `sys_user_sso_binding`;
CREATE TABLE `sys_user_sso_binding` (
  `id`                bigint NOT NULL AUTO_INCREMENT,
  `user_id`           bigint NOT NULL,
  `provider_id`       bigint NOT NULL,
  `external_user_id`  varchar(100) NOT NULL,
  `external_username` varchar(100) DEFAULT NULL,
  `email`             varchar(100) DEFAULT NULL,
  `bind_time`         datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_provider_external` (`provider_id`,`external_user_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户与外部 IdP 身份绑定';

-- 12.3 菜单：系统管理 → 身份认证源（menu_id=120 起）
INSERT INTO `sys_menu` (`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`remark`)
VALUES
 (120,'身份认证源',1,7,'sso','system/sso/index',1,0,'C','0','0','system:sso:list','connection','SSO 身份认证源管理'),
 (121,'认证源查询',120,1,'','',1,0,'F','0','0','system:sso:query','#',''),
 (122,'认证源新增',120,2,'','',1,0,'F','0','0','system:sso:add','#',''),
 (123,'认证源修改',120,3,'','',1,0,'F','0','0','system:sso:edit','#',''),
 (124,'认证源删除',120,4,'','',1,0,'F','0','0','system:sso:remove','#',''),
 (125,'认证源测试',120,5,'','',1,0,'F','0','0','system:sso:test','#','');

-- 12.4 把 120-125 这 6 个菜单绑给 admin 角色（role_id=1）
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES
 (1,120),(1,121),(1,122),(1,123),(1,124),(1,125);
