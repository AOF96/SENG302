/* eslint-env jest*/

import {createLocalVue, mount} from '@vue/test-utils'
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


  it('NavBar should not have login button anymore and have myaccount button instead', () => {
    const wrapper = mount(NavBar, {store, localVue, router})
    expect(wrapper.find(".login").exists()).toBe(false)
    expect(wrapper.find(".myaccount").exists()).toBe(true)

  })

  // it('NavBar should redirect to  ', () => {
  //     const wrapper = mount(NavBar, { store, localVue, router })
  //     wrapper.find( ".login").trigger('click')
  //     expect(window.location.href).toBe('http://localhost/#/login')
  // })

  it('myaccount button that is on the NavBar should take the user to profile page', () => {
    const wrapper = mount(NavBar, {store, localVue, router})
    wrapper.find(".myaccount").trigger('click')
    expect(window.location.href).toBe('http://localhost/#/profile/100')
  })


})