import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type AxiosError } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import store from '@/store'
import { HTTP_OK, HTTP_UNAUTHORIZED, HTTP_SERVER_ERROR } from '@/utils/constants'

export interface AppRequestConfig extends AxiosRequestConfig {
  /** 仅抑制当前请求的全局错误提示与登录失效处理 */
  silent?: boolean
}

function isSilent(config?: AxiosRequestConfig): boolean {
  return Boolean((config as AppRequestConfig | undefined)?.silent)
}

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
    const silent = isSilent(response.config)

    if (code === HTTP_UNAUTHORIZED) {
      if (!silent) {
        ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('user/logout').then(() => {
            location.href = '/login'
          })
        })
      }
      return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
    } else if (code === HTTP_SERVER_ERROR) {
      if (!silent) {
        ElMessage({
          message: res.message || 'Error',
          type: 'error',
          duration: 5 * 1000
        })
      }
      return Promise.reject(new Error(res.message || 'Error'))
    } else if (code !== HTTP_OK) {
      if (!silent) {
        ElMessage({
          message: res.message || 'Error',
          type: 'error',
          duration: 5 * 1000
        })
      }
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
    if (!isSilent(error.config)) {
      ElMessage({
        message: message,
        type: 'error',
        duration: 5 * 1000
      })
    }
    return Promise.reject(error)
  }
)

/**
 * 业务层类型化请求。返回类型由调用方注解决定（上下文推断 T）：
 * - 对象接口注解 Promise<Result<X>>（拦截器返回的 body 即 Result）
 * - 列表接口注解 Promise<TableResponse<X>>（body 顶层带 rows/total）
 * - responseType:'blob' 等由调用方断言为 Promise<Blob>
 */
export function request<T = unknown>(config: AppRequestConfig): Promise<T> {
  return service(config) as unknown as Promise<T>
}

export default request
