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
    isLogin: true
  }
}

const getters = {
  user(state) {
    return state.user
  }
}

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
  }
}

const actions = {
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
  }
}

export default {
  state,
  actions,
  mutations,
  getters
}
