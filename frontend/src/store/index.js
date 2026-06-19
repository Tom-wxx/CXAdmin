import { createStore } from 'vuex'
import getters from './getters'
import user from './modules/user'
import app from './modules/app'
import permission from './modules/permission'
import settings from './modules/settings'

const store = createStore({
  modules: {
    user,
    app,
    permission,
    settings
  },
  getters
})

export default store
