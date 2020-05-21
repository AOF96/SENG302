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

Vue.use(VueRouter);

const baseUrl = process.env.VUE_APP_BASE_URL;

const routes = [
    {
        path: baseUrl + 'profile/:profileId',
        component: Profile
    },
    {
        path: baseUrl + 'signup',
        component: Signup
    },
    {
        path: baseUrl + 'login',
        component: Login
    },
    {
        path: baseUrl + 'logout',
    },
    {
        path: baseUrl + 'settings/profile/:profileId',
        component: UserProfileSettings
    },
    {
        path: baseUrl + 'settings/password/:profileId',
        component: UserPasswordSettings
    },
    {
        path: baseUrl + 'settings/email/:profileId',
        component: UserEmailSettings
    },
    {
        path: baseUrl + 'settings/passport_countries/:profileId',
        component: UserPassportCountriesSettings
    },
    {
        path: baseUrl + 'settings/activities/:profileId',
        component: UserActivitySettings
    },
    {
        path: baseUrl + "settings/admin_dashboard",
        component: AdminDashboard,
    },
    {
        path: baseUrl + "profile*",
        component: Profile,
    },
    {
        path: "*",
        redirect: baseUrl + "profile",
    },
    {
        path: baseUrl + 'activity/:activityId',
        component: Activity
    },
    {
        path: baseUrl + 'activity_settings/:profileId',
        component: ActivitySettings
    },
    {
        path: baseUrl + 'activity_editing/:activityId',
        component: EditActivity
    }
];



const router = new VueRouter({
    routes,
    mode: 'history'
});

let firstLoad = true;

router.beforeEach((to, from, next) => {
  const isAdmin = store ? store.getters.isAdmin : null;
  const isLoggedIn = store ? store.getters.isLoggedIn : null;
  const isAuthPath = to.path === "/signup" || to.path === "/login";
  console.log("start routing to " + to.path);
  console.log(baseUrl);

  if (firstLoad === true && localStorage.getItem("s_id") !== null) {
    firstLoad = false;
    apiUser.getUserByToken().then(
      (response) => {
        console.log("Token is matched");
        const responseData = response.data;
        store._actions.updateUserProfile[0](responseData);
        isAuthPath ? next("/profile") : next();
      },
      (error) => {
        console.log("Not logged in: " + error);
        next();
      }
    );
  } else {
    if (to.path === "/settings/admin_dashboard" && isAdmin && store.getters.user.permission_level == 2 && isLoggedIn) {
      console.log("login as an admin user");
      next();
    } else if (isAuthPath) {
      isLoggedIn ? next("/profile") : next();
    } else if (to.path !== "/logout" && isLoggedIn) {
      next();
    } else {
      localStorage.removeItem("s_id");
      next("/login");
    }
  }
});

export default router;
