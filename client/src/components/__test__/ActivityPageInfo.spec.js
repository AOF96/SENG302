/* eslint-env jest*/
import Vue from "vue";
import { createLocalVue, mount } from "@vue/test-utils";
import Activity from "../activity/Activity.vue";
import Vuex from "vuex";
import { apiActivity, apiUser } from "../../api";
import flushPromises from "flush-promises";
import { expect } from "@jest/globals";
import Vuetify from "vuetify";

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

//make the test igonre router-link when found
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
});

apiActivity.getActivityStats.mockResolvedValue({data: {
    numFollowers: 0,
    numParticipants: 0,
    numOrganisers:0,
  }});

apiActivity.getActivityUpdates.mockResolvedValue([]);

apiActivity.getParticipants.mockResolvedValue({
  data: {
    content: [
      {
        profile_id: 2,
        firstname: "Mayuko",
        lastname: "Williams",
      },
      {
        profile_id: 3,
        firstname: "Shivin",
        lastname: "Gaba",
      },
      {
        profile_id: 4,
        firstname: "Frankie",
        lastname: "Oprenario",
      },
      {
        profile_id: 5,
        firstname: "Fabian",
        lastname: "Gilson",
      },
    ],
    pageable: "INSTANCE",
    last: true,
    totalPages: 1,
    totalElements: 0,
    size: 0,
    number: 0,
    sort: {
      sorted: false,
      unsorted: true,
      empty: true,
    },
    first: true,
    numberOfElements: 0,
    empty: true,
  },
});

apiActivity.getOrganisers.mockResolvedValue({
  data: {
    content: [
      {
        profile_id: 2,
        firstname: "Mayuko",
        lastname: "Williams",
      },
      {
        profile_id: 2,
        firstname: "Mayuko",
        lastname: "Williams",
      },
      {
        profile_id: 2,
        firstname: "Mayuko",
        lastname: "Williams",
      },
      {
        profile_id: 2,
        firstname: "Mayuko",
        lastname: "Williams",
      },
    ],
    pageable: "INSTANCE",
    last: true,
    totalPages: 1,
    totalElements: 0,
    size: 0,
    number: 0,
    sort: {
      sorted: false,
      unsorted: true,
      empty: true,
    },
    first: true,
    numberOfElements: 0,
    empty: true,
  },
});

// you are not following atm
apiUser.isUserFollowingActivitiy.mockResolvedValue(false);

apiActivity.deleteActivity.mockResolvedValue(
  {
    data:[]
  }
)

apiUser.getUserContinuousActivities.mockResolvedValue(
  {
    data:[]
  }
)

apiUser.getUserDurationActivities.mockResolvedValue(
  {
    data:[]
  }
)

// test to check what is on the activity page if you are the author of this activity
describe("test if you are author of the activity", () => {
  let store;
  let wrapper;
  let vuetify;

  // getters in Vuex
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
      activity_types: ["relaxing"],
      activity_id: null,
      visibility: "public",
    }),
  };

  beforeEach(() => {
    vuetify = new Vuetify();
    store = new Vuex.Store({
      //defined at let getters above
      getters,
      actions: {
        // methods from vuex actions
        updateUserContinuousActivities: jest.fn(),
        updateUserDurationActivities: jest.fn(),
        getActivityUpdates: jest.fn(),
        isUserFollowingActivitiy: jest.fn(),
      },
    });
    // create environment (called wrapper) of where you are doing the test (Activity.vue with vuex store,
    // vue environment, mocks, and stubs for methods you don't need.
    wrapper = mount(Activity, { store, localVue, mocks, stubs, vuetify });
  });

  it("should have delete button on the page if you are the author", async () => {
    await flushPromises();
    expect(wrapper.find("#activityPageInfoDeleteButton").exists()).toBe(true);
  });

  it("should be able to delete the activity by clicking the delete button", async () => {
    await flushPromises();
    await wrapper.find("#activityPageInfoDeleteButton").trigger("click");
    await flushPromises();
    expect(apiActivity.deleteActivity).toBeCalledWith(2, 99);
  });
});

describe("test if you are not the author of this activity", () => {
  let store;
  let wrapper;
  let vuetify;

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
    vuetify = new Vuetify();
    store = new Vuex.Store({
      getters,
      actions: {
        updateUserContinuousActivities: jest.fn(),
        updateUserDurationActivities: jest.fn(),
      },
    });
    wrapper = mount(Activity, { store, localVue, mocks, stubs, vuetify  });
  });

  it("should not have delete button on the profile page", async () => {
    await flushPromises();
    expect(wrapper.find("#activityPageInfoDeleteButton").exists()).toBe(false);
  });

  // it('should have an add participants button', async() => {
  //   await flushPromises();
  //   expect(wrapper.find('.activityPageCardButton').exists().toBe(true))
  // })
});

describe("test if an activity has restricted visibility", () => {
  let store;
  let wrapper;
  let vuetify;

  let getters = {
    user: () => ({
      firstname: "John",
      lastname: "Doe",
      middlename: "James",
      nickname: "Jimmy",
      gender: "Male",
      primary_email: "john@uclive.ac.nz",
      additional_email: [],
      date_of_birth: "1985-01-11",
      bio: "Testing officer",
      isLogin: true,
      fitness: 3,
      profile_id: 5000,
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
      author_id: 5000,
      name: null,
      continuous: null,
      start_time: null,
      end_time: null,
      description: null,
      location: null,
      activity_types: [],
      activity_id: 1000,
      visibility: 1,
      emailsToAdd: "test@mail.com",
    }),
  };

  beforeEach(() => {
    vuetify = new Vuetify();
    store = new Vuex.Store({
      getters,
      actions: {
        updateUserContinuousActivities: jest.fn(),
        updateUserDurationActivities: jest.fn(),
      },
    });
    wrapper = mount(Activity, { store, localVue, mocks, stubs, vuetify });
  });

  it("should have an add participants page section", async () => {
    await flushPromises();
    expect(wrapper.find(".activityPageCard").exists()).toBe(true);
  });

  // it('should have an input field to type emails', async() => {
  //   // await flushPromises();
  //   const wrapper = shallowMount(Activity, { store, localVue, mocks, stubs });
  //   expect(wrapper.find('.activityPageCardTextField').exists()).toBe(true)
  // })

  test("the user provides an invalid email", () => {
    expect(Activity.methods.validateEmail("invalid@email")).toBe(false);
  });

  test("the user provides an valid email", () => {
    expect(Activity.methods.validateEmail("valid@email.com")).toBe(true);
  });

  // test('User is added as an activity participant', () => {
  //   let email = [];
  //   email.push("new_member@mail.com");
  //   return apiActivity.setActivityMembers(email, "follower", 5000, 1000).then(data => {
  //     expect(data.status).toBe(200);
  //   });
  // });
});

describe("test if page shows the participants and organisers of an activity", () => {
  let store;
  let wrapper;
  let vuetify;

  let getters = {
    user: () => ({
      firstname: "John",
      lastname: "Doe",
      middlename: "James",
      nickname: "Jimmy",
      gender: "Male",
      primary_email: "john@uclive.ac.nz",
      additional_email: [],
      date_of_birth: "1985-01-11",
      bio: "Testing officer",
      isLogin: true,
      fitness: 3,
      profile_id: 5000,
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
      numFollowers: 0,
      numOrganisers: 0,
      numParticipants: 0,
      userOpttedIn: false,
    }),

    activity: () => ({
      author_id: 5000,
      name: null,
      continuous: null,
      start_time: null,
      end_time: null,
      description: null,
      location: null,
      activity_types: [],
      activity_id: 1000,
      visibility: 0,
      emailsToAdd: "test@mail.com",
    }),
  };

  beforeEach(() => {
    vuetify = new Vuetify();
    store = new Vuex.Store({
      getters,
      actions: {
        updateUserContinuousActivities: jest.fn(),
        updateUserDurationActivities: jest.fn(),
        getActivityStats: jest.fn(),
      },
    });
    wrapper = mount(Activity, { store, localVue, mocks, stubs, vuetify });
  });

  it("should have a card for displaying the list of participants and organisers", async () => {
    await flushPromises();
    expect(wrapper.find(".activityPageCard").exists()).toBe(true);
  });

  it("shouldn't have button if user is not already opted in", async () => {
    await flushPromises();
    expect(wrapper.find(".optoutButton").exists()).toBe(false);
  });


  it("should have a div for displaying the number of followers", async () => {
    await flushPromises();
    expect(wrapper.find(".activityPageCard").exists()).toBe(true);
  });

  it("should have the preview tabs to switch between the top participants and organisers", async () => {
    await flushPromises();
    expect(wrapper.find("#previewParticipantsOrganisersTabs").exists()).toBe(true);
  });

  it("should have a show more button to view more participants and organisers for the activity", async() => {
    await flushPromises();
    expect(wrapper.find("#activityPageShowMoreButton").exists()).toBe(true);
  });

  it("should have a dialog box appear the displays more users when Show More button is clicked", async() => {
    await flushPromises();
    wrapper.find("#activityPageShowMoreButton").trigger('click');
    expect(wrapper.find("#activityPageMoreParticipantsOrganisersDialog").exists()).toBe(true);
  });

  it("should display a preview of the list of participants", async() => {
    await flushPromises();
    expect(wrapper.find("#participantOrganiserList").text()).toContain("Mayuko Williams");
    expect(wrapper.find("#participantOrganiserList").text()).toContain("Shivin Gaba");
    expect(wrapper.find("#participantOrganiserList").text()).toContain("Frankie Oprenario");
  })
});
