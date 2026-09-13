-- Flyway V2: 种子数据。由 database/init.sql 机械切分而来（INSERT / DELETE）。
-- 不含 sys.login.pet.type 那条配置 —— 它在 V3 里以幂等方式补齐，
-- 以便 baseline=2 的老库（可能尚未打过该补丁）也能拿到。

-- sys_config
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', '2025-11-25 11:11:39', 1, NULL, NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow', 0);
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', '2025-11-25 11:11:39', 1, NULL, NULL, '初始化密码 123456', 0);
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', '2025-11-25 11:11:39', 1, NULL, NULL, '深色主题theme-dark，浅色主题theme-light', 0);
-- config_id=4（sys.login.pet.type）见 V3，那里用幂等写法以兼容 baseline=2 的老库。
-- sys_dept
INSERT INTO `sys_dept` VALUES (100, 0, '0', '科技公司', 0, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '深圳总公司', 1, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '北京分公司', 2, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (103, 101, '0,100,101', '研发部门', 1, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (104, 101, '0,100,101', '市场部门', 2, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (105, 101, '0,100,101', '测试部门', 3, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (106, 101, '0,100,101', '财务部门', 4, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (107, 101, '0,100,101', '运维部门', 5, '管理员', '15888888888', 'admin@example.com', '0', '2025-11-25 11:11:39', 1, NULL, NULL, NULL, 0);
-- sys_dict_data
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '性别男', 0);
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '性别女', 0);
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '性别未知', 0);
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '显示菜单', 0);
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '隐藏菜单', 0);
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '正常状态', 0);
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '停用状态', 0);
INSERT INTO `sys_dict_data` VALUES (8, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '系统默认是', 0);
INSERT INTO `sys_dict_data` VALUES (9, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '系统默认否', 0);
INSERT INTO `sys_dict_data` VALUES (10, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '通知', 0);
INSERT INTO `sys_dict_data` VALUES (11, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '公告', 0);
INSERT INTO `sys_dict_data` VALUES (12, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '正常状态', 0);
INSERT INTO `sys_dict_data` VALUES (13, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '关闭状态', 0);
INSERT INTO `sys_dict_data` VALUES (14, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '新增操作', 0);
INSERT INTO `sys_dict_data` VALUES (15, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '修改操作', 0);
INSERT INTO `sys_dict_data` VALUES (16, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '删除操作', 0);
INSERT INTO `sys_dict_data` VALUES (17, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '授权操作', 0);
INSERT INTO `sys_dict_data` VALUES (18, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '导出操作', 0);
INSERT INTO `sys_dict_data` VALUES (19, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '导入操作', 0);
INSERT INTO `sys_dict_data` VALUES (20, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '强退操作', 0);
INSERT INTO `sys_dict_data` VALUES (21, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '生成操作', 0);
INSERT INTO `sys_dict_data` VALUES (22, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '清空操作', 0);
INSERT INTO `sys_dict_data` VALUES (23, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '正常状态', 0);
INSERT INTO `sys_dict_data` VALUES (24, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '停用状态', 0);
-- sys_dict_type
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '用户性别列表', 0);
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '菜单状态列表', 0);
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '系统开关列表', 0);
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '任务状态列表', 0);
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '任务分组列表', 0);
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '系统是否列表', 0);
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '通知类型列表', 0);
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '通知状态列表', 0);
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '操作类型列表', 0);
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', '2025-11-25 11:11:39', 1, NULL, NULL, '登录状态列表', 0);
-- sys_file
INSERT INTO `sys_file` VALUES (2, '5987656f784b4e5ea9b39b09844d4e87.jpg', 'v2-d6f44389971daab7e688e5b37046e4e4_720w.jpg', 'D:\\uploads\\2025\\11\\26\\5987656f784b4e5ea9b39b09844d4e87.jpg', '/uploads/2025/11/26/5987656f784b4e5ea9b39b09844d4e87.jpg', 43915, 'image/jpeg', 'jpg', 'image', 'local', 'a8e54d5eea7c910d03660ed14d537767', 0, '0', '2025-11-26 16:25:38', 1, '2025-11-28 12:14:38', 1, NULL, 1);
INSERT INTO `sys_file` VALUES (3, '3c02bc9c68ac4d51aeea2145941a7dd9.jpg', 'v2-d6f44389971daab7e688e5b37046e4e4_720w.jpg', 'C:\\uploads\\2025\\11\\28\\3c02bc9c68ac4d51aeea2145941a7dd9.jpg', '/api/uploads/2025/11/28/3c02bc9c68ac4d51aeea2145941a7dd9.jpg', 43915, 'image/jpeg', 'jpg', 'image', 'local', 'a8e54d5eea7c910d03660ed14d537767', 0, '0', '2025-11-28 12:14:56', 1, '2025-11-28 12:35:54', 1, NULL, 1);
INSERT INTO `sys_file` VALUES (4, 'cddf1ba9c3fb4601b9ed9441ddf7b4f9.jpg', '微信图片_20251123182925_17_2.jpg', 'C:\\uploads\\2025\\11\\28\\cddf1ba9c3fb4601b9ed9441ddf7b4f9.jpg', '/api/uploads/2025/11/28/cddf1ba9c3fb4601b9ed9441ddf7b4f9.jpg', 747589, 'image/jpeg', 'jpg', 'image', 'local', 'c5641cc63c832f52929f93b4e7a95778', 0, '0', '2025-11-28 12:15:45', 1, '2025-11-28 12:36:20', 1, NULL, 1);
INSERT INTO `sys_file` VALUES (5, 'bcdf3a3472b14380a4984421fb0c7466.jpg', 'v2-d6f44389971daab7e688e5b37046e4e4_720w.jpg', 'D:\\uploads\\2025\\11\\28\\bcdf3a3472b14380a4984421fb0c7466.jpg', '/api/uploads/2025/11/28/bcdf3a3472b14380a4984421fb0c7466.jpg', 43915, 'image/jpeg', 'jpg', 'image', 'local', 'a8e54d5eea7c910d03660ed14d537767', 0, '0', '2025-11-28 12:35:54', 1, '2025-11-28 13:00:51', 1, '头像展示 - 管理员', 0);
INSERT INTO `sys_file` VALUES (6, '6e0a52c268d14af19e72fb0828d85549.jpg', '微信图片_20251123182925_17_2.jpg', 'D:\\uploads\\2025\\11\\28\\6e0a52c268d14af19e72fb0828d85549.jpg', '/api/uploads/2025/11/28/6e0a52c268d14af19e72fb0828d85549.jpg', 747589, 'image/jpeg', 'jpg', 'image', 'local', 'c5641cc63c832f52929f93b4e7a95778', 0, '0', '2025-11-28 12:36:20', 1, '2025-11-28 12:36:47', 1, '头像展示 - 管理员', 1);
-- sys_file_category
INSERT INTO `sys_file_category` VALUES (1, '图片文件', 'image', 0, 1, 'el-icon-picture', 10485760, 'jpg,jpeg,png,gif,bmp,webp', '0', '2025-11-26 10:59:10', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_file_category` VALUES (2, '文档文件', 'document', 0, 2, 'el-icon-document', 52428800, 'doc,docx,xls,xlsx,ppt,pptx,pdf,txt', '0', '2025-11-26 10:59:10', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_file_category` VALUES (3, '视频文件', 'video', 0, 3, 'el-icon-video-camera', 104857600, 'mp4,avi,mov,wmv,flv,mkv', '0', '2025-11-26 10:59:10', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_file_category` VALUES (4, '音频文件', 'audio', 0, 4, 'el-icon-headset', 20971520, 'mp3,wav,flac,aac,wma', '0', '2025-11-26 10:59:10', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_file_category` VALUES (5, '压缩文件', 'archive', 0, 5, 'el-icon-folder-opened', 104857600, 'zip,rar,7z,tar,gz', '0', '2025-11-26 10:59:10', 1, NULL, NULL, NULL, 0);
INSERT INTO `sys_file_category` VALUES (6, '其他文件', 'other', 0, 6, 'el-icon-files', 52428800, '*', '0', '2025-11-26 10:59:10', 1, NULL, NULL, NULL, 0);
-- sys_menu
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'setting', '2025-11-25 11:11:39', 1, NULL, NULL, '系统管理目录', 0);
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', 1, 0, 'M', '0', '0', '', 'monitor', '2025-11-25 11:11:39', 1, NULL, NULL, '系统监控目录', 0);
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 3, 'tool', NULL, '', 1, 0, 'M', '0', '0', '', 'suitcase', '2025-11-25 11:11:39', 1, NULL, NULL, '系统工具目录', 0);
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', '2025-11-25 11:11:39', 1, NULL, NULL, '用户管理菜单', 0);
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'user-solid', '2025-11-25 11:11:39', 1, NULL, NULL, '角色管理菜单', 0);
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'menu', '2025-11-25 11:11:39', 1, NULL, NULL, '菜单管理菜单', 0);
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'office-building', '2025-11-25 11:11:39', 1, NULL, NULL, '部门管理菜单', 0);
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'postcard', '2025-11-25 11:11:39', 1, NULL, NULL, '岗位管理菜单', 0);
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'collection', '2025-11-25 11:11:39', 1, NULL, NULL, '字典管理菜单', 0);
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', '2025-11-25 11:11:39', 1, NULL, NULL, '参数设置菜单', 0);
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'bell', '2025-11-25 11:11:39', 1, NULL, NULL, '通知公告菜单', 0);
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', '', 1, 0, 'M', '0', '0', '', 'document', '2025-11-25 11:11:39', 1, NULL, NULL, '日志管理菜单', 0);
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'user-solid', '2025-11-25 11:11:39', 1, NULL, NULL, '在线用户菜单', 0);
INSERT INTO `sys_menu` VALUES (110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'time', '2025-11-25 11:11:39', 1, NULL, NULL, '定时任务菜单', 0);
INSERT INTO `sys_menu` VALUES (111, 'Druid监控', 2, 3, 'druid', 'monitor/druid/index', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'data-analysis', '2025-11-25 11:11:39', 1, NULL, NULL, '数据监控菜单', 0);
INSERT INTO `sys_menu` VALUES (112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'cpu', '2025-11-25 11:11:39', 1, NULL, NULL, '服务监控菜单', 0);
INSERT INTO `sys_menu` VALUES (113, '表单构建', 3, 1, 'build', 'tool/build/index', '', 1, 0, 'C', '1', '0', 'tool:build:list', 'edit-outline', '2025-11-25 11:11:39', 1, NULL, NULL, '表单构建菜单', 0);
INSERT INTO `sys_menu` VALUES (114, '代码生成', 3, 2, 'generator', 'tool/generator/index', '', 1, 0, 'C', '0', '0', 'tool:generator:list', 'tickets', '2025-11-25 11:11:39', 1, NULL, NULL, '代码生成菜单', 0);
INSERT INTO `sys_menu` VALUES (115, '系统接口', 2, 6, 'swagger', 'monitor/swagger/index', '', 1, 0, 'C', '0', '0', 'monitor:swagger:list', 'document', '2025-11-25 11:11:39', 1, NULL, NULL, '系统接口菜单（Swagger）', 0);
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'notebook-2', '2025-11-25 11:11:39', 1, NULL, NULL, '操作日志菜单', 0);
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'loginlog', 'monitor/loginlog/index', '', 1, 0, 'C', '0', '0', 'monitor:loginlog:list', 'key', '2025-11-25 11:11:39', 1, NULL, NULL, '登录日志菜单', 0);
INSERT INTO `sys_menu` VALUES (1000, '用户查询', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1001, '用户新增', 100, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1002, '用户修改', 100, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1003, '用户删除', 100, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1004, '用户导出', 100, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1005, '用户导入', 100, 6, '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1006, '重置密码', 100, 7, '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1007, '角色查询', 101, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1008, '角色新增', 101, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1009, '角色修改', 101, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1010, '角色删除', 101, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1011, '角色导出', 101, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1012, '菜单查询', 102, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1013, '菜单新增', 102, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1014, '菜单修改', 102, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1015, '菜单删除', 102, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1016, '部门查询', 103, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1017, '部门新增', 103, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1018, '部门修改', 103, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1019, '部门删除', 103, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1020, '岗位查询', 104, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1021, '岗位新增', 104, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1022, '岗位修改', 104, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1023, '岗位删除', 104, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1024, '岗位导出', 104, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1031, '文件管理', 0, 5, 'file', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'folder-opened', '2025-11-26 11:08:42', 1, NULL, NULL, '文件管理目录', 0);
INSERT INTO `sys_menu` VALUES (1032, '文件列表', 1031, 1, 'list', 'system/file/index', NULL, 1, 0, 'C', '0', '0', 'system:file:list', 'files', '2025-11-26 11:08:42', 1, NULL, NULL, '文件列表菜单', 0);
INSERT INTO `sys_menu` VALUES (1033, '文件上传', 1032, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:file:upload', '#', '2025-11-26 11:08:42', 1, NULL, NULL, '文件上传按钮', 0);
INSERT INTO `sys_menu` VALUES (1034, '文件下载', 1032, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:file:download', '#', '2025-11-26 11:08:42', 1, NULL, NULL, '文件下载按钮', 0);
INSERT INTO `sys_menu` VALUES (1035, '文件删除', 1032, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:file:remove', '#', '2025-11-26 11:08:42', 1, NULL, NULL, '文件删除按钮', 0);
INSERT INTO `sys_menu` VALUES (1036, '文件详情', 1032, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:file:query', '#', '2025-11-26 11:08:42', 1, NULL, NULL, '文件详情按钮', 0);
INSERT INTO `sys_menu` VALUES (1037, '个人中心', 0, 100, 'profile', 'user/profile/index', NULL, 1, 0, 'C', '1', '0', NULL, 'user', '2025-11-26 11:08:42', 1, '2025-11-26 14:39:09', NULL, '个人中心仅通过顶部用户菜单访问，已从侧边栏移除', 1);
INSERT INTO `sys_menu` VALUES (1038, '服务器监控', 2, 1, 'server', 'monitor/server/index', NULL, 1, 0, 'C', '0', '0', 'monitor:server:view', 'monitor', '2025-11-26 11:08:42', 1, NULL, NULL, '服务器监控', 1);
INSERT INTO `sys_menu` VALUES (1140, '代码生成查询', 114, 1, '', '', '', 1, 0, 'F', '0', '0', 'tool:generator:query', '#', '2025-11-27 11:19:47', 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1141, '代码生成执行', 114, 2, '', '', '', 1, 0, 'F', '0', '0', 'tool:generator:code', '#', '2025-11-27 11:19:47', 1, NULL, NULL, '', 0);
-- sys_post
INSERT INTO `sys_post` VALUES (1, 'ceo', '董事长', 1, '0', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_post` VALUES (2, 'se', '项目经理', 2, '0', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_post` VALUES (3, 'hr', '人力资源', 3, '0', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
INSERT INTO `sys_post` VALUES (4, 'user', '普通员工', 4, '0', '2025-11-25 11:11:39', 1, NULL, NULL, '', 0);
-- sys_role
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '2025-11-25 11:11:39', 1, NULL, NULL, '超级管理员', 0);
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '2025-11-25 11:11:39', 1, NULL, NULL, '普通角色', 0);
-- sys_role_menu
INSERT INTO `sys_role_menu` VALUES (1, 1);
INSERT INTO `sys_role_menu` VALUES (1, 2);
INSERT INTO `sys_role_menu` VALUES (1, 3);
INSERT INTO `sys_role_menu` VALUES (1, 100);
INSERT INTO `sys_role_menu` VALUES (1, 101);
INSERT INTO `sys_role_menu` VALUES (1, 102);
INSERT INTO `sys_role_menu` VALUES (1, 103);
INSERT INTO `sys_role_menu` VALUES (1, 104);
INSERT INTO `sys_role_menu` VALUES (1, 105);
INSERT INTO `sys_role_menu` VALUES (1, 106);
INSERT INTO `sys_role_menu` VALUES (1, 107);
INSERT INTO `sys_role_menu` VALUES (1, 108);
INSERT INTO `sys_role_menu` VALUES (1, 109);
INSERT INTO `sys_role_menu` VALUES (1, 110);
INSERT INTO `sys_role_menu` VALUES (1, 111);
INSERT INTO `sys_role_menu` VALUES (1, 112);
INSERT INTO `sys_role_menu` VALUES (1, 113);
INSERT INTO `sys_role_menu` VALUES (1, 114);
INSERT INTO `sys_role_menu` VALUES (1, 115);
INSERT INTO `sys_role_menu` VALUES (1, 500);
INSERT INTO `sys_role_menu` VALUES (1, 501);
INSERT INTO `sys_role_menu` VALUES (1, 1000);
INSERT INTO `sys_role_menu` VALUES (1, 1001);
INSERT INTO `sys_role_menu` VALUES (1, 1002);
INSERT INTO `sys_role_menu` VALUES (1, 1003);
INSERT INTO `sys_role_menu` VALUES (1, 1004);
INSERT INTO `sys_role_menu` VALUES (1, 1005);
INSERT INTO `sys_role_menu` VALUES (1, 1006);
INSERT INTO `sys_role_menu` VALUES (1, 1007);
INSERT INTO `sys_role_menu` VALUES (1, 1008);
INSERT INTO `sys_role_menu` VALUES (1, 1009);
INSERT INTO `sys_role_menu` VALUES (1, 1010);
INSERT INTO `sys_role_menu` VALUES (1, 1011);
INSERT INTO `sys_role_menu` VALUES (1, 1012);
INSERT INTO `sys_role_menu` VALUES (1, 1013);
INSERT INTO `sys_role_menu` VALUES (1, 1014);
INSERT INTO `sys_role_menu` VALUES (1, 1015);
INSERT INTO `sys_role_menu` VALUES (1, 1016);
INSERT INTO `sys_role_menu` VALUES (1, 1017);
INSERT INTO `sys_role_menu` VALUES (1, 1018);
INSERT INTO `sys_role_menu` VALUES (1, 1019);
INSERT INTO `sys_role_menu` VALUES (1, 1020);
INSERT INTO `sys_role_menu` VALUES (1, 1021);
INSERT INTO `sys_role_menu` VALUES (1, 1022);
INSERT INTO `sys_role_menu` VALUES (1, 1023);
INSERT INTO `sys_role_menu` VALUES (1, 1024);
INSERT INTO `sys_role_menu` VALUES (1, 1031);
INSERT INTO `sys_role_menu` VALUES (1, 1032);
INSERT INTO `sys_role_menu` VALUES (1, 1033);
INSERT INTO `sys_role_menu` VALUES (1, 1034);
INSERT INTO `sys_role_menu` VALUES (1, 1035);
INSERT INTO `sys_role_menu` VALUES (1, 1036);
INSERT INTO `sys_role_menu` VALUES (1, 1140);
INSERT INTO `sys_role_menu` VALUES (1, 1141);
-- sys_user
INSERT INTO `sys_user` VALUES (1, 103, 'admin', '管理员', '00', 'admin@example.com', '15888888888', '0', '/api/uploads/2025/11/28/bcdf3a3472b14380a4984421fb0c7466.jpg', '$2a$10$rxPGrNZusiutJ9GMHyn55e/SXwFH2xE9t7Sn/2QhnJ1gHN8z3yZju', '0', '127.0.0.1', '2025-11-25 11:11:39', '2025-11-25 11:11:39', 1, '2025-11-28 13:00:51', 1, '管理员账号', 0);
INSERT INTO `sys_user` VALUES (2, 105, 'test', '测试用户', '00', 'test@example.com', '15666666666', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU3YqX0FJxy', '0', '127.0.0.1', '2025-11-25 11:11:39', '2025-11-25 11:11:39', 1, NULL, NULL, '测试账号', 0);
INSERT INTO `sys_user` VALUES (3, 102, 'cs-user', '测试用户', '00', '', '', '2', '', '$2a$10$WnpAvJsz9bU4e5B11mZXWuCW4cLQWZpyt85yrVEjr9bCZqjWXC5.2', '0', '', NULL, '2025-11-28 13:06:04', 1, '2025-11-28 13:06:04', 1, NULL, 0);
-- sys_user_role
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
-- sys_notification_template
-- 3. 初始化通知模板数据
-- 字段顺序: id, template_code, template_name, title, content, type, priority, status, create_time, create_by, update_time, update_by, remark, deleted
INSERT INTO `sys_notification_template` VALUES
(1, 'USER_REGISTER', '用户注册通知', '欢迎加入系统', '尊敬的{userName}，欢迎您注册成为我们的用户！', 'system', 'normal', '1', NOW(), 1, NOW(), NULL, NULL, 0),
(2, 'PASSWORD_RESET', '密码重置通知', '密码已重置', '{userName}，您的密码已被管理员重置，请及时修改密码。', 'system', 'important', '1', NOW(), 1, NOW(), NULL, NULL, 0),
(3, 'ROLE_CHANGE', '角色变更通知', '您的角色已变更', '{userName}，您的角色已被修改为：{roleName}', 'system', 'normal', '1', NOW(), 1, NOW(), NULL, NULL, 0),
(4, 'APPROVAL_PENDING', '待审批通知', '您有新的审批待处理', '{userName}，您有一条来自{senderName}的审批待处理，请及时处理。', 'approval', 'important', '1', NOW(), 1, NOW(), NULL, NULL, 0),
(5, 'APPROVAL_APPROVED', '审批通过通知', '您的审批已通过', '{userName}，您提交的审批已通过。', 'approval', 'normal', '1', NOW(), 1, NOW(), NULL, NULL, 0),
(6, 'APPROVAL_REJECTED', '审批驳回通知', '您的审批已驳回', '{userName}，您提交的审批已被驳回，驳回原因：{reason}', 'approval', 'important', '1', NOW(), 1, NOW(), NULL, NULL, 0),
(7, 'SYSTEM_ANNOUNCE', '系统公告', '系统公告', '{content}', 'announce', 'urgent', '1', NOW(), 1, NOW(), NULL, NULL, 0);
-- sys_notification
-- 4. 插入测试数据
-- 字段顺序: id, title, content, type, priority, status, user_id, sender_id, sender_name, link_url, link_type, read_time, create_time, create_by, update_time, update_by, remark, deleted
INSERT INTO `sys_notification` VALUES
(1, '欢迎使用系统', '欢迎您使用本系统，如有任何问题请联系管理员。', 'system', 'normal', 'unread', 1, NULL, '系统', NULL, NULL, NULL, NOW(), 1, NOW(), NULL, NULL, 0),
(2, '密码安全提示', '为了您的账号安全，建议您定期修改密码。', 'system', 'important', 'unread', 1, NULL, '系统', '/user/profile', 'profile', NULL, NOW(), 1, NOW(), NULL, NULL, 0);
-- sys_message
-- 初始化消息模板数据
INSERT INTO `sys_message` VALUES
(1, '用户注册欢迎邮件', 'USER_REGISTER_EMAIL', '1', '欢迎注册管理系统',
 '尊敬的 ${username}：\n\n您好！欢迎注册我们的管理系统。\n\n您的账号信息如下：\n用户名：${username}\n注册时间：${registerTime}\n\n如有任何问题，请随时联系我们。\n\n祝您使用愉快！',
 '{"username":"用户名","registerTime":"注册时间"}', '0', NOW(), 1, NULL, NULL, '用户注册成功后发送的欢迎邮件', 0),

(2, '密码重置邮件', 'PASSWORD_RESET_EMAIL', '1', '密码重置通知',
 '尊敬的 ${username}：\n\n您好！您的密码已重置。\n\n新密码：${newPassword}\n\n为了账号安全，请尽快登录系统修改密码。\n\n如非本人操作，请立即联系管理员。',
 '{"username":"用户名","newPassword":"新密码"}', '0', NOW(), 1, NULL, NULL, '密码重置后发送的通知邮件', 0),

(3, '登录验证码短信', 'LOGIN_CODE_SMS', '2', NULL,
 '【管理系统】您的登录验证码是：${code}，有效期${expireMinutes}分钟，请勿泄露给他人。',
 '{"code":"验证码","expireMinutes":"有效期分钟数"}', '0', NOW(), 1, NULL, NULL, '登录时发送的验证码短信', 0),

(4, '系统公告通知', 'SYSTEM_NOTICE', '3', '${noticeTitle}',
 '${noticeContent}',
 '{"noticeTitle":"公告标题","noticeContent":"公告内容"}', '0', NOW(), 1, NULL, NULL, '系统公告站内信通知', 0);
-- sys_message_config
-- 初始化消息配置数据（示例配置）
INSERT INTO `sys_message_config` VALUES
(1, '1', '默认邮件配置',
 '{"host":"smtp.qq.com","port":"587","username":"your-email@qq.com","password":"your-auth-code","from":"your-email@qq.com","fromName":"管理系统","ssl":true}',
 '1', '0', NOW(), 1, NULL, NULL, 'QQ邮箱SMTP配置示例', 0),

(2, '2', '默认短信配置',
 '{"provider":"aliyun","accessKeyId":"your-access-key","accessKeySecret":"your-access-secret","signName":"管理系统","templateCode":"SMS_123456"}',
 '1', '0', NOW(), 1, NULL, NULL, '阿里云短信配置示例', 0);
-- sys_menu
-- 添加缓存监控菜单（menu_id=1142）
INSERT INTO `sys_menu` VALUES (1142, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'coin', NOW(), 1, NULL, NULL, '缓存监控菜单', 0);
-- 添加缓存监控的按钮权限
INSERT INTO `sys_menu` VALUES (1143, '缓存查询', 1142, 1, '', '', '', 1, 0, 'F', '0', '0', 'monitor:cache:query', '#', NOW(), 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1144, '缓存删除', 1142, 2, '', '', '', 1, 0, 'F', '0', '0', 'monitor:cache:remove', '#', NOW(), 1, NULL, NULL, '', 0);
INSERT INTO `sys_menu` VALUES (1145, '缓存清空', 1142, 3, '', '', '', 1, 0, 'F', '0', '0', 'monitor:cache:clear', '#', NOW(), 1, NULL, NULL, '', 0);
-- sys_role_menu
-- 给管理员角色（role_id=1）分配缓存监控权限
INSERT INTO `sys_role_menu` (role_id, menu_id) VALUES (1, 1142);
INSERT INTO `sys_role_menu` (role_id, menu_id) VALUES (1, 1143);
INSERT INTO `sys_role_menu` (role_id, menu_id) VALUES (1, 1144);
INSERT INTO `sys_role_menu` (role_id, menu_id) VALUES (1, 1145);
-- sys_menu
-- 删除可能存在的旧权限（避免重复执行时报错）
DELETE FROM `sys_menu` WHERE `menu_id` IN (1100, 1101, 1102, 1103, 1104, 1105);
-- 添加定时任务按钮权限
INSERT INTO `sys_menu` VALUES (1100, '定时任务查询', 110, 1, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', NOW(), 1, NULL, NULL, '定时任务查询按钮', 0);
INSERT INTO `sys_menu` VALUES (1101, '定时任务新增', 110, 2, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', NOW(), 1, NULL, NULL, '定时任务新增按钮', 0);
INSERT INTO `sys_menu` VALUES (1102, '定时任务修改', 110, 3, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', NOW(), 1, NULL, NULL, '定时任务修改按钮', 0);
INSERT INTO `sys_menu` VALUES (1103, '定时任务删除', 110, 4, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', NOW(), 1, NULL, NULL, '定时任务删除按钮', 0);
INSERT INTO `sys_menu` VALUES (1104, '定时任务状态修改', 110, 5, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', NOW(), 1, NULL, NULL, '定时任务状态修改按钮', 0);
INSERT INTO `sys_menu` VALUES (1105, '定时任务执行', 110, 6, '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:run', '#', NOW(), 1, NULL, NULL, '定时任务立即执行按钮', 0);
-- sys_role_menu
-- 为超级管理员角色（role_id=1）分配所有定时任务权限
-- 先删除可能存在的旧分配
DELETE FROM `sys_role_menu` WHERE `menu_id` IN (110, 1100, 1101, 1102, 1103, 1104, 1105) AND `role_id` = 1;
-- 重新分配权限
INSERT INTO `sys_role_menu` VALUES (1, 110);
-- 定时任务菜单
INSERT INTO `sys_role_menu` VALUES (1, 1100);
-- 查询权限
INSERT INTO `sys_role_menu` VALUES (1, 1101);
-- 新增权限
INSERT INTO `sys_role_menu` VALUES (1, 1102);
-- 修改权限
INSERT INTO `sys_role_menu` VALUES (1, 1103);
-- 删除权限
INSERT INTO `sys_role_menu` VALUES (1, 1104);
-- 状态修改权限
INSERT INTO `sys_role_menu` VALUES (1, 1105);
-- sys_menu
-- ====================================================================
-- Section: add_statistics_permissions.sql
-- ====================================================================

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
-- sys_role_menu
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
-- sys_job
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
-- sys_menu
-- 12.3 菜单：系统管理 → 身份认证源（menu_id=120 起）
INSERT INTO `sys_menu` (`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`remark`)
VALUES
 (120,'身份认证源',1,7,'sso','system/sso/index',1,0,'C','0','0','system:sso:list','connection','SSO 身份认证源管理'),
 (121,'认证源查询',120,1,'','',1,0,'F','0','0','system:sso:query','#',''),
 (122,'认证源新增',120,2,'','',1,0,'F','0','0','system:sso:add','#',''),
 (123,'认证源修改',120,3,'','',1,0,'F','0','0','system:sso:edit','#',''),
 (124,'认证源删除',120,4,'','',1,0,'F','0','0','system:sso:remove','#',''),
 (125,'认证源测试',120,5,'','',1,0,'F','0','0','system:sso:test','#','');
-- 12.5 SSO 审计日志菜单
INSERT INTO `sys_menu` (`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`remark`)
VALUES
 (126,'SSO 日志',120,6,'sso/log','system/sso/log/index',1,0,'C','0','0','system:sso:log:list','log','SSO 登录审计日志');
-- sys_role_menu
-- 12.6 把 120-126 这 7 个菜单绑给 admin 角色（role_id=1）
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES
 (1,120),(1,121),(1,122),(1,123),(1,124),(1,125),(1,126);
