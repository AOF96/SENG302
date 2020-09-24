/* eslint-env jest*/
import { mount, createLocalVue } from '@vue/test-utils'
import VueRouter from 'vue-router'
import NavBar from '../modules/NavBar.vue'
import Vuex from 'vuex'
import Vuetify from "vuetify";
import {expect} from "@jest/globals";
const localVue = createLocalVue();
localVue.use(Vuetify);
localVue.use(VueRouter);
const router = new VueRouter();
localVue.use(Vuex);

describe('NavBar before login or sign up', () => {
    let getters;
    let store;
    let actions;
    let vuetify;

    beforeEach(() => {
        vuetify = new Vuetify();
        getters = {
            user: () => ({
              isLogin: false
            }),
            isAdmin: () => {
                false
            },
        },
        store = new Vuex.Store({
            getters, actions
        })
    })


    it('NavBar should have before login button and sign up', () => {
        const wrapper = mount(NavBar, {store, localVue, router, vuetify});
        expect(wrapper.find( ".login").exists()).toBe(true)
        expect(wrapper.find( ".signup").exists()).toBe(true)
    })

    it('The hamburger menu has a Login button and clicking it takes the user to the Login page.', () => {
        const wrapper = mount(NavBar, {store, localVue, router, vuetify});
        expect(wrapper.find("#hamburgerLogin").exists()).toBe(true);
        wrapper.find("#hamburgerLogin").trigger('click');
        expect(window.location.href).toBe('http://localhost/#/login')
    });

    it('The hamburger menu has a dark mode toggle switch.', () => {
        const wrapper = mount(NavBar, {store, localVue, router, vuetify});
        expect(wrapper.find("#darkModeToggle").exists()).toBe(true);
    });

    it('The hamburger menu has a Sign Up button and clicking it takes the user to the Sign Up page.', () => {
        const wrapper = mount(NavBar, {store, localVue, router, vuetify});
        expect(wrapper.find("#hamburgerSignUp").exists()).toBe(true);
        wrapper.find("#hamburgerSignUp").trigger('click');
        expect(window.location.href).toBe('http://localhost/#/signup')
    });

    it("The hamburger menu states that the user is Signed Out.", () => {
        const wrapper = mount(NavBar, {store, localVue, router, vuetify});
        expect(wrapper.find("#hamburgerSignedOut").text()).toBe("Signed Out")
    });
})
