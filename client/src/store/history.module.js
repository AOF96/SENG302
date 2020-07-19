const state = {
  userSearch: {
    searchTerm: null,
    searchType: null,
    page: null,
    size: null,
    scrollPos: 0,
    activityTypesSelected: [],
    filterMethod: null
  },
  pageHistory: {
    previousPages: [],
    nextPages: [],
  }
};

const getters = {
  userSearch(state) {
    return state.userSearch;
  },
  pageHistory(state) {
    return state.pageHistory;
  },
  getPreviousPage(state) {
    if (state.previousPages !== []) {
      return state.pageHistory.previousPages[state.pageHistory.previousPages.length - 1];
    } else {
      return null;
    }
  },
  getNextPage(state) {
    if (state.nextPages !== []) {
      return state.pageHistory.nextPages[state.pageHistory.nextPages.length - 1];
    } else {
      return null;
    }
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
  setActivityTypes(state, data) {
    state.userSearch.activityTypesSelected = data.activityTypesSelected;
  },
  setFilterMethod(state, data) {
    state.userSearch.filterMethod = data.filterMethod;
  },
  clearSearch() {
    state.userSearch.searchTerm = null;
    state.userSearch.searchType = null;
    state.userSearch.page = null;
    state.userSearch.size = null;
    state.userSearch.scrollPos = 0;
    state.activityTypesSelected = [];
    state.userSearch.filterMethod = null;
  },
  clearPages(state) {
    state.pageHistory.previousPages = [];
    state.pageHistory.nextPages = [];
  },
  addPreviousPage(state, page) {
    state.pageHistory.previousPages.push(page);
  },
  removePreviousPage(state) {
    state.pageHistory.previousPages.pop();
  },
  addNextPage(state, page) {
    state.pageHistory.nextPages.push(page);
  },
  removeNextPage(state) {
    state.pageHistory.nextPages.pop();
  },
  clearNextHistory(state) {
    state.pageHistory.nextPages = [];
  }
};

const actions = {
  setUserSearch({commit}, data) {
    commit('setSearchTerm', data);
    commit('setSearchType', data);
    commit('setPage', data);
    commit('setSize', data);
    commit('setScrollPos', data);
    commit('setActivityTypes', data);
    commit('setFilterMethod', data);
  },
  setScrollPosition({commit}, data) {
    commit('setScrollPos', data);
  },
  resetSearch({ commit }) {
    commit('clearSearch')
  },
  previousPage({commit}, currentPage) {
    commit('addNextPage', currentPage);
    commit('removePreviousPage');
  },
  nextPage({commit}, currentPage) {
    commit('addPreviousPage', currentPage);
    commit('removeNextPage');
  },
  resetPageHistory({commit}) {
    commit('clearPages')
  },
  addPreviousPage({commit}, page) {
    commit('addPreviousPage', page)
  },
  addNextPage({commit}, page) {
    commit('addNextPage', page)
  },
  clearNextHistory({commit}) {
    commit('clearNextHistory')
  }
};

export default {
  state,
  actions,
  mutations,
  getters
};
