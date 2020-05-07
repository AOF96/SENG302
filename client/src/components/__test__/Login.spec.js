import {createLocalVue, mount, shallowMount} from '@vue/test-utils'
import VueRouter from 'vue-router'
import Login from '../Login.vue'
import Vuex from 'vuex'
import {apiUser} from '@/api'


const localVue = createLocalVue()
localVue.use(VueRouter)
const router = new VueRouter()
localVue.use(Vuex)

//mock api
jest.mock('@/api')

describe('Login.vue', () => {
  let getters
  let actions
  let store

  beforeEach(() => {
    getters = {
      user: () => ({
        primary_email: "test@gmail.com",
        password: "Welcome1"
      })
    }
    store = new Vuex.Store({
      getters
    })
  })

  apiUser.login.mockResolvedValue(
    new Promise((resolve, reject) => {
      reject({
          response: {
            data: "Email does not exist",
            status: 403
          }
        }
      )
    })
  )

  it('Login page should have ', () => {
    const wrapper = mount(Login, {store, localVue, router})
    expect(wrapper.text()).toContain('Login')
    expect(wrapper.text()).toContain('Sign in to your account')

  })

  it('When login fails with incorrect email, the error message should show up', done => {
    const wrapper = shallowMount(Login, {store, localVue, router})
    wrapper.vm.$nextTick(() => {
      const submitButton = wrapper.find('.loginButton')
      submitButton.trigger('click')
      expect(wrapper.find("#email_exist").attributes()['hidden']).toBe("hidden")
      done()
    })
  })

})