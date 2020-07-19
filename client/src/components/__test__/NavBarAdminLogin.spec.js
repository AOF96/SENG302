/* eslint-env jest*/
import {createLocalVue, mount} from '@vue/test-utils'
import VueRouter from 'vue-router'
import NavBar from '../modules/NavBar.vue'
import Vuex from 'vuex'
import flushPromises from "flush-promises";


const localVue = createLocalVue();
localVue.use(VueRouter);
const router = new VueRouter();
localVue.use(Vuex);


// describe('NavBar after the login is successful', () => {
//     let getters;
//     let store;
//
//     beforeEach(() => {
//         getters = {
//             user: () => ({
//                 profile_id: 100,
//                 isLogin: true,
//                 permission_level: 1,
//             }),
//             isAdmin: () => ({
//                 isAdmin: true
//             }),
//         };
//         store = new Vuex.Store({
//             getters
//         })
//     });
//
//     it('NavBar should have search, home, admin dashboard, profile and a settings button in the hamburger menu button', () => {
//         const wrapper = mount(NavBar, {store, localVue, router})
//         expect(wrapper.find("#searchButton").exists()).toBe(true)
//         expect(wrapper.find("#homeButton").exists()).toBe(true)
//         expect(wrapper.find("#adminDashboardButton").exists()).toBe(true)
//         expect(wrapper.find("#profileButton").exists()).toBe(true)
//         expect(wrapper.find("#settingsButton").exists()).toBe(true)
//         expect(wrapper.find("#headerNavToggle").exists()).toBe(true)
//         expect(wrapper.find(".logoutButton").exists()).toBe(false)
//
//
//         it('Clicking on the Dashboard button the user gets redirected to the Search page', async () => {
//             const wrapper = mount(NavBar, {store, localVue, router})
//             wrapper.find("#navBarSearchBtn").trigger('click')
//             await flushPromises();
//             expect(window.location.href).toBe('http://localhost/#/search')
//
//         })
//
//         it('Clicking on the Dashboard button the user gets redirected to Admin dashboard', async () => {
//             const wrapper = mount(NavBar, {store, localVue, router})
//             wrapper.find("#dashboardButton").trigger('click')
//             await flushPromises();
//             expect(window.location.href).toBe('http://localhost/#/settings/admin_dashboard')
//         })
//     })
// });

/**
 *  Below is the scenario when the Global Admin logs with permission level 2 logs in
 */
describe('NavBar after global admin login is successful', () => {
    let getters;
    let store;

    beforeEach(() => {
        getters = {
            user: () => ({
                profile_id: 100,
                isLogin: true,
                permission_level: 2,
            }),
            isAdmin: () => ({
                isAdmin: true
            }),
        };
        store = new Vuex.Store({
            getters
        })
    });

    it('NavBar should not have any profile button for global admin', () => {
        const wrapper = mount(NavBar, {store, localVue, router})
        expect(wrapper.find("#profileButton").exists()).toBe(false)
    });

    it('NavBar should not have any home button for global admin as that re-directs to the profile page for now', () => {
        const wrapper = mount(NavBar, {store, localVue, router});
        expect(wrapper.find("#homeButton").exists()).toBe(false)
    });
});