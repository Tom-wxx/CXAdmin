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
