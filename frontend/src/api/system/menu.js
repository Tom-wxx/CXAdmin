import request from '@/utils/request'

/**
 * 查询菜单列表（树形结构）
 * @param {Object} query - 查询参数
 * @param {string} query.menuName - 菜单名称
 * @param {string} query.status - 状态
 */
export function listMenu(query) {
  return request({
    url: '/system/menu/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询菜单详细信息
 * @param {number} menuId - 菜单ID
 */
export function getMenu(menuId) {
  return request({
    url: '/system/menu/' + menuId,
    method: 'get'
  })
}

/**
 * 新增菜单
 * @param {Object} data - 菜单信息
 */
export function addMenu(data) {
  return request({
    url: '/system/menu',
    method: 'post',
    data: data
  })
}

/**
 * 修改菜单
 * @param {Object} data - 菜单信息
 */
export function updateMenu(data) {
  return request({
    url: '/system/menu',
    method: 'put',
    data: data
  })
}

/**
 * 删除菜单
 * @param {number} menuId - 菜单ID
 * @param {boolean} force - 是否强制删除
 */
export function delMenu(menuId, force) {
  return request({
    url: '/system/menu/' + menuId,
    method: 'delete',
    params: {
      force: force
    }
  })
}

/**
 * 校验菜单名称是否唯一
 * @param {string} menuName - 菜单名称
 * @param {number} menuId - 菜单ID（编辑时传入）
 * @param {number} parentId - 父菜单ID
 */
export function checkMenuNameUnique(menuName, menuId, parentId) {
  return request({
    url: '/system/menu/checkMenuNameUnique',
    method: 'get',
    params: {
      menuName: menuName,
      menuId: menuId,
      parentId: parentId
    }
  })
}

/**
 * 获取菜单下拉树列表
 */
export function treeselect() {
  return request({
    url: '/system/menu/treeselect',
    method: 'get'
  })
}
