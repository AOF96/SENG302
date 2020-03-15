import Vue from 'vue'
import VueRouter from 'vue-router'
import Profile from '@/components/Profile.vue'
import Signup from '@/components/Signup.vue'
import Login from '@/components/Login.vue'
import UserProfileSettings from '@/components/Settings/UserProfileSettings'
import UserPasswordSettings from '@/components/Settings/UserPasswordSettings'
import UserEmailSettings from '@/components/Settings/UserEmailSettings'
import UserPassportCountriesSettings from '@/components/Settings/UserPassportCountriesSettings'
import {userInfo} from './globals';
import store from '@/store/index.js';
//import {userInfo} from './globals';


Vue.use(VueRouter);

const routes = [
    {
        path: '/Profile',
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
];



const router = new VueRouter({
    routes,
    mode: 'history'
});

router.beforeEach((to, from, next) => {
    if (to.path == "/Signup" || to.path == "/login") {
        if (store.getters.user.isLogin) {
            next('/Profile')
        } else {
            next()
        }
    } else {
        console.log(store.getters);
        console.log(store.getters.user.isLogin);
        if(store.getters.user.isLogin){
            next()
        } else {
            next('/login')
        }
    }
});

export default router
