import { beforeEach, describe, expect, it, vi } from 'vitest'
import request from '@/utils/request'
import { getLoginPetType, updateLoginPetType } from './config'

vi.mock('@/utils/request', () => ({
  default: vi.fn()
}))

describe('登录宠物配置 API', () => {
  beforeEach(() => {
    vi.mocked(request).mockReset()
  })

  it('通过静默公开 GET 读取宠物类型', () => {
    getLoginPetType()

    expect(request).toHaveBeenCalledWith({
      url: '/system/config/public/login-pet',
      method: 'get',
      silent: true
    })
  })

  it('通过非静默 PUT 仅提交宠物类型', () => {
    updateLoginPetType('owl')

    expect(request).toHaveBeenCalledWith({
      url: '/system/config/login-pet',
      method: 'put',
      data: { type: 'owl' }
    })
    expect(vi.mocked(request).mock.calls[0][0]).not.toHaveProperty('silent')
  })
})
