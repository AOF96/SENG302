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
const initialSearchedState = {
  adminUser: {
    email: null,
    password: null,
    permission_level: 0,
  },
};

const state = {
  ...initialUserState,
  ...initialSearchedState,
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
};

const mutations = {
  resetUser(state) {
    state.user = initialUserState.user;
    state.searchedUser = initialSearchedState.searchedUser;
  },
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
  setUserPassports(state, data) {
    state.user.passports = data.passports;
  },

  setUserTmpPassports(state, data) {
    state.user.tmp_passports = data.tmp_passports;
  },

  setUserFitnessLevel(state, data) {
    state.user.fitness = data.fitness;
  },

  setUserPermissionLevel(state, data) {
    state.user.permission_level = data.permission_level;
  },

  setUserIsLogin(state, data) {
    if (data.isLogin != "") {
      state.user.isLogin = data.isLogin;
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
    if (data.password != "") {
      state.user.password = data.password;
    }
  },
  setAdminUser(state, data) {
    state.admin.user.email = data.email;
    state.admin.user.password = data.email;
    state.admin.user.permission_level = data.permission_level;
  },
  resetAdminUser(state) {
    state.admin.user.email = null;
    state.admin.user.password = null;
    state.admin.user.permission_level = null;
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
  resetUser({ commit }) {
    commit("resetUser");
  },
  setAdminUser({ commit }) {
    commit("setAdminUser");
  },
  resetAdminUser({ commit }) {
    commit("resetAdminUser");
  },
};

export default {
  state,
  actions,
  mutations,
  getters,
};
