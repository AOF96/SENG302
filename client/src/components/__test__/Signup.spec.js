/* eslint-env jest*/
import {createLocalVue, shallowMount} from '@vue/test-utils'
import Signup from '@/components/Signup.vue'
import Vuex from 'vuex'
import user from '@/store/user.module'

//mock api
jest.mock("@/api")

//mock router
const mocks = {
  $router: {
    push: jest.fn()
  }
}

//make the test igonre router-link when found
const stubs = ['router-link']


const localVue = createLocalVue()
localVue.use(Vuex)

const state = {
  user: {
    firstname: "",
    lastname: "",
    middlename: "",
    nickname: "",
    gender: "",
    primary_email: "a",
    date_of_birth: "",
    bio: "superlongbio".repeat(100),
    fitness: "",
    password: ""
  }
};


describe('Signup page display', () => {
  let store
  let wrapper

  beforeEach(() => {
    store = new Vuex.Store({
      state: state,
      getters: user.getters
    })

    wrapper = shallowMount(Signup, {
      localVue,
      store,
      mocks,
      stubs
    })
  })

  it('should display the Sign up title', () => {
    expect(wrapper.find("div.signUpFormContainer h1").text()).toContain("Sign Up")
  })

  it('should display sign up button', () => {
    expect(wrapper.find("button.loginButton").exists()).toBe(true)
  })

  it('should have first name input box', () => {
    expect(wrapper.find("#signup-firstname").exists()).toBe(true)
  })

  it('should have middle name input box', () => {
    expect(wrapper.find("#signup-middlename").exists()).toBe(true)
  })

  it('should have last name input box', () => {
    expect(wrapper.find("#signup-lastname").exists()).toBe(true)
  })

  it('should have nickname input box', () => {
    expect(wrapper.find("#signup-nickname").exists()).toBe(true)
  })

  it('should have gender input box', () => {
    expect(wrapper.find("#signup-gender").exists()).toBe(true)
  })

  it('should have bio input box', () => {
    expect(wrapper.find("#signup-bio").exists()).toBe(true)
  })

  it('should have primary email input box', () => {
    expect(wrapper.find("#signup-primary-email").exists()).toBe(true)
  })

  it('should have dob input box', () => {
    expect(wrapper.find("#signup-dob").exists()).toBe(true)
  })

  it('should have password1 input box', () => {
    expect(wrapper.find("#signup-password-1").exists()).toBe(true)
  })

  it('should have password2 input box', () => {
    expect(wrapper.find("#signup-password-2").exists()).toBe(true)
  })

  it('should show an error if first name is empty', () => {
    expect(wrapper.find("#signup-firstname-err").text()).toContain("Please enter your First name")
  })

  it('should show an error if last name is empty', () => {
    expect(wrapper.find("#signup-lastname-err").text()).toContain("Please enter your Last name")
  })

  it('should show an error if email is empty', () => {
    expect(wrapper.find("#signup-primary-email-err").text()).toContain("Please enter a valid Email")
  })

  it('should show an error if bio is too long', () => {
    expect(wrapper.find("#signup-bio-err").text()).toContain("You have reached the maximum amount of characters")
  })

  it('should show an error if dob is not within #common-sense', () => {
    expect(wrapper.find("#signup-dob-err").text()).toContain("Please select a valid date of birth")
  })
})

