import {apiActivity} from "../api";

const state = {
    activity: {
        author_id: null,
        name: null,
        continuous: null,
        start_time: null,
        end_time: null,
        description: null,
        location: null,
        activity_types: [],
        activity_id: null
    }
};

const getters = {
    activity(state) {
        return state.activity
    }
};

const mutations = {
    setAuthor(state, data) {
        state.activity.author_id = data.author.profile_id;
    },
    setName(state, data) {
        state.activity.name = data.activity_name;
    },
    setContinuous(state, data) {
        state.activity.continuous = data.continuous;
    },
    setStartTime(state, data) {
        state.activity.start_time = data.start_time;
    },
    setEndTime(state, data) {
        state.activity.end_time = data.end_time;
    },
    setDescription(state, data) {
        state.activity.description = data.description;
    },
    setLocation(state, data) {
        state.activity.location = data.location;
    },
    setActivityTypes(state, data) {
        state.activity.activity_types = data.activity_type;
    },
    setActivityId(state, data) {
        state.activity.activity_id = data.id;
    }
};

const actions = {
    createActivity({commit}, data) {
        commit('setActivityId', data);
        commit('setAuthor', data);
        commit('setName', data);
        commit('setContinuous', data);
        commit('setStartTime', data);
        commit('setEndTime', data);
        commit('setDescription', data);
        commit('setLocation', data);
        commit('setActivityTypes', data);
    },
    async addActivity(data, {'id': id, 'name': name, 'duration': duration, 'startTime': startTime, 'endTime': endTime, 'description': description, 'location': location, 'activityTypes': activityTypes}) {
        await apiActivity.addActivity(id, name, duration, startTime, endTime, description, location, activityTypes);
    },
    async getActivityById(data, id) {
        return await apiActivity.getActivityById(id)
    },
    async editActivity(data, {'userId': userId, 'name': name, 'duration': duration, 'startTime': startTime, 'endTime': endTime, 'description': description, 'location': location, 'activityTypes': activityTypes, 'activityId': activityId}) {
        return await apiActivity.editActivity(userId, name, duration, startTime, endTime,description, location, activityTypes, activityId);
    },
    async deleteActivity(data, {'userId': userId, 'activityId': activityId}) {
        return await apiActivity.deleteActivity(userId, activityId);
    },
    async addActivityAchievement(data, {'userId': userId, 'activityId': activityId, 'name': name, 'description': description, 'result_type': result_type }) {
        return await apiActivity.addActivityAchievement(userId, activityId, name, description, result_type);
    },
    async editActivityAchievement(data, {'userId': userId, 'activityId': activityId, 'achievementId': achievementId, 'name': name, 'description': description, 'result_type': result_type }) {
        return await apiActivity.addActivityAchievement(userId, activityId, achievementId, name, description, result_type);
    },
    async deleteActivityAchievement(data, {'userId': userId, 'activityId': activityId, 'achievementId': achievementId }) {
        return await apiActivity.deleteActivity(userId, activityId, achievementId);
    },
    /**
     * Calls the api to send a request to add a result
     * @param data
     * @param profileId
     * @param achievementId
     * @param value
     * @returns {Promise<AxiosResponse<T>>}
     */
    async addResult(data, {'profileId': profileId, 'achievementId': achievementId, 'value': value}) {
        return await apiActivity.addResult(profileId, achievementId, value);
    }

};

export default {
    state,
    actions,
    mutations,
    getters
};
