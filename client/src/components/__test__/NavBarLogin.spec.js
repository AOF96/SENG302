import { mount, createLocalVue } from '@vue/test-utils'
import VueRouter from 'vue-router'
import NavBar from '../modules/NavBar.vue'
import Vuex from 'vuex'

const localVue = createLocalVue()
localVue.use(VueRouter)
const router = new VueRouter()
localVue.use(Vuex)

describe('NavBar.vue', () => {
    let getters
    let store

    beforeEach(() => {
        getters = {
            user: () => ({
                isLogin: true
            })
        },
            store = new Vuex.Store({
                getters
            })
    })


    it('NavBar should have ', () => {
        const wrapper = mount(NavBar, { store, localVue, router })
        expect(wrapper.find( ".login").exists()).toBe(true)
        expect(wrapper.find( ".myaccount").exists()).toBe(true)

    })

    // it('NavBar should redirect to  ', () => {
    //     const wrapper = mount(NavBar, { store, localVue, router })
    //     wrapper.find( ".login").trigger('click')
    //     expect(window.location.href).toBe('http://localhost/#/login')
    // })

    it('NavBar should redirect to  ', () => {
        const wrapper = mount(NavBar, { store, localVue, router })
        wrapper.find( ".myaccount").trigger('click')
        expect(window.location.href).toBe('http://localhost/#/profile')
    })


})