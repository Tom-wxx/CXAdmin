-- ====================================================================
-- 既有数据库升级：增加登录页宠物全局配置
-- ====================================================================
-- 适用范围：已经初始化过 admin_system、不能重新执行 database/init.sql 的环境。
-- 执行示例：mysql -u root -p admin_system < database/upgrade/20260713_add_login_pet_config.sql
-- 可重复执行：已存在未删除的 sys.login.pet.type 时不会新增重复记录。

START TRANSACTION;

INSERT INTO `sys_config` (
  `config_name`,
  `config_key`,
  `config_value`,
  `config_type`,
  `create_time`,
  `create_by`,
  `remark`,
  `deleted`
)
SELECT
  '登录页宠物',
  'sys.login.pet.type',
  'cat',
  'Y',
  NOW(),
  1,
  '允许值：cat、dog、owl；默认 cat',
  0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1
  FROM `sys_config`
  WHERE `config_key` = 'sys.login.pet.type'
    AND `deleted` = 0
);

COMMIT;

-- 验证：应始终返回一条未删除记录。
SELECT `config_id`, `config_key`, `config_value`, `config_type`
FROM `sys_config`
WHERE `config_key` = 'sys.login.pet.type'
  AND `deleted` = 0;

-- 回滚（仅在确认不再使用该功能时手工执行）：
-- DELETE FROM `sys_config` WHERE `config_key` = 'sys.login.pet.type';
