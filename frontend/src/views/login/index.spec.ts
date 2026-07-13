import { shallowMount, type VueWrapper } from '@vue/test-utils'
import { defineComponent, nextTick, ref } from 'vue'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import LoginView from './index.vue'

const mocks = vi.hoisted(() => ({
  getCaptcha: vi.fn(),
  listEnabledProviders: vi.fn(),
  loadLoginPetType: vi.fn(),
  login: vi.fn(),
  routerPush: vi.fn(),
  useLoginPetConfig: vi.fn()
}))

vi.mock('@/api/login', () => ({
  getCaptcha: mocks.getCaptcha
}))

vi.mock('@/api/system/sso', () => ({
  listEnabledProviders: mocks.listEnabledProviders
}))

vi.mock('@/composables/store', () => ({
  useUserStore: () => ({ login: mocks.login })
}))

vi.mock('@/composables/useLoginPetConfig', () => ({
  useLoginPetConfig: mocks.useLoginPetConfig
}))

vi.mock('vue-router', () => ({
  useRoute: () => ({ query: {} }),
  useRouter: () => ({ push: mocks.routerPush })
}))

const LoginPetStub = defineComponent({
  name: 'LoginPet',
  props: {
    type: { type: String, required: true },
    size: { type: Number, required: true }
  },
  template: '<div data-testid="login-pet" :data-pet="type" :data-size="size" />'
})

const wrappers: VueWrapper[] = []

function mountLoginView() {
  const wrapper = shallowMount(LoginView, {
    global: {
      stubs: {
        LoginPet: LoginPetStub,
        ElButton: true,
        ElForm: true,
        ElFormItem: true,
        ElIcon: true,
        ElInput: true,
        ElLink: true,
        ElTooltip: true,
        Hide: true,
        View: true
      }
    }
  })
  wrappers.push(wrapper)
  return wrapper
}

describe('登录页动态宠物集成', () => {
  const loginPetType = ref<'cat' | 'dog' | 'owl'>('cat')

  beforeEach(() => {
    vi.clearAllMocks()
    loginPetType.value = 'cat'
    mocks.getCaptcha.mockResolvedValue({ data: { captchaEnabled: false } })
    mocks.listEnabledProviders.mockResolvedValue({ data: [] })
    mocks.loadLoginPetType.mockResolvedValue(undefined)
    mocks.useLoginPetConfig.mockReturnValue({
      loginPetType,
      loading: ref(false),
      loadLoginPetType: mocks.loadLoginPetType
    })
  })

  afterEach(() => {
    wrappers.splice(0).forEach(wrapper => wrapper.unmount())
  })

  it('用 108px 全局宠物替换原平台图标并保留登录区', () => {
    loginPetType.value = 'dog'

    const wrapper = mountLoginView()
    const pet = wrapper.get('[data-testid="login-pet"]')

    expect(pet.attributes()).toMatchObject({
      'data-pet': 'dog',
      'data-size': '108'
    })
    expect(wrapper.find('.brand-icon').exists()).toBe(false)
    expect(wrapper.find('platform-stub, platform').exists()).toBe(false)
    expect(wrapper.find('.login-form-wrapper').exists()).toBe(true)
    expect(wrapper.find('.login-form').exists()).toBe(true)
  })

  it('以 cat 为默认类型并在配置 ref 更新后同步宠物 props', async () => {
    const wrapper = mountLoginView()

    expect(wrapper.get('[data-testid="login-pet"]').attributes('data-pet')).toBe('cat')

    loginPetType.value = 'owl'
    await nextTick()

    expect(wrapper.get('[data-testid="login-pet"]').attributes('data-pet')).toBe('owl')
  })

  it('setup 加载配置一次且不阻塞验证码与 SSO 初始化', () => {
    mocks.loadLoginPetType.mockReturnValue(new Promise<void>(() => undefined))

    mountLoginView()

    expect(mocks.loadLoginPetType).toHaveBeenCalledTimes(1)
    expect(mocks.getCaptcha).toHaveBeenCalledTimes(1)
    expect(mocks.listEnabledProviders).toHaveBeenCalledTimes(1)
  })
})
