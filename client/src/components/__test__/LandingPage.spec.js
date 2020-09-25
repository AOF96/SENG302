/* eslint-env jest*/
import {createLocalVue, shallowMount} from '@vue/test-utils'
import Landing from '@/components/Landing.vue'
import Vuex from 'vuex'
import user from '@/store/user.module'
import Vuetify from "vuetify";
import {expect} from "@jest/globals";
import VueRouter from 'vue-router'

//mock api
jest.mock("@/api");

//mock router
const mocks = {
  $router: {
    push: jest.fn()
  }
};

const localVue = createLocalVue();
localVue.use(Vuex);
localVue.use(Vuetify);
localVue.use(VueRouter);
const router = new VueRouter();

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

describe('Landing page display', () => {
  let store;
  let wrapper;

  beforeEach(() => {
    store = new Vuex.Store({
      state: state,
      getters: user.getters
    });

    wrapper = shallowMount(Landing, {
      localVue,
      store,
      mocks,
      router
    })
  });

  it('should display the Passage title', () => {
    expect(wrapper.find("#landingMainHeading").text()).toContain("Passage")
  });

  it('should display the Passage sub heading', () => {
    expect(wrapper.find("#landingSubHeading").text()).toContain("The number one companion for any " +
        "traveller. Create, manage, and explore activities near you. Start your journey today.")
  });

  it('should display the About Passage heading', () => {
    expect(wrapper.find("#landingAboutHeading").text()).toContain("About Passage")
  });

  it('should display the Passage description', () => {
    expect(wrapper.find("#landingAboutDescription").exists()).toBe(true)
  });

  it('should display the Passage copyright info', () => {
    expect(wrapper.find("#landingCopyRight").exists()).toBe(true)
  });

  it('should display the logo', () => {
    expect(wrapper.find("#landingIcon").exists()).toBe(true)
  });

  it('should display the Login button', () => {
    expect(wrapper.find("#landingLoginButton").exists()).toBe(true);
    wrapper.find("#landingLoginButton").trigger('click');
    expect(window.location.href).toBe('http://localhost/#/login')
  });

  it('should display the Sign up button', () => {
    expect(wrapper.find("#landingSignUpButton").exists()).toBe(true);
    wrapper.find("#landingSignUpButton").trigger('click');
    expect(window.location.href).toBe('http://localhost/#/signup')
  })
});