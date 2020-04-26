import Vue from 'vue'
import VueRouter from 'vue-router'
import Profile from '@/components/Profile.vue'
import Signup from '@/components/Signup.vue'
import Login from '@/components/Login.vue'
import UserProfileSettings from '@/components/Settings/UserProfileSettings'
import UserPasswordSettings from '@/components/Settings/UserPasswordSettings'
import UserEmailSettings from '@/components/Settings/UserEmailSettings'
import UserPassportCountriesSettings from '@/components/Settings/UserPassportCountriesSettings'
import store from '@/store/index.js';
import { apiUser } from "./api";

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
];



const router = new VueRouter({
    routes,
    mode: 'history'
});

router.beforeEach((to, from, next) => {
    //localStorage.removeItem('userLoggedIn')
    //localStorage.removeItem('thisUser')
    console.log('start routering to.path=' + to.path)
    console.log('user.isLogin=' + store.getters.user.isLogin)
    console.log('user.isLogin=' + localStorage.getItem('userLoggedIn'))
    console.log('user' + localStorage.getItem('thisUser'))
    if (localStorage.getItem('userLoggedIn') === null) {
        if (to.path == "/signup" || to.path == "/login") {
            if (localStorage.getItem('userLoggedIn') === 'true') {
                next('/profile')
            } else {
                next()
            }
        }
    } else if (localStorage.getItem('userLoggedIn') === 'true') {
        apiUser.getUserSessionToken(localStorage.getItem('thisUser')).then((response) => {
            const responseData = response.data;
            if (localStorage.getItem('userLoggedIn') === 'true' && responseData.indexOf(localStorage.getItem('s_id')) > 0) {
                if (to.path == '/logout') {
                    this.resetUser();
                    next('/login')
                } else {
                    next()
                }
            } else {
                localStorage.removeItem('userLoggedIn')
                localStorage.removeItem('thisUser')
                next('/login')
            }
        }, (error) => {
            console.log(error.response.data)
            console.log(error.response.status)
        });
    } else {
        next('/login')
    }
})

export default router

