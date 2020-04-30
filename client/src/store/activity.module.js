const state = {
    activity: {
        author_id: null,
        name: null,
        continuous: null,
        start_time: null,
        end_time: null,
        description: null,
        location: null,
        activity_types: []
    }
};

const getters = {
    activity(state) {
        return state.activity
    }
};

const mutations = {
    setAuthor(state, data) {
        state.author = data;
    },
    setName(state, data) {
        state.name = data;
    },
    setContinuous(state, data) {
        state.continuous = data;
    },
    setStartTime(state, data) {
        state.start_time = data;
    },
    setEndTime(state, data) {
        state.end_time = data;
    },
    setDescription(state, data) {
        state.description = data;
    },
    setLocation(state, data) {
        state.location = data;
    },
    setActivityTypes(state, data) {
        state.activity_types = data;
    }
};

const actions = {
    createActivity({commit}, data) {
        commit('setAuthor', data);
        commit('setName', data);
        commit('setContinuous', data);
        commit('setStartTime', data);
        commit('setEndTime', data);
        commit('setDescription', data);
        commit('setLocation', data);
        commit('setActivityTypes', data);
    }
};

export default {
    state,
    actions,
    mutations,
    getters
};