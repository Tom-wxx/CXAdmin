import { ref } from 'vue'
import { getLoginPetType } from '@/api/system/config'
import {
  DEFAULT_LOGIN_PET_TYPE,
  normalizeLoginPetType,
  type LoginPetType
} from '@/types/login-pet'

export function useLoginPetConfig() {
  const loginPetType = ref<LoginPetType>(DEFAULT_LOGIN_PET_TYPE)
  const loading = ref(false)

  async function loadLoginPetType(): Promise<void> {
    loading.value = true
    try {
      const response = await getLoginPetType()
      loginPetType.value = normalizeLoginPetType(response.data)
    } catch {
      loginPetType.value = DEFAULT_LOGIN_PET_TYPE
    } finally {
      loading.value = false
    }
  }

  return { loginPetType, loading, loadLoginPetType }
}
