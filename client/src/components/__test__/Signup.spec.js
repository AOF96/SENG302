/* eslint-env jest*/
import {createLocalVue, shallowMount} from '@vue/test-utils'
import Signup from '@/components/Signup.vue'
import Vuex from 'vuex'
import user from '@/store/user.module'
import {apiUser} from "../../api";

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

describe('profile created', () => {
  apiUser.signUp = jest.fn()
  const actions = {
    createUserProfile: jest.fn()
  }

  const getters = {
    user: () => ({
      firstname: "Wendy",
      lastname: "Lambkins",
      middlename: "",
      nickname: "",
      gender: "Female",
      primary_email: "wendy@ac.com",
      date_of_birth: "2000-01-01",
      fitness: 1,
      password: "Water123",
      bio: ""
    })
  }
  const store = new Vuex.Store({
    actions,
    getters
  })

  apiUser.signUp.mockResolvedValue({
    data: {
      profile_id: 1
    }
  })

  it('should call createUserProfile and router push to move to profile page', async () => {
    const wrapper = shallowMount(Signup, {store, localVue, mocks, stubs})
    wrapper.setData({password1: "Water123", password2: "Water123"})

    await wrapper.find('#signUpButton').trigger('click')
    await wrapper.vm.$nextTick()
    expect(apiUser.signUp).toHaveBeenCalledTimes(1)
    expect(actions.createUserProfile).toHaveBeenCalledTimes(1)
    expect(mocks.$router.push).toHaveBeenCalledTimes(1)
  })
})

describe('password error handling', () => {

  let store
  let wrapper

  const getters = {
    user: () => ({
      firstname: "Wendy",
      lastname: "Lambkins",
      middlename: "",
      nickname: "",
      gender: "Female",
      primary_email: "wendy@ac.com",
      date_of_birth: "2000-01-01",
      fitness: 1,
      password: "Water123",
      bio: ""
    })
  }

  beforeEach(() => {
    store = new Vuex.Store({
      state: state,
      getters
    })

    wrapper = shallowMount(Signup, {
      localVue,
      store,
      mocks,
      stubs
    })
  })

  it('should show an error if password is shorter than 8 characters', () => {

    const input = wrapper.find('#signup-password-1')
    input.element.value = 'Water12'
    input.trigger('input')
    const input2 = wrapper.find('#signup-password-2')
    input2.element.value = 'Water12'
    input2.trigger('input')

    expect(wrapper.vm.validation.password.length).toBe(false)
  })

  it('should show an error if password1 and password2 do not match', () => {
    const input = wrapper.find('#signup-password-1')
    input.element.value = 'Water123'
    input.trigger('input')
    const input2 = wrapper.find('#signup-password-2')
    input2.element.value = 'Fire123'
    input2.trigger('input')

    expect(wrapper.vm.validation.password.match).toBe(false)
  })

  it('should show an error if number is not included in the password', () => {
    const input = wrapper.find('#signup-password-1')
    input.element.value = 'Water'
    input.trigger('input')
    const input2 = wrapper.find('#signup-password-2')
    input2.element.value = 'Water'
    input2.trigger('input')

    expect(wrapper.vm.validation.password.number).toBe(false)
  })

  it('should show an error if there is no lowercase letters in the password', () => {
    const input = wrapper.find('#signup-password-1')
    input.element.value = 'WATER123'
    input.trigger('input')
    const input2 = wrapper.find('#signup-password-2')
    input2.element.value = 'WATER123'
    input2.trigger('input')

    expect(wrapper.vm.validation.password.lowercase).toBe(false)
  })

  it('should show an error if there is no uppercase letters in the password', () => {
    const input = wrapper.find('#signup-password-1')
    input.element.value = 'water123'
    input.trigger('input')
    const input2 = wrapper.find('#signup-password-2')
    input2.element.value = 'water123'
    input2.trigger('input')

    expect(wrapper.vm.validation.password.uppercase).toBe(false)
  })
})