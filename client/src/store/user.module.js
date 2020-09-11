import {apiUser} from "../api";
import axios from "axios";


const state = {
  user: {
    firstname: null,
    lastname: null,
    middlename: null,
    nickname: null,
    gender: "Gender",
    primary_email: null,
    additional_email: [],
    date_of_birth: null,
    bio: null,
    isLogin: false,
    fitness: -1,
    profile_id: null,
    password: null,
    passports: [],
    tmp_passports: [],
    permission_level: 0,
    activities: [],
    tmp_activities: [],
    cont_activities: [],
    dur_activities: [],
    location: {
      street_address: null,
      suburb: null,
      postcode: null,
      city: null,
      state: null,
      country: null,
      latitude: null,
      longitude: null,
    },
  },
};

const getters = {
  user(state) {
    return state.user;
  },
  searchedUser(state) {
    return state.user;
  },
  isLoggedIn(state) {
    return state.user.isLogin;
  },
  isAdmin(state) {
    return (state.user.permission_level > 0);
  },
  getProfileId(state) {
    return (state.user.profile_id);
  },
};

const mutations = {
  setUser(state, data) {
    state.user = data;
  },
  setUserFirstName(state, data) {
    if (data.firstname != "") {
      state.user.firstname = data.firstname;
    }
  },
  setUserLastName(state, data) {
    if (data.lastname != "") {
      state.user.lastname = data.lastname;
    }
  },
  setUserMiddleName(state, data) {
    state.user.middlename = data.middlename;
  },
  setUserNickName(state, data) {
    state.user.nickname = data.nickname;
  },
  setUserGender(state, data) {
    if (data.gender != "") {
      state.user.gender = data.gender;
    }
  },
  setUserEmail(state, data) {
    if (data.primary_email != "") {
      state.user.primary_email = data.primary_email;
    }
  },
  setUserID(state, data) {
    if (data.profile_id != "") {
      state.user.profile_id = data.profile_id;
    }
  },
  setUserSecondaryEmails(state, data) {
    state.user.additional_email = data.additional_email;
  },
  setUserBirthday(state, data) {
    if (data.birthday != "") {
      state.user.date_of_birth = data.date_of_birth;
    }
  },
  setUserBio(state, data) {
    if (data.bio != "") {
      state.user.bio = data.bio;
    }
  },
  setUserLocation(state, data) {
    if (data.location !== undefined) {
      state.user.location.street_address = data.location.street_address;
      state.user.location.suburb = data.location.suburb;
      state.user.location.postcode = data.location.postcode;
      state.user.location.city = data.location.city;
      state.user.location.state = data.location.state;
      state.user.location.country = data.location.country;
      state.user.location.longitude = data.location.longitude;
      state.user.location.latitude = data.location.latitude;
    } else {
      state.user.location.street_address = data.street_address;
      state.user.location.suburb = data.suburb;
      state.user.location.postcode = data.postcode;
      state.user.location.city = data.city;
      state.user.location.state = data.state;
      state.user.location.country = data.country;
      state.user.location.longitude = data.longitude;
      state.user.location.latitude = data.latitude;
    }
  },
  setUserPassports(state, data) {
    state.user.passports = data.passports;
  },
  setUserTmpPassports(state, data) {
    state.user.tmp_passports = data.tmp_passports;
  },
  setUserContinuousActivities(state, data) {
    state.user.cont_activities = data;
  },
  setUserDurationActivities(state, data) {
    state.user.dur_activities = data;
  },
  setUserFitnessLevel(state, data) {
    state.user.fitness = data.fitness;
  },
  setUserActivity(state, data) {
    let activityTypes = data.activities;
    for (let i = 0; i < activityTypes.length; i++) {
      activityTypes[i] = activityTypes[i].replace(/-/g, " ")
    }
    state.user.activities = activityTypes;
  },
  setUserTmpActivity(state, data) {
    let activityTypes = data.tmp_activities;
    for (let i = 0; i < activityTypes.length; i++) {
      activityTypes[i] = activityTypes[i].replace(/-/g, " ")
    }
    state.user.tmp_activities = activityTypes;
  },
  setUserIsLogin(state, data) {
    if (data.isLogin != "") {
      state.user.isLogin = data.isLogin;
    }
  },
  setUserPermissionLevel(state, data) {
    state.user.permission_level = data.permission_level;
  },
  userLogin() {
    state.user.isLogin = true;
  },
  userLogout() {
    state.user.firstname = null;
    state.user.lastname = null;
    state.user.middlename = null;
    state.user.nickname = null;
    state.user.gender = "Gender";
    state.user.primary_email = null;
    state.user.additional_email = [];
    state.user.date_of_birth = null;
    state.user.bio = null;
    state.user.isLogin = false;
    state.user.fitness = -1;
    state.user.profile_id = null;
    state.user.password = null;
    state.user.passports = [];
    state.user.tmp_passports = [];
    state.user.permission_level = 0;
    state.user.activities = [];
    state.user.tmp_activities = [];
    state.user.cont_activities = [];
    state.user.dur_activities = [];
    state.user.location = {
      street_address: null,
      suburb: null,
      postcode: null,
      city: null,
      state: null,
      county: null,
      latitude: null,
      longitude: null,
    };
    apiUser.logout();
  },
  setUserPassword(state, data) {
    if (data.password != "") {
      state.user.password = data.password;
    }
  },
};

const actions = {
  createUserProfile({ commit }, data) {
    commit("setUserFirstName", data);
    commit("setUserLastName", data);
    commit("setUserMiddleName", data);
    commit("setUserNickName", data);
    commit("setUserEmail", data);
    commit("setUserGender", data);
    commit("setUserBirthday", data);
    commit("setUserBio", data);
    commit("setUserPassword", data);
    commit("setUserFitnessLevel", data);
    commit("setUserSecondaryEmails", data);
    commit("setUserID", data);
    commit("userLogin");
  },
  updateUserProfile({ commit }, data) {
    commit("setUserFirstName", data);
    commit("setUserLastName", data);
    commit("setUserMiddleName", data);
    commit("setUserNickName", data);
    commit("setUserGender", data);
    commit("setUserBirthday", data);
    commit("setUserBio", data);
    commit("setUserPassports", data);
    commit("setUserActivity", data);
    commit("setUserFitnessLevel", data);
    commit("setUserEmail", data);
    commit("setUserLocation", data);
    commit("setUserSecondaryEmails", data);
    commit("setUserID", data);
    commit("setUserPermissionLevel", data);
    commit("userLogin");
  },
  updateUserContinuousActivities({ commit }, data) {
    commit("setUserContinuousActivities", data);
  },
  updateUserDurationActivities({ commit }, data) {
    commit("setUserDurationActivities", data);
  },
  updateUserEmail({ commit }, data) {
    commit("setUserEmail", data);
    commit("setUserSecondaryEmails", data);
  },
  logout({ commit }) {
    commit("userLogout");
  },
  updatePassports({ commit }, data) {
    commit("setUserPassports", data);
  },
  updateTmpPassports({ commit }, data) {
    commit("setUserTmpPassports", data);
  },
  updateActivities({ commit }, data) {
    commit("setUserActivity", data);
  },
  updateTmpActivities({ commit }, data) {
    commit("setUserTmpActivity", data);
  },
  async getUserById(data, id) {
    return await apiUser.getUserById(id);
  },
  async getUserContinuousActivities(data, id) {
    return await apiUser.getUserContinuousActivities(id);
  },
  async getUserDurationActivities(data, id) {
    return await apiUser.getUserDurationActivities(id);
  },
  async getDataFromUrl(data, url) {
    return await axios.get(url);
  },
  async editUserActivityTypes(data, {'id': id, 'activities': activities}) {
    return await apiUser.editUserActivityTypes(id, activities);
  },
  async getActivityTypes() {
    return await apiUser.getActivityTypes();
  },
  async getAllEmails() {
    return await apiUser.getAllEmails();
  },
  async addEmail(data, {'id': id, 'newEmail': additionalEmails}) {
    await apiUser.addEmails(id, additionalEmails);
  },
  async editEmail(data, {'id': id, 'primaryEmail': primaryEmail, 'additionalEmail': secondaryEmails}) {
    await apiUser.editEmail(id, primaryEmail, secondaryEmails);
  },
  async editProfile(data, user) {
    return await apiUser.editProfile(user.profile_id, user.firstname, user.lastname, user.middlename, user.nickname, user.primary_email, user.bio, user.date_of_birth, user.gender, user.fitness, user.additional_email, user.passports, user.permission_level, user.activities, user.location);
  },
  async changePassword(data, {'id': id, 'oldPassword': oldPassword, 'newPassword': newPassword, 'confirmPassword': confirmPassword }) {
    return await apiUser.changePassword(id, oldPassword, newPassword, confirmPassword);
  },
  async login(data, {'email': email, 'password':password}) {
      return await apiUser.login(email, password);
  },
  async signUp(data, {'firstName': firstName, 'lastName': lastName, 'middleName': middleName, 'nickName': nickName, 'email': email, 'password': password, 'bio': bio, 'dateOfBirth': dateOfBirth, 'gender': gender, 'fitnessLevel': fitnessLevel}) {
    return await apiUser.signUp(firstName, lastName, middleName, nickName, email, password, bio, dateOfBirth, gender, fitnessLevel);
  },
  async searchForUsers(data, {'searchTerm': searchTerm, 'searchBy': searchBy, 'page': page, 'size': size}) {
    return await apiUser.searchUsers(searchTerm, searchBy, page, size);
  },
  async getIdByEmail(data, {'email': email}) {
    return await apiUser.getIdByEmail(email);
  },
  async isUserFollowingActivitiy(data, {'userId': userId, 'activityId': activityId}) {
    return await apiUser.isUserFollowingActivity(userId, activityId);
  },
  async followActivity(data, {'userId': userId, 'activityId': activityId}) {
    return await apiUser.followActivity(userId, activityId);
  },
  async unfollowActivity(data, {'userId': userId, 'activityId': activityId}) {
    return await apiUser.unfollowActivity(userId, activityId);
  },
  async refreshUserData(data, userId) {
    return await apiUser.refreshUserData(userId);
  },
  async deleteUserAccount(data, {'id': id}) {
    return await apiUser.deleteUserAccount(id);
  },
  async editUserRoleForActivity(data, {'id': id, 'activityId': activityId, 'email': email, 'activityRole': activityRole}) {
    return await apiUser.editUserRoleForActivity(id, activityId, email, activityRole);
  },
  async getUserFeed(data, {'id': id, 'page': page, 'size': size}) {
    return await apiUser.getUserFeed(id, page, size);
  }
};

export default {
  state,
  actions,
  mutations,
  getters,
};
