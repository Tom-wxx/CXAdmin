import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type AxiosError } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import store from '@/store'
import { HTTP_OK, HTTP_UNAUTHORIZED, HTTP_SERVER_ERROR } from '@/utils/constants'
import type { Result } from '@/types/api'

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 10000,
  withCredentials: true
})

// request 拦截器
service.interceptors.request.use(
  (config: AxiosRequestConfig) => config,
  (error: unknown) => Promise.reject(error)
)

// response 拦截器：成功时把后端 Result 解包返回
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    const code = res.code || HTTP_OK

    if (code === HTTP_UNAUTHORIZED) {
      ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
        confirmButtonText: '重新登录',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        store.dispatch('user/logout').then(() => {
          location.href = '/login'
        })
      })
      return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
    } else if (code === HTTP_SERVER_ERROR) {
      ElMessage({
        message: res.message || 'Error',
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(new Error(res.message || 'Error'))
    } else if (code !== HTTP_OK) {
      ElMessage({
        message: res.message || 'Error',
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject('error')
    } else {
      return res
    }
  },
  (error: AxiosError) => {
    let message = error.message || ''
    if (message === 'Network Error') {
      message = '后端接口连接异常'
    } else if (message.includes('timeout')) {
      message = '系统接口请求超时'
    } else if (message.includes('Request failed with status code')) {
      message = '系统接口' + message.substr(message.length - 3) + '异常'
    }
    ElMessage({
      message: message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

/**
 * 业务层类型化请求。
 * 响应拦截器已把后端响应解包为 Result，故返回 Promise<Result<T>>；
 * 对 responseType:'blob' 等场景，由调用方按需断言为 Promise<Blob>。
 */
export function request<T = unknown>(config: AxiosRequestConfig): Promise<Result<T>> {
  return service(config) as unknown as Promise<Result<T>>
}

export default request
