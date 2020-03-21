import { mount, createLocalVue } from '@vue/test-utils'
import VueRouter from 'vue-router'
import Login from '../Login.vue'
import Vuex from 'vuex'

const localVue = createLocalVue()
localVue.use(VueRouter)
const router = new VueRouter()
localVue.use(Vuex)

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

  it('Login page should have ', () => {
    const wrapper = mount(Login, { store, localVue, router })
    expect(wrapper.text()).toContain('Login')
    expect(wrapper.text()).toContain('Sign in to your account')

  })

  it('When login fails, the error message should show up', () => {
    const wrapper = mount(Login, { store, localVue, router })
    expect(wrapper.text()).toContain('Login')
    wrapper.find({ name: "Login" }).trigger('click')
  })

})