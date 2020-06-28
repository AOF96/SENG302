const state = {
  userSearch: {
    searchTerm: null,
    searchType: null,
    page: null,
    size: null,
    scrollPos: 0,
  }
};

const getters = {
  userSearch(state) {
    return state.userSearch;
  }
};

const mutations = {
  setSearchTerm(state, data) {
    state.userSearch.searchTerm = data.searchTerm;
  },
  setSearchType(state, data) {
    state.userSearch.searchType = data.searchType;
  },
  setPage(state, data) {
    state.userSearch.page = data.page;
  },
  setSize(state, data) {
    state.userSearch.size = data.size;
  },
  setScrollPos(state, data) {
    state.userSearch.scrollPos = data.scrollPos;
  },
  clearSearch() {
    state.userSearch.searchTerm = null;
    state.userSearch.searchType = null;
    state.userSearch.page = null;
    state.userSearch.size = null;
    state.userSearch.scrollPos = 0;
  }
};

const actions = {
  setUserSearch({commit}, data) {
    commit('setSearchTerm', data);
    commit('setSearchType', data);
    commit('setPage', data);
    commit('setSize', data);
    commit('setScrollPos', data);
  },
  setScrollPosition({commit}, data) {
    commit('setScrollPos', data);
  },
  resetSearch({ commit }) {
    commit('clearSearch')
  },
};

export default {
  state,
  actions,
  mutations,
  getters
};
