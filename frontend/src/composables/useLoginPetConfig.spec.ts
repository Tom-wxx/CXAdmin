import { beforeEach, describe, expect, it, vi } from 'vitest'
import { getLoginPetType } from '@/api/system/config'
import type { LoginPetType } from '@/types/login-pet'
import { useLoginPetConfig } from './useLoginPetConfig'

vi.mock('@/api/system/config', () => ({
  getLoginPetType: vi.fn()
}))

function loginPetResponse(data: unknown) {
  return {
    code: 200,
    message: 'ok',
    data: data as LoginPetType,
    timestamp: 1
  }
}

describe('useLoginPetConfig', () => {
  beforeEach(() => {
    vi.mocked(getLoginPetType).mockReset()
  })

  it.each<LoginPetType>(['cat', 'dog', 'owl'])('保留合法宠物类型 %s', async (type) => {
    vi.mocked(getLoginPetType).mockResolvedValue(loginPetResponse(type))

    const config = useLoginPetConfig()
    expect(config.loginPetType.value).toBe('cat')

    await config.loadLoginPetType()

    expect(config.loginPetType.value).toBe(type)
  })

  it.each([undefined, null, '', 'fox'])('响应缺失或非法时回退猫咪：%s', async (value) => {
    vi.mocked(getLoginPetType).mockResolvedValue(loginPetResponse(value))
    const config = useLoginPetConfig()

    await config.loadLoginPetType()

    expect(config.loginPetType.value).toBe('cat')
  })

  it('请求失败不抛出并回退猫咪', async () => {
    vi.mocked(getLoginPetType)
      .mockResolvedValueOnce(loginPetResponse('owl'))
      .mockRejectedValueOnce(new Error('offline'))
    const config = useLoginPetConfig()
    await config.loadLoginPetType()
    expect(config.loginPetType.value).toBe('owl')

    await expect(config.loadLoginPetType()).resolves.toBeUndefined()

    expect(config.loginPetType.value).toBe('cat')
  })

  it('请求期间标记 loading，成功后恢复', async () => {
    let resolveRequest!: (value: ReturnType<typeof loginPetResponse>) => void
    vi.mocked(getLoginPetType).mockReturnValue(new Promise((resolve) => {
      resolveRequest = resolve
    }))
    const config = useLoginPetConfig()

    const loadingPromise = config.loadLoginPetType()
    expect(config.loading.value).toBe(true)

    resolveRequest(loginPetResponse('dog'))
    await loadingPromise

    expect(config.loading.value).toBe(false)
  })

  it('请求失败后也恢复 loading', async () => {
    let rejectRequest!: (reason: Error) => void
    vi.mocked(getLoginPetType).mockReturnValue(new Promise((_resolve, reject) => {
      rejectRequest = reject
    }))
    const config = useLoginPetConfig()

    const loadingPromise = config.loadLoginPetType()
    expect(config.loading.value).toBe(true)

    rejectRequest(new Error('offline'))
    await loadingPromise

    expect(config.loading.value).toBe(false)
  })
})
