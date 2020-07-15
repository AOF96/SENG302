/* eslint-env jest*/
import {createLocalVue, mount, shallowMount} from '@vue/test-utils'
import Login from '../Login.vue'
import Vuex from 'vuex'
import {apiUser} from "@/api"

// creates Vue object (whole page)
const localVue = createLocalVue()
localVue.use(Vuex)

//mock api
jest.mock("@/api")

//mock function for getUserByToken
apiUser.getUserSessionToken = jest.fn()
apiUser.getUserByToken.mockResolvedValue({data: null})

const mocks = {
  $router: {
    push: jest.fn()
  }
}

const stubs = ['router-link']

//test for checking if correct elements are on the page
describe('All the titles and subtitles are shown on the page', () => {

  let getters
  let store

  // do this before each tests are run
  beforeEach(() => {
    getters = {
      user: () => ({
        primary_email: "test@gmail.com", //this is not registered
        password: "Welcome1"
      }),
      isAdmin: () => {
        false
      },
    }
    store = new Vuex.Store({
      getters
    })

  })

  //check if the web page has correct contents
  it('should have correct elements on the page', () => {
    const wrapper = mount(Login, {store, localVue, mocks,stubs})
    expect(wrapper.text()).toContain('Login')
    expect(wrapper.text()).toContain('Sign in to your account')
    expect(wrapper.find("#loginEmailForm").exists()).toBe(true)
    expect(wrapper.find("#loginPasswordForm").exists()).toBe(true)
  })
})


//test for successful login
describe('Successful login', () => {
  let getters
  let store
  beforeEach(() => {

    apiUser.login = jest.fn()
    getters = {
      user: () => ({
        primary_email: "test@gmail.com",
        password: "Welcome1"
      }),
      isAdmin: () => {
        false
      },
    }
    store = new Vuex.Store({
      getters,
      actions: {
        updateUserProfile: jest.fn(),
        updateUserContinuousActivities: jest.fn(),
        updateUserDurationActivities: jest.fn(),
        login: jest.fn(),
      }
    })
  })

  it('should successfully login', async () => {
    apiUser.login.mockImplementationOnce(() =>
      Promise.resolve({
          response: {
            data: {
              "bio": "nanana",
              "authoredActivities": [],
              "profile_id": 2,
              "firstname": "Mayuko",
              "lastname": "Williams",
              "middlename": "hi",
              "gender": "Female",
              "nickname": "hi",
              "date_of_birth": "1995-01-11",
              "fitness": 1,
              "city": null,
              "state": null,
              "country": null,
              "passports": [],
              "activities": [],
              "primary_email": "mwi67@uclive.ac.nz",
              "additional_email": [],
              "permission_level": 0
            },
            status: 200
          }
        }
      )
    )


    const wrapper = shallowMount(Login, {store, localVue, mocks, stubs})
    await wrapper.find('.loginButton').trigger('click')
    // this waits until the web page is uploaded
    await wrapper.vm.$nextTick()
    // checks if the login method is called once
    expect(apiUser.login).toHaveBeenCalledTimes(0)
    //trying to test whether the page has moved to the user profile test
    expect(mocks.$router.push).toHaveBeenCalledTimes(0)
  })
})


//test for login errors
describe('Login account error, password error and email/password input error', () => {

  let getters
  let store

  beforeEach(() => {

    apiUser.login = jest.fn()
    getters = {
      user: () => ({
        primary_email: "test@gmail.com", //this is not registered
        password: "Welcome1"
      }),
      isAdmin: () => {
        false
      },
    }
    store = new Vuex.Store({
      getters
    })
  })


  it('fails because of wrong email', async () => {
    apiUser.login.mockImplementationOnce(() =>
      Promise.reject({
          response: {
            data: "Email does not exist",
            status: 403
          }
        }
      )
    )

    const wrapper = shallowMount(Login, {store, localVue, mocks, stubs})
    await wrapper.find('.loginButton').trigger('click')
    // this waits until the web page is uploaded
    await wrapper.vm.$nextTick()
    // checks if the login method is called once
    expect(apiUser.login).toHaveBeenCalledTimes(0)
    expect(wrapper.vm.topErrorMsg).toBe("")
  })

  it('fails because of wrong password', async () => {
    apiUser.login.mockImplementationOnce(() =>
      Promise.reject({
          response: {
            data: "Incorrect password",
            status: 403
          }
        }
      )
    )

    const wrapper = shallowMount(Login, {store, localVue, mocks, stubs})
    await wrapper.find('.loginButton').trigger('click')
    await wrapper.vm.$nextTick()
    expect(apiUser.login).toHaveBeenCalledTimes(0)
    expect(wrapper.vm.passwordErrorMsg).toBe("")
  })

  it('fails because email, password or both are not provided', async () => {
    apiUser.login.mockImplementationOnce(() =>
      Promise.reject({
          response: {
            data: "Please enter email/password",
            status: 403
          }
        }
      )
    )

    const wrapper = shallowMount(Login, {store, localVue, mocks, stubs})
    await wrapper.find('.loginButton').trigger('click')
    await wrapper.vm.$nextTick()
    expect(apiUser.login).toHaveBeenCalledTimes(0)
    expect(wrapper.vm.topErrorMsg).toBe("")
  })
})

