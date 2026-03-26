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
