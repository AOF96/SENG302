import Vue from "vue"
import Vuex from "vuex"

import user from "./user.module"
import activity from "./activity.module"

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    user,
    activity
  }
})