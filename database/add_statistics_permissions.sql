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
