import request from '@/utils/request'
import type { Result, TreeOption } from '@/types/api'
import type { Menu, MenuQuery } from '@/types/system/menu'

/** 查询菜单列表（树形结构） */
export function listMenu(query?: MenuQuery): Promise<Result<Menu[]>> {
  return request({ url: '/system/menu/list', method: 'get', params: query })
}

/** 查询菜单详细信息 */
export function getMenu(menuId: number): Promise<Result<Menu>> {
  return request({ url: '/system/menu/' + menuId, method: 'get' })
}

/** 新增菜单 */
export function addMenu(data: Menu): Promise<Result<void>> {
  return request({ url: '/system/menu', method: 'post', data })
}

/** 修改菜单 */
export function updateMenu(data: Menu): Promise<Result<void>> {
  return request({ url: '/system/menu', method: 'put', data })
}

/** 删除菜单（force 强制删除） */
export function delMenu(menuId: number, force?: boolean): Promise<Result<void>> {
  return request({ url: '/system/menu/' + menuId, method: 'delete', params: { force } })
}

/** 校验菜单名称是否唯一 */
export function checkMenuNameUnique(menuName: string, menuId?: number, parentId?: number): Promise<Result<boolean>> {
  return request({ url: '/system/menu/checkMenuNameUnique', method: 'get', params: { menuName, menuId, parentId } })
}

/** 获取菜单下拉树列表 */
export function treeselect(): Promise<Result<TreeOption[]>> {
  return request({ url: '/system/menu/treeselect', method: 'get' })
}
