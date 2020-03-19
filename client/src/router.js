import Vue from 'vue'
import VueRouter from 'vue-router'
import Profile from '@/components/Profile.vue'
import Signup from '@/components/Signup.vue'
import Login from '@/components/Login.vue'
import UserProfileSettings from '@/components/Settings/UserProfileSettings'
import UserPasswordSettings from '@/components/Settings/UserPasswordSettings'
import UserEmailSettings from '@/components/Settings/UserEmailSettings'
import UserPassportCountriesSettings from '@/components/Settings/UserPassportCountriesSettings'
//import store from '@/store/index.js';


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
//
// router.beforeEach((to, from, next) => {
//     console.log('start routering to.path=' + to.path)
//     console.log('user.isLogin=' + store.getters.user.isLogin)
//     if (to.path == "/Signup" || to.path == "/login") {
//         if (store.getters.user.isLogin) {
//             next('/Profile')
//         } else {
//             next()
//         }
//     } else if (to.path == '/logout') {
//         next('/login')
//     } else {
//         if(store.getters.user.isLogin){
//             next()
//         } else {
//             next('/login')
//         }
//     }
// })

export default router
