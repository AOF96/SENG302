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

Vue.use(VueRouter);

const routes = [
    {
        path: '/profile',
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
        path: '/settings/profile',
        component: UserProfileSettings
    },
    {
        path: '/settings/password',
        component: UserPasswordSettings
    },
    {
        path: '/settings/email',
        component: UserEmailSettings
    },
    {
        path: '/settings/passport_countries',
        component: UserPassportCountriesSettings
    },
    {
        path: '/settings/activities',
        component: UserActivitySettings
    },
    {
        path: '/activity/:activityId',
        component: Activity
    },
    {
        path: '/activity_settings',
        component: ActivitySettings
    },
    {
        path: '/activity_editing',
        component: EditActivity
    }
];



const router = new VueRouter({
    routes,
    mode: 'history'
});

router.beforeEach((to, from, next) => {
    console.log('start routering to.path=' + to.path)
    console.log('user.isLogin=' + store.getters.user.isLogin)
    if (to.path == "/signup" || to.path == "/login") {
        if (store.getters.user.isLogin) {
            next('/profile')
        } else {
            next()
        }
    } else if (to.path == '/logout') {
        next('/login')
    } else {
        if(store.getters.user.isLogin){
            next()
        } else {
            next('/login')
        }
    }
})

export default router
