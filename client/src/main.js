import Vue from 'vue'
import App from './App'
import router from './router'
import store from "./store"


Vue.config.productionTip = false

import VueLogger from 'vuejs-logger';
import vuetify from './plugins/vuetify';

const options = {
  isEnabled: true,
  logLevel : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : false,
  separator: '|',
  showConsoleColors: true
};

Vue.use(VueLogger, options);

new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount('#app')