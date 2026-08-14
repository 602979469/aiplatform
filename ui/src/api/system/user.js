import request from '@/utils/request'

// 查询用户列表（分页，返回 PageResult）
export function listUser(query) {
  return request({
    url: '/auth/user/page',
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function getUser(userId) {
  return request({
    url: '/auth/user/' + userId,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/auth/user',
    method: 'post',
    data: data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/auth/user',
    method: 'put',
    data: data
  })
}

// 删除用户
export function delUser(userId) {
  return request({
    url: '/auth/user/' + userId,
    method: 'delete'
  })
}

// 重置密码
export function resetUserPwd(userId, password) {
  return request({
    url: '/auth/user/' + userId + '/password',
    method: 'put',
    data: { password }
  })
}

// 用户状态修改
export function changeUserStatus(userId, status) {
  return request({
    url: '/auth/user/' + userId + '/status',
    method: 'put',
    data: { status }
  })
}

// 分配用户角色
export function updateUserRole(userId, roleIds) {
  return request({
    url: '/auth/user/' + userId + '/role',
    method: 'put',
    data: { roleIds }
  })
}

// 查询用户已分配角色ID（编辑回显）
export function getUserRoleIds(userId) {
  return request({
    url: '/auth/user/' + userId + '/role',
    method: 'get'
  })
}

// 查询角色列表（角色下拉）
export function listRole(query) {
  return request({
    url: '/auth/role/page',
    method: 'get',
    params: query
  })
}

// ---------- 个人中心（后端接口待补充，先按 /auth/user/profile 预留） ----------

// 查询当前用户个人信息
export function getUserProfile() {
  return request({
    url: '/auth/user/profile',
    method: 'get'
  })
}

// 修改当前用户个人信息
export function updateUserProfile(data) {
  return request({
    url: '/auth/user/profile',
    method: 'put',
    data: data
  })
}

// 修改当前用户密码
export function updateUserPwd(oldPassword, newPassword) {
  return request({
    url: '/auth/user/profile/password',
    method: 'put',
    data: { oldPassword, newPassword }
  })
}

// 上传头像
export function uploadAvatar(data) {
  return request({
    url: '/auth/user/profile/avatar',
    method: 'put',
    data: data
  })
}
