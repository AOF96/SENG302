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
        permission_level: 0,
      }),
      isAdmin: () => {
        false
      },
    }
    store = new Vuex.Store({
      getters
    })
  })


  it('NavBar should not have login button anymore and have website logo button instead', () => {
    const wrapper = mount(NavBar, {store, localVue, router})
    expect(wrapper.find(".login").exists()).toBe(false)
    expect(wrapper.find("#appNavLogo").exists()).toBe(true)

  })

  it("Website's logo button that is on the NavBar on the click should redirects the user to the logged in user's news feed", () => {
    const wrapper = mount(NavBar, {store, localVue, router})
    wrapper.find("#appNavLogo").trigger('click')
    expect(window.location.href).toBe('http://localhost/#/feed/')
  })

  it('Global search bar exists on the top of the NavBar', () => {
    const wrapper = mount(NavBar, {store, localVue, router})
    expect(wrapper.find("#globalSearchBarInput").exists()).toBe(true)
  })

  it('A hamburger menu option exists for quick navigation around website in the of the NavBar', () => {
    const wrapper = mount(NavBar, {store, localVue, router})
    expect(wrapper.find("#headerNavToggle").exists()).toBe(true)
  })

})