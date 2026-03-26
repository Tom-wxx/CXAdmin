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
