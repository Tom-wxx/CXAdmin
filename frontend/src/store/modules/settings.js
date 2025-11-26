// 从 localStorage 读取保存的侧边栏颜色
const getSidebarColor = () => {
  return localStorage.getItem('sidebarColor') || '#304156'
}

// 从 localStorage 读取保存的侧边栏位置
const getSidebarPosition = () => {
  return localStorage.getItem('sidebarPosition') || 'left'
}

const state = {
  title: '后台管理系统',
  fixedHeader: true,
  sidebarLogo: true,
  sidebarColor: getSidebarColor(),
  sidebarPosition: getSidebarPosition() // 侧边栏位置: 'left' | 'top'
}

const mutations = {
  CHANGE_SETTING: (state, { key, value }) => {
    if (state.hasOwnProperty(key)) {
      state[key] = value
      // 如果是侧边栏颜色，保存到 localStorage
      if (key === 'sidebarColor') {
        localStorage.setItem('sidebarColor', value)
      }
      // 如果是侧边栏位置，保存到 localStorage
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
