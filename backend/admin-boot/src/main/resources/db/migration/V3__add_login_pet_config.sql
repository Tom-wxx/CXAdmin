-- Flyway V3: 登录页宠物全局配置（sys.login.pet.type）。
--
-- 为什么单独一个版本而不是并进 V2：
--   baseline-version=2 会让"已用旧 init.sql 建好的库"跳过 V1/V2，而那些库可能
--   从未打过这条配置（它原先在 database/upgrade/20260713_add_login_pet_config.sql 里）。
--   放在 V3 并写成幂等，可让两条路径都拿到正确结果：
--     新库  V1→V2→V3：V2 未插入此条，V3 插入
--     老库  baseline=2→V3：已有则跳过，缺失则补齐
--
-- 无需 START TRANSACTION / COMMIT —— Flyway 会把纯 DML 的 migration 包在事务里。

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
