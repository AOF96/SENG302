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

    beforeEach(() => {
        getters = {
            user: () => ({
              isLogin: false
            })
        },
        store = new Vuex.Store({
            getters
        })
    })


    it('NavBar should have ', () => {
        const wrapper = mount(NavBar, { store, localVue, router })
        //expect(wrapper.find( "Login In").exists()).toBe(true)

        //expect(wrapper.isVisible()).toBe(true)
       // expect(wrapper.find('.is-not-visible').isVisible()).toBe(false)
      //  let button = wrapper.find('Login');
       // expect(wrapper.find({neme: "Login In"}).exists()).toBe(true)
       // expect(button.text()).toBe('Login');
    })


})