import request from '@/utils/request'

// 查询在线用户列表（分页）
export function list(query) {
  return request({
    url: '/auth/online/list',
    method: 'get',
    params: query
  })
}

// 强退用户（按用户ID）
export function forceLogout(userId) {
  return request({
    url: '/auth/online/logout',
    method: 'post',
    data: { userId }
  })
}
