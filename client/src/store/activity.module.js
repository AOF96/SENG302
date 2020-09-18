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
        return await apiActivity.getActivityById(id);
    },
    async getActivityUpdates(data, {'id': id, 'page': page, 'size': size}) {
        return await apiActivity.getActivityUpdates(id, page, size);
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
        return await apiActivity.editActivityAchievement(userId, activityId, achievementId, name, description, result_type);
    },
    async deleteActivityAchievement(data, {'userId': userId, 'activityId': activityId, 'achievementId': achievementId }) {
        return await apiActivity.deleteActivityAchievement(userId, activityId, achievementId);
    },
    async getActivityAchievement(data, {'userId': userId, 'activityId': activityId}) {
        return await apiActivity.getActivityAchievement(userId, activityId);
    },
    async getParticipants(data, {'activityId': activityId, 'page': page, 'size': size}) {
        return await apiActivity.getParticipants(activityId, page, size);
    },
    async getOrganisers(data, {'activityId': activityId, 'page': page, 'size': size}) {
        return await apiActivity.getParticipants(activityId, page, size);
    },
    async editUserActivityRole(data, {'userId': userId ,'activityId': activityId, 'role': role, 'email': email}) {
        return await apiActivity.editUserActivityRole(userId, activityId, role, email);
    },
    async checkUserActivityVisibility(data, {'profileId': profileId, 'activityId': activityId }) {
        return await apiActivity.checkUserActivityVisibility(profileId, activityId);
    },
    async getSearchedActivity(data, {'searchTerm': searchTerm, 'page': page, 'size': size}) {
        return await apiActivity.getSearchedActivity(searchTerm, page, size);
    },

    async getActivityInRange(data, {'latitudeBottomLeft': latitudeBottomLeft, 'latitudeTopRight': latitudeTopRight,
        'longitudeBottomLeft': longitudeBottomLeft, 'longitudeTopRight': longitudeTopRight}) {
        return await apiActivity.getActivityInRange(latitudeBottomLeft, latitudeTopRight, longitudeBottomLeft, longitudeTopRight);
    },
    /**
     * Calls the api to send a request to add a result
     * @param data
     * @param profileId Id of profile
     * @param achievementId Id of achievement to add result to
     * @param value value of result
     * @returns {Promise<AxiosResponse<T>>}
     */
    async addResult(data, {'profileId': profileId, 'achievementId': achievementId, 'value': value}) {
        return await apiActivity.addResult(profileId, achievementId, value);
    },
    /**
     * Calls the api to send a get request to retrieve one result
     * @param data
     * @param profileId Id of profile
     * @param achievementId Id of achievement
     * @param resultId Id of result
     * @returns {Promise<AxiosResponse<T>>}
     */
    async getOneResult(data, {'profileId': profileId, 'achievementId': achievementId, 'resultId': resultId}) {
        return await apiActivity.getOneResult(profileId, achievementId, resultId);
    },
    /**
     * Calls the api to send a get request to retrieve all achievements
     * @param data
     * @param achievementId Id of the achievement
     * @returns {Promise<AxiosResponse<any>>}
     */
    async getResults(data, {'achievementId': achievementId}) {
        return await apiActivity.getResults(achievementId);
    },
    /**
     * Calls the api method to get location for the activity with the provided id
     * @param data
     * @param activityId id of the activity that the location for is to be retrieved
     * @returns {Promise<AxiosResponse<any>>}
     */
    async getLocationForActivity(data, {'activityId': activityId}) {
        return await apiActivity.getLocationForActivity(activityId);
    }
};

export default {
    state,
    actions,
    mutations,
    getters
};
