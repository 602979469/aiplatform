import request from '@/utils/request'

// 搜索镜像（AI 判断名称/版本/厂商，逐厂商探测）
export function searchMirror(data) {
  return request({
    url: '/ai/mirror/search',
    method: 'post',
    data: data,
    timeout: 180000
  })
}

// 生成下载链接（docker pull + docker save）
export function generateDownload(data) {
  return request({
    url: '/ai/mirror/download/generate',
    method: 'post',
    data: data,
    timeout: 30000
  })
}

// 查询生成进度
export function getDownloadStatus(taskId) {
  return request({
    url: '/ai/mirror/download/status',
    method: 'get',
    params: { taskId: taskId }
  })
}

// 下载 tar 文件
export function downloadMirrorFile(fileName) {
  return request({
    url: '/ai/mirror/download/file',
    method: 'get',
    params: { fileName: fileName },
    responseType: 'blob',
    timeout: 600000
  })
}
