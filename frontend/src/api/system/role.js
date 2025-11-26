import request from '@/utils/request'

/**
 * 分页查询角色列表
 * @param {Object} query - 查询参数
 * @param {string} query.roleName - 角色名称
 * @param {string} query.roleKey - 权限字符
 * @param {string} query.status - 状态
 * @param {number} query.current - 页码
 * @param {number} query.size - 每页数量
 */
export function listRole(query) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询所有角色列表
 */
export function listAllRole() {
  return request({
    url: '/system/role/listAll',
    method: 'get'
  })
}

/**
 * 查询角色详细信息
 * @param {number} roleId - 角色ID
 */
export function getRole(roleId) {
  return request({
    url: '/system/role/' + roleId,
    method: 'get'
  })
}

/**
 * 查询角色已分配的菜单ID列表
 * @param {number} roleId - 角色ID
 */
export function getMenuIds(roleId) {
  return request({
    url: '/system/role/menuIds/' + roleId,
    method: 'get'
  })
}

/**
 * 新增角色
 * @param {Object} data - 角色信息
 */
export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data: data
  })
}

/**
 * 修改角色
 * @param {Object} data - 角色信息
 */
export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data: data
  })
}

/**
 * 删除角色
 * @param {number|Array} roleIds - 角色ID或角色ID数组
 */
export function delRole(roleIds) {
  return request({
    url: '/system/role/' + roleIds,
    method: 'delete'
  })
}

/**
 * 修改角色状态
 * @param {number} roleId - 角色ID
 * @param {string} status - 状态（0正常 1停用）
 */
export function changeRoleStatus(roleId, status) {
  return request({
    url: '/system/role/changeStatus',
    method: 'put',
    params: {
      roleId: roleId,
      status: status
    }
  })
}

/**
 * 校验角色名称是否唯一
 * @param {string} roleName - 角色名称
 * @param {number} roleId - 角色ID（编辑时传入）
 */
export function checkRoleNameUnique(roleName, roleId) {
  return request({
    url: '/system/role/checkRoleNameUnique',
    method: 'get',
    params: {
      roleName: roleName,
      roleId: roleId
    }
  })
}

/**
 * 校验角色权限是否唯一
 * @param {string} roleKey - 权限字符
 * @param {number} roleId - 角色ID（编辑时传入）
 */
export function checkRoleKeyUnique(roleKey, roleId) {
  return request({
    url: '/system/role/checkRoleKeyUnique',
    method: 'get',
    params: {
      roleKey: roleKey,
      roleId: roleId
    }
  })
}

/**
 * 保存角色菜单权限
 * @param {number} roleId - 角色ID
 * @param {Array} menuIds - 菜单ID数组
 */
export function saveRoleMenus(roleId, menuIds) {
  return request({
    url: '/system/role/saveRoleMenus',
    method: 'put',
    params: {
      roleId: roleId,
      menuIds: menuIds
    }
  })
}
