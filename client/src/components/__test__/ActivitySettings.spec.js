/* eslint-env jest*/
import Vuex from "vuex";
// import Profile from "../profile/Profile";
import {createLocalVue, mount, shallowMount} from "@vue/test-utils";
import ActivitySettings from "../activity/settings/ActivitySettings.vue";
import flushPromises from "flush-promises";
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

apiActivity.getActivityById.mockResolvedValue({
    id: 4,
    users: [],
    author: {
      bio: "nanana",
      authoredActivities: [],
      profile_id: 2,
      firstname: "Mayuko",
      lastname: "Williams",
      middlename: "hi",
      gender: "Female",
      nickname: "hi",
      date_of_birth: "1995-01-11",
      fitness: 1,
      city: null,
      state: null,
      country: null,
      passports: [],
      activities: [],
      primary_email: "mwi67@uclive.ac.nz",
      additional_email: [],
      permission_level: 0,
    },
    activity_name: "Create shortcut",
    description: "You go to nano bashrc first",
    activity_type: [{ name: "Extreme", users: [] }],
    continuous: false,
    start_time: 1593680400000,
    end_time: 1594159200000,
    location: null,
});

// if vue js finds router-link, ignore it
const stubs = ["router-link"];

describe("Check user's activity settings page", () => {
    let getters
    let store
    let actions

    beforeEach(() => {
      getters = {
        user: () => ({
          profile_id: 100,
          isLogin: true,
          permission_level: 0,
        }),
        isAdmin: () => ({
          isAdmin: false
        }),
      }
      actions = {logout: jest.fn()}
      store = new Vuex.Store({
        getters,
        actions
      })
      wrapper = mount(ActivitySettings, { store, actions })
    })

    beforeEach(() => {
        store = new Vuex.Store({
        getters,
        actions: {
          updateUserContinuousActivities: jest.fn(),
          updateUserDurationActivities: jest.fn(),
        }
      })
      wrapper = mount(ActivityPageInfo, { store, localVue, mocks, stubs })
    })

    it("should have an input field for the activity name", () => {
        expect(wrapper.find("#name").exists()).toBe(true);
    })

    it("should have an input field the duration type of activity", () => {
        expect(wrapper.find("#time").exists()).toBe(true);
    })

    it("should have an input field for the start date", () => {
        expect(wrapper.find("#start_date").exists()).toBe(true);
    })

    it("should have an input field for the end date", () => {
        expect(wrapper.find("#end_date").exists()).toBe(true);
    })

    it("should have an input field for the activity start time", () => {
        expect(wrapper.find("#start_time").exists()).toBe(true);
    })

    it("should have an input field for the activity start time", () => {
        expect(wrapper.find("#end_time").exists()).toBe(true);
    })

    it("should have a textarea field for the activity description", () => {
        expect(wrapper.find("#desc").exists()).toBe(true);
    })

    it("should have an input field for the activity location", () => {
        expect(wrapper.find("#locationInput").exists()).toBe(true);
    })

    it("should have a select field for the activity type", () => {
        expect(wrapper.find(".editActivitySelect").exists()).toBe(true);
    })
});
