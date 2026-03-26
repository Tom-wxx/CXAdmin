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
