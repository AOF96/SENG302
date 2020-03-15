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
    password: null
  }
}

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
    if(data.firstName != ""){
      state.user.firstName = data.firstName
    }
  },
  setUserLastName(state, data) {
    if(data.lastName != ""){
      state.user.lastName = data.lastName
    }
  },
  setUserMiddleName(state, data) {
    state.user.middleName = data.middleName
  },
  setUserNickName(state, data) {
    state.user.nickName = data.nickName
  },
  setUserGender(state, data) {
    if(data.gender != ""){
      state.user.gender = data.gender
    }
  },
  setUserEmail(state, data) {
    if(data.email != ""){
      state.user.email = data.email
    }
  },
  setUserSecondaryEmails(state, data) {
    state.user.secondaryEmails = data.secondaryEmails
  },
  setUserBirthday(state, data) {
    if(data.birthday != ""){
      state.user.birthday = data.birthday
    }
  },
  setUserBio(state, data) {
    if(data.bio != ""){
      state.user.bio = data.bio
    }
  },
  setUserIsLogin(state, data) {
    if(data.isLogin != ""){
      state.user.isLogin = data.isLogin
    }
  },
  userLogout() {
    console.log(state.user);
    state.user.isLogin = false;
    console.log(state.user);
  },

  setUserPassword(state, data) {
    if(data.password != ""){
      state.user.password = data.password
    }
  },
  login(state) {
    state.user.isLogin = true
  }

};






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
    commit('login')
    alert("Profile info updated.");
  },
  updateUserProfile({ commit }, data) {
    commit('setUserFirstName', data)
    commit('setUserLastName', data)
    commit('setUserMiddleName', data)
    commit('setUserNickName', data)
    commit('setUserGender', data)
    commit('setUserBirthday', data)
    commit('setUserBio', data)
    alert("Profile info updated.");
  },
  updateUserEmail({ commit }, data) {
    commit('setUserEmail', data)
    commit('setUserSecondaryEmails', data)
    alert("Email updated.");
  },
  logout({commit, data}) {
    commit('userLogout', data);
  }
};

export default {
  state,
  actions,
  mutations,
  getters
}
