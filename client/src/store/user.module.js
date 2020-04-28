import {apiUser} from "../api";

const state = {
  user: {
    firstname: null,
    lastname: null,
    middlename: null,
    nickname: null,
    gender: null,
    primary_email: null,
    additional_email: [],
    date_of_birth: null,
    bio: null,
    isLogin: false,
    fitness: null,
    profile_id: null,
    password: null,
    passports: [],
    tmp_passports: [],
    permission_level: null,
    activities: [],
    tmp_activities: [],
    cont_activities: [],
    dur_activities: []
  }
};

const getters = {
  user(state) {
    return state.user
  },
  isLoggedIn(state) {
    return state.user.isLogin;
  }

};

const mutations = {
  setUser(state, data) {
    state.user = data
  },
  setUserFirstName(state, data) {
    if(data.firstname != ""){
      state.user.firstname = data.firstname;
    }
  },
  setUserLastName(state, data) {
    if(data.lastname != ""){
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
    if(data.gender != ""){
      state.user.gender = data.gender;
    }
  },
  setUserEmail(state, data) {
    if(data.primary_email != ""){
      state.user.primary_email = data.primary_email;
    }
  },
  setUserID(state, data) {
    if(data.profile_id != ""){
      state.user.profile_id = data.profile_id;
    }
  },
  setUserSecondaryEmails(state, data) {
    state.user.additional_email = data.additional_email;
  },
  setUserBirthday(state, data) {
    if(data.birthday != ""){
      state.user.date_of_birth = data.date_of_birth;
    }
  },
  setUserBio(state, data) {
    if(data.bio != ""){
      state.user.bio = data.bio;
    }
  },
  setUserPassports(state, data) {
    state.user.passports = data.passports;
  },
  setUserTmpPassports(state, data) {
    state.user.tmp_passports = data.tmp_passports;
  },
  setUserContinuousActivities(state, data) {
    let result = [];
    for (let i = 0; i < data.length; i++) {
      console.log(data[i]);
      result.push(data[i].name + " - " + data[i].description)
    }
    state.user.cont_activities = result;
  },
  setUserDurationActivities(state, data) {
    let result = [];
    for (let i = 0; i < data.length; i++) {
      console.log(data[i]);
      result.push(data[i].name + " - " + data[i].description)
    }
    state.user.dur_activities = result;
  },
  setUserFitnessLevel(state, data) {
    state.user.fitness = data.fitness;
  },
  setUserPermissionLevel(state, data) {
    state.user.permission_level = data.permission_level;
  },
  setUserActivity(state) {
    state.user.activities = state.user.tmp_activities.slice();
  },
  setUserTmpActivity(state, data) {
    state.user.tmp_activities = data.tmp_activities;
  },
  setUserIsLogin(state, data) {
    if(data.isLogin != ""){
      state.user.isLogin = data.isLogin
    }
  },
  userLogin() {
    state.user.isLogin = true;
  },
  userLogout() {
    state.user.isLogin = false;
    apiUser.logout();
  },
  setUserPassword(state, data) {
    if(data.password != ""){
      state.user.password = data.password
    }
  }
}

const actions = {
  createUserProfile({ commit }, data) {
    commit('setUserFirstName', data);
    commit('setUserLastName', data);
    commit('setUserMiddleName', data);
    commit('setUserNickName', data);
    commit('setUserEmail', data);
    commit('setUserGender', data);
    commit('setUserBirthday', data);
    commit('setUserBio', data);
    commit('setUserPassword', data);
    commit('setUserFitnessLevel', data);
    commit('setUserSecondaryEmails', data);
    commit('setUserID', data);
    commit('userLogin');
  },
  updateUserProfile({ commit }, data) {
    commit('setUserFirstName', data);
    commit('setUserLastName', data);
    commit('setUserMiddleName', data);
    commit('setUserNickName', data);
    commit('setUserGender', data);
    commit('setUserBirthday', data);
    commit('setUserBio', data);
    commit('setUserPassports', data);
    commit('setUserActivity', data);
    commit('setUserFitnessLevel', data);
    commit('setUserEmail', data);
    commit('setUserSecondaryEmails', data);
    commit('setUserID', data);
    commit('setUserPermissionLevel', data);
    commit('userLogin');
  },
  updateUserContinuousActivities({ commit }, data) {
    commit('setUserContinuousActivities', data);
  },
  updateUserDurationActivities({ commit }, data) {
    commit('setUserDurationActivities', data);
  },
  updateUserEmail({ commit }, data) {
    commit('setUserEmail', data);
    commit('setUserSecondaryEmails', data);
  },
  logout({ commit }) {
    commit('userLogout')
  },
  updatePassports({commit}, data){
    commit('setUserPassports', data)
  },
  updateTmpPassports({commit}, data){
    commit('setUserTmpPassports', data)
  },
  updateActivities({commit}, data){
    commit('setUserActivity', data)
  },
  updateTmpActivities({commit}, data){
    commit('setUserTmpActivity', data)
  },
  resetUser() {
    state.user.firstname = null;
    state.user.lastname = null;
    state.user.middlename = null;
    state.user.nickname = null;
    state.user.gender = null;
    state.user.primary_email = null;
    state.user.additional_email = [];
    state.user.date_of_birth = null;
    state.user.bio = null;
    state.user.isLogin = false;
    state.user.fitness = null;
    state.user.profile_id = null;
    state.user.password = null;
    state.user.passports = [];
    state.user.tmp_passports = [];
    state.user.permission_level = null;
    state.user.activities =  [];
    state.user.tmp_activities = [];
    state.user.cont_activities = [];
    state.user.dur_activities = [];
  }
};

export default {
  state,
  actions,
  mutations,
  getters
}
