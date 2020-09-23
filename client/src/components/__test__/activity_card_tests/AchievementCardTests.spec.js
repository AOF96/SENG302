/* eslint-env jest*/
import Vue from "vue";
import {createLocalVue, mount} from "@vue/test-utils";
import Vuex from "vuex";
import Vuetify from "vuetify";
import {apiActivity} from "../../../api";
import AchievementsCard from "../../activity/modules/AchievementsCard";
import flushPromises from "flush-promises";
import {expect} from "@jest/globals";

Vue.use(Vuetify);

// creates Vue environment
const localVue = createLocalVue();
localVue.use(Vuex);

//mock api
jest.mock("@/api");

//mock router
const mocks = {
  $route: {
    params: {
      activityId: 99,
    },
  },
  $router: {
    push: jest.fn(),
  },
};

//make the test ignore router-link when found
const stubs = ["router-link"];

// mocking getActivityById by stating what is returned by this method if loading is successful
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
  visibility: "public",
  achievements: [
    {
      id: 77001,
      name: "Time taken",
      description: "Start to finish",
      resultType: "TIME"
    }
  ]
});

// mocking getActivityById by stating what is returned by this method if loading is successful
apiActivity.getResults.mockResolvedValue({
  "achievementId": 1,
  "value": "test"
});

describe("Test if achievements are displayer", () => {
  let store;
  let wrapper;
  let vuetify;

  // getters in Vuex
  let getters = {
    activity: () => ({
      author_id: 2,
      name: null,
      continuous: null,
      start_time: null,
      end_time: null,
      description: null,
      location: null,
      activity_types: ["relaxing"],
      activity_id: null,
      visibility: "public",
      achievements: [
        {
          id: 77001,
          name: "Time taken",
          description: "Start to finish",
          resultType: "TIME"
        }
      ]
    }),
  };

  beforeEach(() => {
    vuetify = new Vuetify();
    store = new Vuex.Store({
      //defined at let getters above
      getters,
    });
    // create environment (called wrapper) of where you are doing the test (Activity.vue with vuex store,
    // vue environment, mocks, and stubs for methods you don't need.
    wrapper = mount(AchievementsCard, {
      store,
      localVue,
      mocks,
      stubs,
      vuetify,
      propsData: {
        achievements: {"id": 1}
      }
    });
  });

  // it("sanity check", async () => {
  //   await flushPromises();
  //   expect(wrapper.find("#achievementCardTitle").exists()).toBe(true);
  // });

  it("card should exist", async () => {
    await flushPromises();
    expect(wrapper.find("#achievementCard").exists()).toBe(true);
  });
});