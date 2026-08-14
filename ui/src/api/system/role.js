import request from '@/utils/request'

// 查询角色列表（分页）
export function listRole(query) {
  return request({
    url: '/auth/role/page',
    method: 'get',
    params: query
  })
}

// 查询角色详细
export function getRole(roleId) {
  return request({
    url: '/auth/role/' + roleId,
    method: 'get'
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/auth/role',
    method: 'post',
    data: data
  })
}

// 修改角色
export function updateRole(data) {
  return request({
    url: '/auth/role',
    method: 'put',
    data: data
  })
}

// 角色状态修改
export function changeRoleStatus(roleId, status) {
  return request({
    url: '/auth/role/' + roleId + '/status',
    method: 'put',
    data: { status }
  })
}

// 删除角色
export function delRole(roleId) {
  return request({
    url: '/auth/role/' + roleId,
    method: 'delete'
  })
}

// 查询角色已分配菜单ID（回显）
export function roleMenuTreeselect(roleId) {
  return request({
    url: '/auth/role/' + roleId + '/menu-ids',
    method: 'get'
  })
}

// 保存角色菜单分配
export function saveRoleMenus(roleId, menuIds) {
  return request({
    url: '/auth/role/' + roleId + '/menu',
    method: 'put',
    data: { menuIds }
  })
}

// 全量菜单树（分配弹窗）
export function menuTree() {
  return request({
    url: '/auth/menu/tree',
    method: 'get'
  })
}
