/* eslint-env jest*/

import {createLocalVue, mount} from '@vue/test-utils'
import VueRouter from 'vue-router'
import NavBar from '../modules/NavBar.vue'
import Vuex from 'vuex'

const localVue = createLocalVue()
localVue.use(VueRouter)
const router = new VueRouter()
localVue.use(Vuex)

describe('NavBar after the login is successful as a normal user', () => {
  let getters
  let store
  let actions

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
    actions = {logout: jest.fn()}
    store = new Vuex.Store({
      getters,
      actions
    })
  })

  it('NavBar should not have login button anymore and have myaccount button instead', () => {
    const wrapper = mount(NavBar, {store, localVue, router})
    expect(wrapper.find(".login").exists()).toBe(false)
    expect(wrapper.find(".myaccount").exists()).toBe(true)
    //myaccount = profile button
  })

  it('myaccount button that is on the NavBar should take the user to profile page', () => {
    const wrapper = mount(NavBar, {store, localVue, router})
    wrapper.find(".myaccount").trigger('click')
    expect(window.location.href).toBe('http://localhost/#/profile/100')
  })

  it('should have logout button and the button should call logout method', () => {
    const wrapper = mount(NavBar, {store, localVue, router})
    expect(wrapper.find('#navBarLogoutButton').exists()).toBe(true)
    wrapper.find('#navBarLogoutButton').trigger('click')
    expect(actions.logout).toHaveBeenCalledTimes(1)
    expect(wrapper.findComponent({name: 'Login'}))
  })

  it('should have search button and the button should takr you to search page', () => {
    const wrapper = mount(NavBar, {store, localVue, router})
    expect(wrapper.find('#navBarSearchBtn').exists()).toBe(true)
    wrapper.find('#navBarSearchBtn').trigger('click')
    expect(wrapper.findComponent({name: 'searchUser'}))
  })
})