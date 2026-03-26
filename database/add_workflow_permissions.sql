-- 工作流模块权限配置

-- 1. 添加工作流顶级菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('工作流', 0, 5, 'workflow', NULL, 1, 0, 'M', '0', '0', '', 'tree', NOW(), 1, '工作流管理目录');

-- 获取刚插入的工作流菜单ID
SET @workflow_menu_id = LAST_INSERT_ID();

-- 2. 添加流程定义管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程定义', @workflow_menu_id, 1, 'definition', 'workflow/definition/index', 1, 0, 'C', '0', '0', 'workflow:definition:list', 'form', NOW(), 1, '流程定义管理菜单');

SET @definition_menu_id = LAST_INSERT_ID();

-- 流程定义按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程定义查询', @definition_menu_id, 1, '', NULL, 1, 0, 'F', '0', '0', 'workflow:definition:query', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程定义新增', @definition_menu_id, 2, '', NULL, 1, 0, 'F', '0', '0', 'workflow:definition:add', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程定义修改', @definition_menu_id, 3, '', NULL, 1, 0, 'F', '0', '0', 'workflow:definition:edit', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程定义删除', @definition_menu_id, 4, '', NULL, 1, 0, 'F', '0', '0', 'workflow:definition:remove', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程定义发布', @definition_menu_id, 5, '', NULL, 1, 0, 'F', '0', '0', 'workflow:definition:publish', '#', NOW(), 1, '');

-- 3. 添加待办任务菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('待办任务', @workflow_menu_id, 2, 'pending', 'workflow/pending/index', 1, 0, 'C', '0', '0', 'workflow:task:list', 'guide', NOW(), 1, '待办任务菜单');

SET @pending_menu_id = LAST_INSERT_ID();

-- 待办任务按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('任务审批', @pending_menu_id, 1, '', NULL, 1, 0, 'F', '0', '0', 'workflow:task:approve', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('任务驳回', @pending_menu_id, 2, '', NULL, 1, 0, 'F', '0', '0', 'workflow:task:reject', '#', NOW(), 1, '');

-- 4. 添加已办任务菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('已办任务', @workflow_menu_id, 3, 'completed', 'workflow/completed/index', 1, 0, 'C', '0', '0', 'workflow:task:list', 'tab', NOW(), 1, '已办任务菜单');

-- 5. 添加我的流程菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('我的流程', @workflow_menu_id, 4, 'myProcess', 'workflow/myProcess/index', 1, 0, 'C', '0', '0', 'workflow:process:list', 'list', NOW(), 1, '我的流程菜单');

SET @myprocess_menu_id = LAST_INSERT_ID();

-- 我的流程按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程查询', @myprocess_menu_id, 1, '', NULL, 1, 0, 'F', '0', '0', 'workflow:process:query', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('发起流程', @myprocess_menu_id, 2, '', NULL, 1, 0, 'F', '0', '0', 'workflow:process:start', '#', NOW(), 1, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('取消流程', @myprocess_menu_id, 3, '', NULL, 1, 0, 'F', '0', '0', 'workflow:process:cancel', '#', NOW(), 1, '');

-- 6. 添加流程详情菜单（隐藏菜单，不在侧边栏显示）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_time, create_by, remark)
VALUES ('流程详情', @workflow_menu_id, 5, 'detail', 'workflow/detail/index', 1, 0, 'C', '1', '0', 'workflow:process:query', 'eye', NOW(), 1, '流程详情页面');

-- 7. 给管理员角色分配所有工作流权限
-- 首先获取工作流模块下的所有菜单ID
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_id = @workflow_menu_id
UNION ALL
SELECT 1, menu_id FROM sys_menu WHERE parent_id = @workflow_menu_id
UNION ALL
SELECT 1, menu_id FROM sys_menu WHERE parent_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @workflow_menu_id);
