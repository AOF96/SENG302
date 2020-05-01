import { apiUser } from "../api";

const userInterface = {
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
};

//only for normal user with permission level 0
const initialUserState = {
  user: userInterface,
};

//only for default admin with permission level 2
const initialAdminUserState = {
  adminUser: {
    email: null,
    password: null,
    permission_level: 0,
    isLogin: false
  },
};

const state = {
  adminUser: {
    email: null,
    password: null,
    permission_level: 0,
    isLogin: false
  },
  ...initialUserState,
};

const getters = {
  user(state) {
    return state.user;
  },
  adminUser(state) {
    return state.adminUser;
  },

  searchedUser(state) {
    return state.user;
  },
  isLoggedIn(state) {
    return state.user.isLogin;
  },
};

const mutations = {
  resetUser(state) {
    state.user = initialUserState.user;
    state.adminUser = initialAdminUserState.adminUser;
  },
  setUser(state, data) {
    state.user = data;
  },
  setUserFirstName(state, data) {
    if(data.firstname !== ""){
      state.user.firstname = data.firstname;
    }
  },
  setUserLastName(state, data) {
    if(data.lastname !== ""){
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
    if(data.gender !== ""){
      state.user.gender = data.gender;
    }
  },
  setUserEmail(state, data) {
    if(data.primary_email !== ""){
      state.user.primary_email = data.primary_email;
    }
  },
  setUserID(state, data) {
    if(data.profile_id !== ""){
      state.user.profile_id = data.profile_id;
    }
  },
  setUserSecondaryEmails(state, data) {
    state.user.additional_email = data.additional_email;
  },
  setUserBirthday(state, data) {
    if(data.birthday !== ""){
      state.user.date_of_birth = data.date_of_birth;
    }
  },
  setUserBio(state, data) {
    if(data.bio !== ""){
      state.user.bio = data.bio;
    }
  },
  setUserPassports(state, data) {
    state.user.passports = data.passports;
  },

  setUserTmpPassports(state, data) {
    state.user.tmp_passports = data.tmp_passports;
  },

  setUserFitnessLevel(state, data) {
    state.user.fitness = data.fitness;
  },
  setUserActivity(state, data) {
    state.user.activities = data.activities;
  },
  setUserIsLogin(state, data) {
    if(data.isLogin !== ""){
      state.user.isLogin = data.isLogin
    }

  setUserPermissionLevel(state, data) {
    state.user.permission_level = data.permission_level;
  },

  adminUserLogin() {
    state.user.isLogin = true;
  },

  adminUserLogout() {
    state.user.isLogin = false;
    apiUser.logout();
  },

  userLogin() {
    state.user.isLogin = true;
  },

  userLogout() {
    state.user.isLogin = false;
    apiUser.logout();
  },

  setUserPassword(state, data) {
    if(data.password !== ""){
      state.user.password = data.password
    }
  },
  setAdminUser(state, data) {
    state.adminUser.email = data.email;
    state.adminUser.password = data.password;
    state.adminUser.permission_level = data.permission_level;
  },
  resetAdminUser(state) {
    state.adminUser.email = null;
    state.adminUser.password = null;
    state.adminUser.permission_level = null;
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
    commit("setUserFitnessLevel", data);
    commit("setUserEmail", data);
    commit("setUserSecondaryEmails", data);
    commit("setUserID", data);
    commit("setUserPermissionLevel", data);
    commit("userLogin");
    commit('setUserActivity', data);
  },
  updateUserEmail({ commit }, data) {
    commit("setUserEmail", data);
    commit("setUserSecondaryEmails", data);
  },
  logout({ commit }) {
    commit("userLogout");
    commit("resetUser");
  },
  updatePassports({ commit }, data) {
    commit("setUserPassports", data);
  },
  updateTmpPassports({ commit }, data) {
    commit("setUserTmpPassports", data);
  },
  resetUser({ commit }) {
    commit("resetUser");
  },
  loginAdminUser({ commit }, data) {
    commit("setAdminUser", data);
    commit("adminUserLogin");
  },
  logoutAdminUser({ commit }) {
    commit("resetAdminUser");
    commit("adminUserLogout");
  },
  updateActivities({commit}, data){
    commit('setUserActivity', data)
  },
};

export default {
  state,
  actions,
  mutations,
  getters,
};

