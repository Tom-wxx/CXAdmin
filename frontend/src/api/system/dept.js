import request from '@/utils/request'

/**
 * 查询部门列表
 * @param {Object} query - 查询参数
 * @param {string} query.deptName - 部门名称
 * @param {string} query.status - 状态
 */
export function listDept(query) {
  return request({
    url: '/system/dept/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询部门列表（树形结构）
 * @param {Object} query - 查询参数
 */
export function listDeptTree(query) {
  return request({
    url: '/system/dept/treeList',
    method: 'get',
    params: query
  })
}

/**
 * 查询部门详细信息
 * @param {number} deptId - 部门ID
 */
export function getDept(deptId) {
  return request({
    url: '/system/dept/' + deptId,
    method: 'get'
  })
}

/**
 * 新增部门
 * @param {Object} data - 部门信息
 */
export function addDept(data) {
  return request({
    url: '/system/dept',
    method: 'post',
    data: data
  })
}

/**
 * 修改部门
 * @param {Object} data - 部门信息
 */
export function updateDept(data) {
  return request({
    url: '/system/dept',
    method: 'put',
    data: data
  })
}

/**
 * 删除部门
 * @param {number} deptId - 部门ID
 */
export function delDept(deptId) {
  return request({
    url: '/system/dept/' + deptId,
    method: 'delete'
  })
}

/**
 * 查询部门下拉树列表
 * @param {Object} query - 查询参数
 */
export function treeSelect(query) {
  return request({
    url: '/system/dept/treeSelect',
    method: 'get',
    params: query
  })
}
