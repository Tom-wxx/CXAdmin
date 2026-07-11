export const LOGIN_PET_TYPES = ['cat', 'dog', 'owl'] as const

export type LoginPetType = typeof LOGIN_PET_TYPES[number]

export const DEFAULT_LOGIN_PET_TYPE: LoginPetType = 'cat'

export function isLoginPetType(value: unknown): value is LoginPetType {
  return typeof value === 'string' && LOGIN_PET_TYPES.includes(value as LoginPetType)
}

export function normalizeLoginPetType(value: unknown): LoginPetType {
  return isLoginPetType(value) ? value : DEFAULT_LOGIN_PET_TYPE
}
