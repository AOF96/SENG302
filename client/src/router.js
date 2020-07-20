import Vue from 'vue'
import VueRouter from 'vue-router'
import Profile from './components/profile/Profile.vue'
import Signup from './components/Signup.vue'
import Login from './components/Login.vue'
import UserProfileSettings from './components/profile/settings/ProfileInfoSettings'
import UserPasswordSettings from './components/profile/settings/ProfilePasswordSettings'
import UserEmailSettings from './components/profile/settings/ProfileEmailSettings'
import UserPassportCountriesSettings from './components/profile/settings/ProfilePassportCountriesSettings'
import UserActivitySettings from "./components/profile/settings/ProfileActivityTypeSettings";
import Activity from './components/activity/Activity.vue'
import store from './store/index.js';
import ActivitySettings from "./components/activity/settings/ActivitySettings";
import EditActivity from "./components/EditActivity";
import { apiUser } from "./api";
import AdminDashboard from "./components/AdminDashboard";
import Search from "./components/Search";

Vue.use(VueRouter);

const baseUrl = process.env.BASE_URL;

const routes = [
    {
        path: '/profile/:profileId',
        component: Profile
    },
    {
        path: '/signup',
        component: Signup
    },
    {
        path: '/login',
        component: Login
    },
    {
        path: '/logout',
    },
    {
        path: '/settings/profile/:profileId',
        component: UserProfileSettings
    },
    {
        path: '/settings/password/:profileId',
        component: UserPasswordSettings
    },
    {
        path: '/settings/email/:profileId',
        component: UserEmailSettings
    },
    {
        path: '/settings/passport_countries/:profileId',
        component: UserPassportCountriesSettings
    },
    {
        path: '/settings/activities/:profileId',
        component: UserActivitySettings
    },
    {
        path: "/settings/admin_dashboard",
        component: AdminDashboard,
    },
    {
        path: "/profile*",
        component: Profile,
    },
    {
        path: "*",
        redirect: "/profile",
    },
    {
        path: '/activity/:activityId',
        component: Activity
    },
    {
        path: '/activity_settings/:profileId',
        component: ActivitySettings
    },
    {
        path: '/activity_editing/:activityId',
        component: EditActivity
    },
    {
        path: '/search/:query',
        component: Search
    },
    {
        path: '/search',
        component: Search
    }
];



const router = new VueRouter({
    routes,
    base: baseUrl,
    mode: 'history'
});

let firstLoad = true;

router.beforeEach((to, from, next) => {
  const isAdmin = store ? store.getters.isAdmin : null;
  const isLoggedIn = store ? store.getters.isLoggedIn : null;
  const isAuthPath = to.path === "/signup" || to.path === "/login";

  if (firstLoad === true) {
    firstLoad = false;
    apiUser.getUserByToken().then(
      (response) => {
        const responseData = response.data;
        store._actions.updateUserProfile[0](responseData);
        isAuthPath ? next("/profile") : next();
      }).catch(
      (error) => {
        console.log("Not logged in: " + error);
        next();
      });
  } else {
    if (to.path === "/settings/admin_dashboard" && isAdmin && store.getters.user.permission_level === 2 && isLoggedIn) {
      updatePageHistory(to, from);
      next();
    } else if (isAuthPath) {
      store._actions.resetPageHistory[0]();
      if(store.getters.user.permission_level === 2){
        isLoggedIn ? next("/settings/admin_dashboard") : next();
      }else{
        isLoggedIn ? next("/profile") : next();
      }
    } else if (to.path !== "/logout" && isLoggedIn) {
      updatePageHistory(to, from);
      if((to.path == "/profile" || to.path == "/profile/"+store.getters.user.profileId) && store.getters.user.permission_level === 2){
        next("/settings/admin_dashboard");
      }else{
        next();
      }
    } else {
      store._actions.resetPageHistory[0]();
      next("/login");
    }
  }
});

function updatePageHistory(to, from) {
  if (store.getters.getPreviousPage === to.path) {
    store._actions.previousPage[0](from.path);
  } else if (store.getters.getNextPage === to.path) {
    store._actions.nextPage[0](from.path);
  } else if (to.path === "/profile" || from.path === "/profile") {
    store._actions.resetPageHistory[0]();
  } else if (to.path !== from.path && from.path !== "/login" && from.path !== "/signup") {
    store._actions.clearNextHistory[0]();
    store._actions.addPreviousPage[0](from.path);
  }
}

export default router;
