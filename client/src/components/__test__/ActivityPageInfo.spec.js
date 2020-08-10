/* eslint-env jest*/
import { mount, createLocalVue } from "@vue/test-utils";
import ActivityPageInfo from "../modules/ActivityPageInfo.vue";
import Vuex from "vuex";
import { apiActivity, } from "../../api";
import flushPromises from "flush-promises";
import {expect} from "@jest/globals";


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

//make the test igonre router-link when found
const stubs = ["router-link"];

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

describe("test if you are author of the activity", () => {
  let store
  let wrapper
  let getters = {
    user: () => ({
      firstname: "Mayuko",
      lastname: "Williams",
      middlename: "hi",
      nickname: "hi",
      gender: "Female",
      primary_email: "mwi67@uclive.ac.nz",
      additional_email: [],
      date_of_birth: "1995-01-11",
      bio: "nanana",
      isLogin: true,
      fitness: 1,
      profile_id: 2,
      password: null,
      passports: [],
      tmp_passports: [],
      permission_level: 0,
      activities: [],
      tmp_activities: [],
      cont_activities: [],
      dur_activities: [],
      location: {
        city: null,
        state: null,
        county: null,
      },
    }),

    activity: () => ({
      author_id: 2,
      name: null,
      continuous: null,
      start_time: null,
      end_time: null,
      description: null,
      location: null,
      activity_types: [],
      activity_id: null,
    }),
  };

  beforeEach(() => {
      store = new Vuex.Store({
      getters,
      actions: {
        updateUserContinuousActivities: jest.fn(),
        updateUserDurationActivities: jest.fn(),
      }
    });
    wrapper = mount(ActivityPageInfo, { store, localVue, mocks, stubs })
  });

  it('should have delete button on the page if you are the author', async() => {
    await flushPromises();
    expect(wrapper.find('#activityPageInfoDeleteButton').exists()).toBe(true)
  });

  it('should be able to delete the activity by clicking the delete button', async() => {
    await flushPromises();
    await wrapper.find('#activityPageInfoDeleteButton').trigger('click');
    await flushPromises();
    expect(apiActivity.deleteActivity).toBeCalledWith(2, 99)
  })
})

describe('test if you are not the author of this activity', () => {

  let store;
  let wrapper;
  let getters = {
    user: () => ({
      firstname: "Mayuko",
      lastname: "Williams",
      middlename: "hi",
      nickname: "hi",
      gender: "Female",
      primary_email: "mwi67@uclive.ac.nz",
      additional_email: [],
      date_of_birth: "1995-01-11",
      bio: "nanana",
      isLogin: true,
      fitness: 1,
      profile_id: 777,
      password: null,
      passports: [],
      tmp_passports: [],
      permission_level: 0,
      activities: [],
      tmp_activities: [],
      cont_activities: [],
      dur_activities: [],
      location: {
        city: null,
        state: null,
        county: null,
      },
    }),

    activity: () => ({
      author_id: 555,
      name: null,
      continuous: null,
      start_time: null,
      end_time: null,
      description: null,
      location: null,
      activity_types: [],
      activity_id: null,
    }),
  };

  beforeEach(() => {
    store = new Vuex.Store({
      getters,
      actions: {
        updateUserContinuousActivities: jest.fn(),
        updateUserDurationActivities: jest.fn(),
      }
    });
    wrapper = mount(ActivityPageInfo, { store, localVue, mocks, stubs })
  });

  it('should not have delete button on the profile page', async() => {
    await flushPromises();
    expect(wrapper.find('#activityPageInfoDeleteButton').exists()).toBe(false)
  })
});