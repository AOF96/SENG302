import Vue from "vue";
import VueRouter from "vue-router";
import Profile from "@/components/Profile.vue";
import Signup from "@/components/Signup.vue";
import Login from "@/components/Login.vue";
import UserProfileSettings from "@/components/Settings/UserProfileSettings";
import UserPasswordSettings from "@/components/Settings/UserPasswordSettings";
import UserEmailSettings from "@/components/Settings/UserEmailSettings";
import UserPassportCountriesSettings from "@/components/Settings/UserPassportCountriesSettings";
import store from "@/store/index.js";
import { apiUser } from "./api";
import AdminDashboard from "./components/Settings/AdminDashboard";
//import { mapGetters, mapActions} from 'vuex';

Vue.use(VueRouter);

var firstLoad = true;

const routes = [
  {
    path: "/profile",
    component: Profile,
  },
  {
    path: "/signup",
    component: Signup,
  },
  {
    path: "/login",
    component: Login,
  },
  {
    path: "/logout",
  },
  {
    path: "/settings/profile",
    component: UserProfileSettings,
  },
  {
    path: "/settings/password",
    component: UserPasswordSettings,
  },
  {
    path: "/settings/email",
    component: UserEmailSettings,
  },
  {
    path: "/settings/passport_countries",
    component: UserPassportCountriesSettings,
  },
  {
    path: "/settings/admin_dashboard",
    component: AdminDashboard,
  },
];

const router = new VueRouter({
  routes,
  mode: "history",
});


router.beforeEach((to, from, next) => {
  // Make sure that the next function is called exactly once in any given pass through the navigation guard.
  // It can appear more than once, but only if the logical paths have no overlap,
  // otherwise the hook will never be resolved or produce errors.
  // https://router.vuejs.org/guide/advanced/navigation-guards.html#global-before-guards
  // console.log("start routering to.path=" + to.path);
  // console.log("admin user.isLogin=" + store.getters.adminUser.isLogin);
  // console.log("Is user logged in=" + store.getters.user.isLogin);

  if (firstLoad === true) {
    firstLoad = false;
    apiUser.getUserByToken().then(
        response => {
          const responseData = response.data;
          if (responseData.permission_level === 2) {
            store.adminUser.adminUserLogin(responseData);
          } else {
            store._actions.updateUserProfile[0](responseData);
          }
          if (to.path === "/signup" || to.path === "/login") {
            next('/profile');
          } else {
            next();
          }
        },
        error => {
          if (!(to.path === "/signup" || to.path === "/login")) {
            next('/login');
          } else {
            next();
          }
          if (error) {
            console.log("Not logged in!");
          }
        }
    );
  } else {
    if (
        to.path === "/settings/admin_dashboard" &&
        store.getters.adminUser.isLogin
    ) {
      console.log("login as an admin user");
      next();
    } else if (to.path === "/signup" || to.path === "/login") {
      if (store.getters.user.isLogin) {
        next("/profile");
      } else {
        next();
      }
    } else if (to.path === "/logout") {
      next("/login");
    } else if (store.getters.user.isLogin) {
      next();
    } else {
      localStorage.removeItem("s_id");
      next("/login");
    }
  }
});

export default router;
