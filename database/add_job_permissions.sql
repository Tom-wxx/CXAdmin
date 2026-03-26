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
