/* eslint-env jest*/
import Vuex from "vuex";
import Profile from "../profile/Profile";
import axios from "axios";
import flushPromises from "flush-promises";

import { createLocalVue, mount } from "@vue/test-utils";

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

//test for checking when admin goes to user profile, there is 'make admin' button
describe("button appears on the bottom left of the profile page if admin", () => {
  // let getters;
  let store;
  let actions = {
    getUserById: jest.fn(),
    getUserContinuousActivities: jest.fn(),
    getUserDurationActivities: jest.fn(),
    getDataFromUrl: jest.fn(),
  };
  // do this before each tests are run
  beforeEach(() => {
    store = new Vuex.Store({
      getters: {
        // admin
        user: () => ({
          primary_email: "test@gmail.com", //this is not registered
          password: "Welcome1",
          permission_level: 1,
          isLogin: true,
        }),
        isAdmin: () => ({
          isAdmin: true,
        }),
        userSearch: () => ({
          searchTerm: null,
          searchType: null,
          page: null,
          size: null,
          scrollPos: 0,
        }),
      },
      actions
    });

    actions.getUserById.mockResolvedValue({
      bio: null,
      authoredActivities: [],
      profile_id: 2,
      firstname: "Mayuko",
      lastname: "Williams",
      middlename: null,
      gender: "Female",
      nickname: null,
      date_of_birth: "1911-11-11",
      fitness: 1,
      location: {city: "delhi",
        state: "delhi",
        country: "india"},
      passports: [],
      activities: [],
      primary_email: "mwi67@uclive.ac.nz",
      additional_email: [],
      permission_level: 0,
    });

    actions.getUserContinuousActivities.mockResolvedValue({
      data: [],
    });

    actions.getUserDurationActivities.mockResolvedValue({
      data: [],
    });

    axios.get.mockResolvedValue({
      data: [],
    });

  });
  it("should have admin promotion  button", async () => {
    const wrapper = mount(Profile, { store, localVue, mocks, stubs });
    await flushPromises();
    expect(wrapper.find("#profileAdminRightsButton").exists()).toBe(true);
  });
});