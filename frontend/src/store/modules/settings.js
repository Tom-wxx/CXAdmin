const getSidebarColor = () => {
  return localStorage.getItem('sidebarColor') || '#001529'
}

const getSidebarPosition = () => {
  return localStorage.getItem('sidebarPosition') || 'left'
}

const state = {
  title: 'CXAdmin',
  fixedHeader: true,
  sidebarLogo: true,
  sidebarColor: getSidebarColor(),
  sidebarPosition: getSidebarPosition()
}

const mutations = {
  CHANGE_SETTING: (state, { key, value }) => {
    if (state.hasOwnProperty(key)) {
      state[key] = value
      if (key === 'sidebarColor') {
        localStorage.setItem('sidebarColor', value)
      }
      if (key === 'sidebarPosition') {
        localStorage.setItem('sidebarPosition', value)
      }
    }
  }
}

const actions = {
  changeSetting({ commit }, data) {
    commit('CHANGE_SETTING', data)
  },
  setSidebarColor({ commit }, color) {
    commit('CHANGE_SETTING', { key: 'sidebarColor', value: color })
  },
  setSidebarPosition({ commit }, position) {
    commit('CHANGE_SETTING', { key: 'sidebarPosition', value: position })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
