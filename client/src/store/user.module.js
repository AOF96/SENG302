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

const initialUserState = {
  user: userInterface,
};

const initialSearchedState = {
  searchedUser: userInterface,
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

function isAdmin(state) {
  return state.user.permission_level > 0;
}

const mutations = {
  resetUser(state) {
    state.user = initialUserState.user;
    state.searchedUser = initialSearchedState.searchedUser;
  },
  setUser(state, data) {
    if (!isAdmin(state)) {
      state.user = data;
    } else {
      state.searchedUser = data;
    }
  },
  setUserFirstName(state, data) {
    if (data.firstname != "") {
      if (!isAdmin(state)) {
        state.user.firstname = data.firstname;
      } else {
        state.searchedUser.firstname = data.firstname;
      }
    }
  },
  setUserLastName(state, data) {
    if (data.lastname != "") {
      if (!isAdmin(state)) {
        state.user.lastname = data.lastname;
      } else {
        state.searchedUser.lastname = data.lastname;
      }
    }
  },
  setUserMiddleName(state, data) {
    if (!isAdmin(state)) {
      state.user.middlename = data.middlename;
    } else {
      state.searchedUser.middlename = data.middlename;
    }
  },
  setUserNickName(state, data) {
    if (!isAdmin(state)) {
      state.user.nickname = data.nickname;
    } else {
      state.searchedUser.nickname = data.nickname;
    }
  },
  setUserGender(state, data) {
    if (data.gender != "") {
      if (!isAdmin(state)) {
        state.user.gender = data.gender;
      } else {
        state.searchedUser.gender = data.gender;
      }
    }
  },
  setUserEmail(state, data) {
    if (data.primary_email != "") {
      if (!isAdmin(state)) {
        state.user.primary_email = data.primary_email;
      } else {
        state.searchedUser.primary_email = data.primary_email;
      }
    }
  },
  setUserID(state, data) {
    if (data.profile_id != "") {
      if (!isAdmin(state)) {
        state.user.profile_id = data.profile_id;
      } else {
        state.searchedUser.profile_id = data.profile_id;
      }
    }
  },
  setUserSecondaryEmails(state, data) {
    if (!isAdmin(state)) {
      state.user.additional_email = data.additional_email;
    } else {
      state.searchedUser.additional_email = data.additional_email;
    }
  },
  setUserBirthday(state, data) {
    if (data.birthday != "") {
      if (!isAdmin(state)) {
        state.user.date_of_birth = data.date_of_birth;
      } else {
        state.searchedUser.date_of_birth = data.date_of_birth;
      }
    }
  },
  setUserBio(state, data) {
    if (data.bio != "") {
      if (!isAdmin(state)) {
        state.user.bio = data.bio;
      } else {
        state.searchedUser.bio = data.bio;
      }
    }
  },
  setUserPassports(state, data) {
    if (!isAdmin(state)) {
      state.user.passports = data.passports;
    } else {
      state.searchedUser.passports = data.passports;
    }
  },

  setUserTmpPassports(state, data) {
    if (!isAdmin(state)) {
      state.user.tmp_passports = data.tmp_passports;
    } else {
      state.searchedUser.tmp_passports = data.tmp_passports;
    }
  },

  setUserFitnessLevel(state, data) {
    if (!isAdmin(state)) {
      state.user.fitness = data.fitness;
    } else {
      state.searchedUser.fitness = data.fitness;
    }
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
};

export default {
  state,
  actions,
  mutations,
  getters,
};
