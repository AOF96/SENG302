/* eslint-env jest*/

import Vuex from 'vuex'
import Profile from '../profile/Profile'
import {createLocalVue, mount} from '@vue/test-utils'
import {apiUser} from '@/api'

// creates Vue object (whole page)
const localVue = createLocalVue()
localVue.use(Vuex)

jest.mock("@/api")

const mocks = {
  $router: {
    push: jest.fn()
  }
}

const stubs = ['router-link']

apiUser.

//test for checking when admin goes to user profile, there is 'make admin' button
describe('button appears on the bottom left of the profile page if admin', () => {

  let getters
  let store

  // do this before each tests are run
  beforeEach(() => {
    getters = {
      user: () => ({
        primary_email: "test@gmail.com", //this is not registered
        password: "Welcome1",
        permission_level: 0,
        isLogin: true
      }),
      isAdmin: () => ({
        isAdmin: true
      }),

      userSearch: () => ({
        searchTerm: null,
        searchType: null,
        page: null,
        size: null,
        scrollPos: 0
      })
    }
    store = new Vuex.Store({
      getters
    })

  })
  it('should have admin promotion  button', () => {
    const wrapper = mount(Profile, {store, localVue, mocks, stubs})
    expect(wrapper.find("#profileAdminRightsButton").exists()).toBe(true)
  })
})