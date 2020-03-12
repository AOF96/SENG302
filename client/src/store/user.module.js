const state = {
  user: {
    firstname: null
  }
}

const getters = {
  user(state) {
    return state.user
  }
}

const mutations = {
  setUser(state, data) {
    state.user = data
  }
}

const actions = {
  updateUser({ commit }, data) {
    commit('setUser', data)
  }
}

export default {
  state,
  actions,
  mutations,
  getters
}
