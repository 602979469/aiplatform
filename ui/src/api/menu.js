import request from '@/utils/request'

// 获取当前用户菜单路由树（后端 /auth/menu/routers）
export const getRouters = () => {
  return request({
    url: '/auth/menu/routers',
    method: 'get'
  })
}
