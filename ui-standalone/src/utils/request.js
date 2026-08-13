import axios from 'axios'
import { Message } from 'element-ui'

const service = axios.create({
  baseURL: '',
  timeout: 180000
})

service.interceptors.response.use(
  response => {
    // 文件流下载：直接返回 Blob
    if (response.request && response.request.responseType === 'blob') {
      return response.data
    }
    const res = response.data
    if (res.errorCode !== 0 || !res.success) {
      Message({ message: res.errorMessage || '请求失败', type: 'error', duration: 3000 })
      return Promise.reject(new Error(res.errorMessage || '请求失败'))
    }
    return res
  },
  error => {
    Message({ message: error.message || '网络异常', type: 'error', duration: 3000 })
    return Promise.reject(error)
  }
)

export default service
