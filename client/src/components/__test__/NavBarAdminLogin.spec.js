/* eslint-env jest*/
import {createLocalVue, mount, shallowMount} from '@vue/test-utils'
import VueRouter from 'vue-router'
import NavBar from '../modules/NavBar.vue'
import Vuex from 'vuex'

const localVue = createLocalVue()
localVue.use(VueRouter)
const router = new VueRouter()
localVue.use(Vuex)

describe('NavBar after the login is successful', () => {
    let getters
    let store

    beforeEach(() => {
        getters = {
            user: () => ({
                profile_id: 100,
                isLogin: true,
                permission_level: 1,
            }),
            isAdmin: () => ({
                isAdmin: true
            }),
        }
        store = new Vuex.Store({
            getters
        })
    })

    it('NavBar should have search button, myaccount button and Dashboard button ', () => {
        const wrapper = mount(NavBar, {store, localVue, router})
        expect(wrapper.find("#navBarSearchBtn").exists()).toBe(true)
        expect(wrapper.find("#dashboardButton").exists()).toBe(true)
        expect(wrapper.find(".myaccount").exists()).toBe(true)


    })

    it('Clicking on the Dashboard button the user gets redirected to the Search page', () => {
        const wrapper = mount(NavBar, {store, localVue, router})
        wrapper.find("#navBarSearchBtn").trigger('click')
        expect(wrapper.findComponent({name: 'searchUser'}))

    })

    it('Clicking on the Dashboard button the user gets redirected to Admin dashboard', () => {
        const wrapper = mount(NavBar, {store, localVue, router})
        wrapper.find("#dashboardButton").trigger('click')
        expect(wrapper.findComponent({name: 'AdminDashboard'}))

    })

})

/**
 *  Below is the scenario when the Global Admin logs with permission level 2 logs in
 */
describe('NavBar after global admin login is successful', () => {
        let getters
        let store

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
            }
            store = new Vuex.Store({
                getters
            })
        })

        it('NavBar should not have any profile button for global admin', () => {
            const wrapper = mount(NavBar, {store, localVue, router})
            expect(wrapper.find("#profileButton").exists()).toBe(false)
        })
    })