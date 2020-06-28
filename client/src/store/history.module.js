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
  }
};

export default {
  state,
  actions,
  mutations,
  getters
};
