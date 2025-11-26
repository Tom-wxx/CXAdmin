import request from '@/utils/request'

/**
 * 查询岗位列表
 * @param {Object} query - 查询参数
 * @param {string} query.postCode - 岗位编码
 * @param {string} query.postName - 岗位名称
 * @param {string} query.status - 状态
 */
export function listPost(query) {
  return request({
    url: '/system/post/list',
    method: 'get',
    params: query
  })
}

/**
 * 分页查询岗位列表
 * @param {Object} query - 查询参数
 * @param {string} query.postCode - 岗位编码
 * @param {string} query.postName - 岗位名称
 * @param {string} query.status - 状态
 * @param {number} query.current - 页码
 * @param {number} query.size - 每页数量
 */
export function pagePost(query) {
  return request({
    url: '/system/post/page',
    method: 'get',
    params: query
  })
}

/**
 * 查询岗位详细信息
 * @param {number} postId - 岗位ID
 */
export function getPost(postId) {
  return request({
    url: '/system/post/' + postId,
    method: 'get'
  })
}

/**
 * 新增岗位
 * @param {Object} data - 岗位信息
 */
export function addPost(data) {
  return request({
    url: '/system/post',
    method: 'post',
    data: data
  })
}

/**
 * 修改岗位
 * @param {Object} data - 岗位信息
 */
export function updatePost(data) {
  return request({
    url: '/system/post',
    method: 'put',
    data: data
  })
}

/**
 * 删除岗位
 * @param {number|number[]} postId - 岗位ID或ID数组
 */
export function delPost(postId) {
  return request({
    url: '/system/post/' + postId,
    method: 'delete'
  })
}

/**
 * 校验岗位名称是否唯一
 * @param {string} postName - 岗位名称
 * @param {number} postId - 岗位ID（编辑时传入）
 */
export function checkPostNameUnique(postName, postId) {
  return request({
    url: '/system/post/checkPostNameUnique',
    method: 'get',
    params: { postName, postId }
  })
}

/**
 * 校验岗位编码是否唯一
 * @param {string} postCode - 岗位编码
 * @param {number} postId - 岗位ID（编辑时传入）
 */
export function checkPostCodeUnique(postCode, postId) {
  return request({
    url: '/system/post/checkPostCodeUnique',
    method: 'get',
    params: { postCode, postId }
  })
}
