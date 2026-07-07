import { createStore, type Store } from 'vuex'
import type { InjectionKey } from 'vue'
import getters from './getters'
import user from './modules/user'
import app from './modules/app'
import permission from './modules/permission'
import settings from './modules/settings'
import type { RootState } from '@/types/store'

/** 注入键：配合 useStore(key) 获得类型化 store */
export const key: InjectionKey<Store<RootState>> = Symbol('store')

const store = createStore<RootState>({
  modules: {
    user,
    app,
    permission,
    settings
  },
  getters
})

export default store
