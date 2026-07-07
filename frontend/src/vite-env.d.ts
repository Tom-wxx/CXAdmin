/// <reference types="vite/client" />

// 仅扩展 ImportMetaEnv；ImportMeta 本身由 vite/client 声明（含 env / glob / 内置变量），
// 不要重声明 ImportMeta，否则会收窄类型、丢失 BASE_URL/MODE/DEV/PROD 等内置项。
interface ImportMetaEnv {
  readonly VITE_APP_BASE_API: string
}
