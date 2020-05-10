import { mount, createLocalVue } from '@vue/test-utils'
import VueRouter from 'vue-router'
import NavBar from '../modules/NavBar.vue'
import Vuex from 'vuex'

const localVue = createLocalVue()
localVue.use(VueRouter)
const router = new VueRouter()
localVue.use(Vuex)

describe('NavBar before login or sign up', () => {
    let getters
    let store
    let actions

    beforeEach(() => {
        getters = {
            user: () => ({
              isLogin: false
            }),
            isAdmin: () => {
                false
            },
        },
        store = new Vuex.Store({
            getters, actions
        })
    })


    it('NavBar should have before login button and sign up', () => {
        const wrapper = mount(NavBar, { store, localVue, router })
        expect(wrapper.find( ".login").exists()).toBe(true)
        expect(wrapper.find( ".signup").exists()).toBe(true)

    })
})
