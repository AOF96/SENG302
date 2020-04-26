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
import AdminDashboard from "./components/Settings/AdminDashboard";


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
        path: '/settings/admin_dashboard',
        component: AdminDashboard
    },
];



const router = new VueRouter({
    routes,
    mode: 'history'
});

router.beforeEach((to, from, next) => {
    console.log('start routering to.path=' + to.path)
    console.log('user.isLogin=' + store.getters.user.isLogin)
    console.log('getters=' + store.getters)
    console.log('admin user.isLogin=' + store.getters.adminUser.isLogin)

    if(to.path == "/settings/admin_dashboard" && store.getters.adminUser.isLogin) {
        next();
    }
    else if (to.path == "/signup" || to.path == "/login") {
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
