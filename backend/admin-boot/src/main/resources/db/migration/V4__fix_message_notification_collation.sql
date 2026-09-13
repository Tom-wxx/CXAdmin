-- Flyway V4: 统一 5 张表的排序规则为 utf8mb4_general_ci。
--
-- 背景：这 5 张表原先在 init.sql 里只写了 `DEFAULT CHARSET=utf8mb4`，没写 COLLATE，
-- 于是继承库级默认 —— 而 MySQL 8 的 utf8mb4 默认排序规则是 utf8mb4_0900_ai_ci，
-- 其余 23 张表却写死了 utf8mb4_general_ci。同库混用排序规则时，
-- 拿这些表的 varchar 去 JOIN 其它表会抛 `Illegal mix of collations (1267)`。
--
-- V1 已在建表语句里补上 COLLATE，所以新库本就正确，本脚本对新库是 no-op
-- （CONVERT TO 仍会 rebuild 表，但这几张表数据量很小，代价可忽略）。
-- 本脚本真正的作用对象是走 baseline=2 的老库。
--
-- 字符集前后都是 utf8mb4、只改排序规则，因此 TEXT 列不会被提升为 MEDIUMTEXT。
--
-- 前提：库里存在这 5 张表。若接入的是 notification/message 模块上线之前的更老的库，
-- 本脚本会失败并明确报出缺失的表名 —— 此时应先补建表再重跑。

ALTER TABLE `sys_notification`          CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `sys_notification_template` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `sys_message`               CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `sys_message_config`        CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `sys_message_log`           CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
