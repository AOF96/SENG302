const state = {
  user: {
    firstName: null,
    lastName: null,
    middleName: null,
    nickName: null,
    gender: null,
    email: null,
    secondaryEmails: [],
    birthday: null,
    bio: null,
    isLogin: true,
    fitnesslevel: null,
    profile_id: null,
    password: null,
    passports: []
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
      state.user.firstName = data.firstname;
    }
  },
  setUserLastName(state, data) {
    if(data.lastname != ""){
      state.user.lastName = data.lastName;
    }
  },
  setUserMiddleName(state, data) {
    state.user.middleName = data.middlename;
  },
  setUserNickName(state, data) {
    state.user.nickName = data.nickname;
  },
  setUserGender(state, data) {
    if(data.gender != ""){
      state.user.gender = data.gender;
    }
  },
  setUserEmail(state, data) {
    if(data.primary_email != ""){
      state.user.email = data.primary_email;
    }
  },
  setUserSecondaryEmails(state, data) {
    state.user.secondaryEmails = data.additional_email;
  },
  setUserBirthday(state, data) {
    if(data.birthday != ""){
      state.user.birthday = data.date_of_birth;
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

  setUserFitnessLevel(state, data) {
    state.user.fitnesslevel = data.fitnessLevel;
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
  },
  setUserPassword(state, data) {
    if(data.password != ""){
      state.user.password = data.password
    }
  }

}

const actions = {
  createUserProfile({ commit }, data) {
    commit('setUserFirstName', data)
    commit('setUserLastName', data)
    commit('setUserMiddleName', data)
    commit('setUserNickName', data)
    commit('setUserEmail', data)
    commit('setUserGender', data)
    commit('setUserBirthday', data)
    commit('setUserBio', data)
    commit('setUserPassword', data)
    commit('userLogin')
    alert("Profile info updated.")
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
    commit('setUserFitnessLevel', data);
    commit('setUserEmail', data);
    commit('setUserSecondaryEmails', data);
    commit('userLogin');

    alert("Profile info updated.")
  },
  updateUserEmail({ commit }, data) {
    commit('setUserEmail', data)
    commit('setUserSecondaryEmails', data)
    alert("Email updated.")
  },
  logout({ commit }) {
    console.log('logged out')
    commit('userLogout')
  }

};

export default {
  state,
  actions,
  mutations,
  getters
}
