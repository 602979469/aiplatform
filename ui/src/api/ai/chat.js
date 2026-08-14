import request from '@/utils/request'

// 查询会话列表
export function listSession(query) {
  return request({
    url: '/ai/chat/session/list',
    method: 'get',
    params: query
  })
}

// 新建会话
export function addSession(data) {
  return request({
    url: '/ai/chat/session',
    method: 'post',
    data: data
  })
}

// 修改会话标题
export function updateSession(data) {
  return request({
    url: '/ai/chat/session',
    method: 'put',
    data: data
  })
}

// 删除会话
export function delSession(sessionId) {
  return request({
    url: '/ai/chat/session/' + sessionId,
    method: 'delete'
  })
}

// 查询会话消息记录
export function listMessage(sessionId) {
  return request({
    url: '/ai/chat/message/list',
    method: 'get',
    params: { sessionId: sessionId }
  })
}

// 发起对话（一次返回完整回答，不流式输出）
export function sendChat(data) {
  return request({
    url: '/ai/chat/message',
    method: 'post',
    data: data,
    timeout: 180000
  })
}
