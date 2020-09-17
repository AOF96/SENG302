/* eslint-env jest*/
import {createLocalVue, mount, shallowMount} from '@vue/test-utils'
import VueRouter from 'vue-router'
import NavBar from '../modules/NavBar.vue'
import Vuex from 'vuex'

const localVue = createLocalVue();
localVue.use(VueRouter);
const router = new VueRouter();
localVue.use(Vuex);

describe('NavBar after the login is successful', () => {
  let getters;
  let store;

  beforeEach(() => {
    getters = {
      user: () => ({
        profile_id: 100,
        isLogin: true,
        permission_level: 0,
        firstname: "John",
        lastname: "Smith",
        location: {
          latitude: 120.47,
          longitude: -20.12
        }
      }),
      isAdmin: () => {
        false
      },
    };
    store = new Vuex.Store({
      getters
    })
  });


  it('NavBar should not have login button anymore and have website logo button instead', () => {
    const wrapper = mount(NavBar, {store, localVue, router});
    expect(wrapper.find(".login").exists()).toBe(false);
    expect(wrapper.find("#appNavLogo").exists()).toBe(true)

  });

  it("Website's logo button that is on the NavBar on the click should redirects the user to the logged in user's news feed", () => {
    const wrapper = mount(NavBar, {store, localVue, router});
    wrapper.find("#appNavLogo").trigger('click');
    expect(window.location.href).toBe('http://localhost/#/feed/')
  });

  it('Global search bar exists on the top of the NavBar', () => {
    const wrapper = mount(NavBar, {store, localVue, router});
    expect(wrapper.find("#globalSearchBarInput").exists()).toBe(true)
  });

  it('A hamburger menu option exists for quick navigation around website in the of the NavBar', () => {
    const wrapper = mount(NavBar, {store, localVue, router});
    expect(wrapper.find("#headerNavToggle").exists()).toBe(true)
  });

  it('The hamburger menu has a Home button and clicking it takes the user to the Home Feed.', () => {
    const wrapper = mount(NavBar, {store, localVue, router});
    expect(wrapper.find("#homeButton").exists()).toBe(true);
    wrapper.find("#homeButton").trigger('click');
    expect(window.location.href).toBe('http://localhost/#/feed/')
  });

  it('The hamburger menu has a Map button and clicking it takes the user to the Map page.', () => {
    const wrapper = mount(NavBar, {store, localVue, router});
    expect(wrapper.find("#hamburgerMap").exists()).toBe(true);
    wrapper.find("#hamburgerMap").trigger('click');
    expect(window.location.href).toBe('http://localhost/#/map/@120.47,-20.12')
    // expect(wrapper.findComponent({name: "Map"}).exists()).toBe(true)
  });

  it('The hamburger menu has a Profile button and clicking it takes the user to the Profile page.', () => {
    const wrapper = mount(NavBar, {store, localVue, router});
    expect(wrapper.find("#profileButton").exists()).toBe(true);
    wrapper.find("#profileButton").trigger('click');
    expect(window.location.href).toBe('http://localhost/#/profile/100')
  });

  it('The hamburger menu has a Search button and clicking it takes the user to the Search page.', () => {
    const wrapper = mount(NavBar, {store, localVue, router});
    expect(wrapper.find("#searchButton").exists()).toBe(true);
    wrapper.find("#searchButton").trigger('click');
    expect(window.location.href).toBe('http://localhost/#/search')
  });

  it('The hamburger menu has a Settings button and clicking it takes the user to the Settings page.', () => {
    const wrapper = mount(NavBar, {store, localVue, router});
    expect(wrapper.find("#settingsButton").exists()).toBe(true);
    wrapper.find("#settingsButton").trigger('click');
    expect(window.location.href).toBe('http://localhost/#/settings/profile/100')
  });


  it("The hamburger menu displays the user's name John Smith.", () => {
    const wrapper = mount(NavBar, {store, localVue, router});
    expect(wrapper.find("#hamburgerName").text()).toBe("John Smith")
  });

  it("The hamburger menu states that the user is Signed In.", () => {
    const wrapper = mount(NavBar, {store, localVue, router});
    expect(wrapper.find("#hamburgerSignedIn").text()).toBe("Signed In")
  });

});