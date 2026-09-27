-- Flyway V5: 为高频查询补二级索引。
--
-- V1 里日志表（sys_oper_log / sys_login_log / sys_job_log）除主键外没有任何索引，
-- 而它们是增长最快的表：列表按时间倒序分页、仪表盘/统计按时间区间计数，都是全表扫描。
-- 配套改动：SysOperLogMapper / SysLoginLogMapper 的时间条件已从
-- DATE_FORMAT(col, ...) 改为对列的区间比较，否则函数包裹列会让下面的索引失效。
--
-- sys_user_role 主键是 (user_id, role_id)，按 role_id 反查（角色下的用户、菜单权限 JOIN）无法使用它。
-- sys_user.dept_id 用于数据权限过滤与部门用户统计。
--
-- 只加索引、不改数据；对走 baseline=2 的老库同样适用。

ALTER TABLE `sys_oper_log`  ADD INDEX `idx_oper_time` (`oper_time`);
ALTER TABLE `sys_login_log` ADD INDEX `idx_login_time` (`login_time`);
ALTER TABLE `sys_job_log`   ADD INDEX `idx_create_time` (`create_time`);
ALTER TABLE `sys_user_role` ADD INDEX `idx_role_id` (`role_id`);
ALTER TABLE `sys_user`      ADD INDEX `idx_dept_id` (`dept_id`);
