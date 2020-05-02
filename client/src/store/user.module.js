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
const initialUserState = {user: userInterface};


const state = {
  ...initialUserState
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
};

const mutations = {
  resetUser(state) {
    state.user = initialUserState.user;
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
};

export default {
  state,
  actions,
  mutations,
  getters,
};

