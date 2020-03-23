import { mount, createLocalVue } from '@vue/test-utils'
import VueRouter from 'vue-router'
import NavBar from '../NavBar.vue'
import Vuex from 'vuex'

const localVue = createLocalVue()
localVue.use(VueRouter)
const router = new VueRouter()
localVue.use(Vuex)

describe('NavBar.vue', () => {
    let getters
    let store
    let actions

    beforeEach(() => {
        getters = {
            user: () => ({
              isLogin: false
            })
        },
            actions = {
                user: () => ({
                    isLogin: true
                })
            },
        store = new Vuex.Store({
            getters, actions
        })
    })


    it('NavBar should have ', () => {
        const wrapper = mount(NavBar, { store, localVue, router })
        expect(wrapper.find( ".login").exists()).toBe(true)
        expect(wrapper.find( ".signup").exists()).toBe(true)

    })

})