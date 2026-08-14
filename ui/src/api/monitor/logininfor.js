import request from '@/utils/request'

// 查询登录日志列表（分页）
export function list(query) {
  return request({
    url: '/auth/login-log/page',
    method: 'get',
    params: query
  })
}

// 删除登录日志
export function delLogininfor(logId) {
  return request({
    url: '/auth/login-log/' + logId,
    method: 'delete'
  })
}
