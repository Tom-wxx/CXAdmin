import type { AxiosAdapter, AxiosError, AxiosRequestConfig, AxiosResponse } from 'axios'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

const { dispatch, elMessage, confirm } = vi.hoisted(() => ({
  dispatch: vi.fn(),
  elMessage: vi.fn(),
  confirm: vi.fn()
}))

vi.mock('element-plus', () => ({
  ElMessage: elMessage,
  ElMessageBox: { confirm }
}))

vi.mock('@/store', () => ({
  default: { dispatch }
}))

import request from './request'

interface BackendResult {
  code: number
  message: string
  data: null
  timestamp: number
}

function responseAdapter(body: BackendResult, inspectConfig?: (config: AxiosRequestConfig) => void): AxiosAdapter {
  return async (config): Promise<AxiosResponse<BackendResult>> => {
    inspectConfig?.(config)
    return {
      data: body,
      status: 200,
      statusText: 'OK',
      headers: {},
      config,
      request: {}
    }
  }
}

function networkErrorAdapter(inspectConfig?: (config: AxiosRequestConfig) => void): AxiosAdapter {
  return async (config) => {
    inspectConfig?.(config)
    const error = Object.assign(new Error('Network Error'), {
      config,
      isAxiosError: true,
      toJSON: () => ({ message: 'Network Error' })
    }) as AxiosError
    return Promise.reject(error)
  }
}

function result(code: number, message: string): BackendResult {
  return { code, message, data: null, timestamp: 1 }
}

describe('request silent', () => {
  beforeEach(() => {
    dispatch.mockReset().mockResolvedValue(undefined)
    elMessage.mockReset()
    confirm.mockReset().mockResolvedValue(undefined)
    vi.stubGlobal('location', { href: 'http://localhost/' })
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it.each([
    [500, 'server failed', Error],
    [400, 'business failed', String]
  ] as const)('silent 业务错误 %i 不显示消息且仍拒绝', async (code, message, rejectionType) => {
    let adapterSilent: unknown

    const promise = request({
      url: '/silent-business-error',
      silent: true,
      adapter: responseAdapter(result(code, message), (config) => {
        adapterSilent = (config as AxiosRequestConfig & { silent?: boolean }).silent
      })
    })

    if (rejectionType === Error) {
      await expect(promise).rejects.toThrow(message)
    } else {
      await expect(promise).rejects.toBe('error')
    }
    expect(adapterSilent).toBe(true)
    expect(elMessage).not.toHaveBeenCalled()
  })

  it('silent 401 不确认、不登出且仍拒绝', async () => {
    await expect(request({
      url: '/silent-unauthorized',
      silent: true,
      adapter: responseAdapter(result(401, 'expired'))
    })).rejects.toBe('无效的会话，或者会话已过期，请重新登录。')

    expect(confirm).not.toHaveBeenCalled()
    expect(dispatch).not.toHaveBeenCalled()
  })

  it('silent 网络错误从 error.config 读取选项，不显示消息且保留原错误', async () => {
    let adapterSilent: unknown
    const promise = request({
      url: '/silent-network-error',
      silent: true,
      adapter: networkErrorAdapter((config) => {
        adapterSilent = (config as AxiosRequestConfig & { silent?: boolean }).silent
      })
    })

    await expect(promise).rejects.toMatchObject({ message: 'Network Error' })
    expect(adapterSilent).toBe(true)
    expect(elMessage).not.toHaveBeenCalled()
  })
})

describe('request 非 silent 回归', () => {
  beforeEach(() => {
    dispatch.mockReset().mockResolvedValue(undefined)
    elMessage.mockReset()
    confirm.mockReset().mockResolvedValue(undefined)
    vi.stubGlobal('location', { href: 'http://localhost/' })
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('401 保持确认、登出、跳转与字符串拒绝语义', async () => {
    await expect(request({
      url: '/unauthorized',
      adapter: responseAdapter(result(401, 'expired'))
    })).rejects.toBe('无效的会话，或者会话已过期，请重新登录。')

    expect(confirm).toHaveBeenCalledTimes(1)
    await vi.waitFor(() => {
      expect(dispatch).toHaveBeenCalledWith('user/logout')
      expect(location.href).toBe('/login')
    })
  })

  it('500 保持错误消息与 Error 拒绝语义', async () => {
    await expect(request({
      url: '/server-error',
      adapter: responseAdapter(result(500, 'server failed'))
    })).rejects.toThrow('server failed')

    expect(elMessage).toHaveBeenCalledWith({
      message: 'server failed',
      type: 'error',
      duration: 5 * 1000
    })
  })

  it('其他业务码保持错误消息与字符串拒绝语义', async () => {
    await expect(request({
      url: '/business-error',
      adapter: responseAdapter(result(400, 'business failed'))
    })).rejects.toBe('error')

    expect(elMessage).toHaveBeenCalledWith({
      message: 'business failed',
      type: 'error',
      duration: 5 * 1000
    })
  })

  it('网络错误保持翻译消息并拒绝原错误', async () => {
    const promise = request({
      url: '/network-error',
      adapter: networkErrorAdapter()
    })

    await expect(promise).rejects.toMatchObject({ message: 'Network Error' })
    expect(elMessage).toHaveBeenCalledWith({
      message: '后端接口连接异常',
      type: 'error',
      duration: 5 * 1000
    })
  })
})
