/* eslint-env jest*/

import Vuex from "vuex";
import Profile from "../profile/Profile";
import { apiUser } from "@/api";
import axios from "axios";
import flushPromises from "flush-promises";

import { createLocalVue, mount } from "@vue/test-utils";
import PassportCountries from "../modules/PassportCountries";

// creates Vue object (whole page)
const localVue = createLocalVue();
localVue.use(Vuex);

//mock api
jest.mock("@/api");

//mock axios
jest.mock("axios");

// mock router push (change pages)
const mocks = {
    $route: {
        params: {
            profileId: 2,
        },
    },
    $router: {
        push: jest.fn(),
    },
};

// if vue js finds router-link, ignore it
const stubs = ["router-link"];

apiUser.getUserById.mockResolvedValue({
    bio: null,
    authoredActivities: [],
    profile_id: 2,
    firstname: "John",
    lastname: "Doe",
    middlename: null,
    gender: "Non-Binary",
    nickname: null,
    date_of_birth: "1911-12-30",
    fitness: 1,
    city: null,
    state: null,
    country: null,
    passports: ["Canada"],
    activities: ["swimming"],
    primary_email: "sg@123.com",
    additional_email: [],
    permission_level: 0,
});

apiUser.getUserContinuousActivities.mockResolvedValue({
    data: [],
});

apiUser.getUserDurationActivities.mockResolvedValue({
    data: [],
});

axios.get.mockResolvedValue({
    data: [],
});

//test for checking when admin goes to user profile, there is 'make admin' button
describe("button appears on the bottom left of the profile page if admin", () => {
    let getters;
    let store;

    // do this before each tests are run
    beforeEach(() => {
        getters = {
            // admin
            user: () => ({
                primary_email: "test@gmail.com", //this is not registered
                password: "Welcome1",
                permission_level: 1,
                isLogin: true,
            }),
            isAdmin: () => ({
                isAdmin: false,
            }),

            userSearch: () => ({
                searchTerm: null,
                searchType: null,
                page: null,
                size: null,
                scrollPos: 0,
            }),
        };
        store = new Vuex.Store({
            getters,
        });
    });
    it("There Should be an edit profile button on click redirects to the Edit profile page", async () => {
        const wrapper = mount(Profile, { store, localVue, mocks, stubs });
        await flushPromises();
        expect(wrapper.find("#editProfileButton").exists()).toBe(true);
        wrapper.find("#editProfileButton").trigger('click')
        expect(wrapper.findComponent({name: 'EditUserInfo'}))
    });

    it("There Should be an Add activity button on click redirects to the Add activity page", async () => {
        const wrapper = mount(Profile, { store, localVue, mocks, stubs });
        await flushPromises();
        expect(wrapper.find("#addActivityButton").exists()).toBe(true);
        wrapper.find("#addActivityButton").trigger('click')
        expect(wrapper.findComponent({name: 'AddUserActivity'}))
    });

    it('should display the Logged in user details in the panel on profile page', () => {
        const wrapper = mount(Profile, { store, localVue, mocks, stubs });
        expect(wrapper.find(".leftSidebarContainer").text()).toContain("Profile Info")
    })

    it('should display the Logged in user passport country details in the panel on the right of the page ', () => {
        const wrapper = mount(PassportCountries, { store, localVue, mocks, stubs });
        expect(wrapper.find(".profileModule").text()).toContain("Passport Countries")
    })

});