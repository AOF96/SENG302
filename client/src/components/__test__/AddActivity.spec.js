/* eslint-env jest*/
import Vuex from "vuex";
// import Profile from "../profile/Profile";
import ActivitySettingsPage from "@/components/activity/settings/ActivitySettings";
import {createLocalVue, mount} from "@vue/test-utils";
import {apiUser} from "@/api";
// creates Vue object (whole page)
const localVue = createLocalVue();
import Vuetify from "vuetify";
localVue.use(Vuetify)
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

apiUser
    .getActivityTypes.mockResolvedValue(
        ["Adventurous","Extreme","Fun","Relaxing","Team-Sport"]
    );


describe("Check user's edit profile page", () => {
    let actions = {
        getUserById: jest.fn(),
        updateUserProfile: jest.fn(),
        getDataFromUrl: jest.fn(),
        getActivityTypes: jest.fn()
    };

    let store;
    let wrapper;
    let vuetify;

    beforeEach(() => {
        vuetify = new Vuetify();
        store = new Vuex.Store({
            getters: {
                userSearch: () => ({
                    searchTerm: null,
                }),
                user: () => ({
                    primary_email: "test@test.com",
                    password: "anything",
                    permission_level: 0,
                    isLogin: true,
                }),
                isAdmin: () => false,
            },
            actions,
        });
        actions.getUserById.mockResolvedValue({
            firstname: "Shivin",
            lastname: "Gaba",
            middlename: "hi",
            nickname: "hi",
            gender: "Male",
            primary_email: "sga113@uclive.ac.nz",
            additional_email: [],
            date_of_birth: "1992-12-26",
            bio: "hahah",
            city: "Delhi",
            country: "India",
            isLogin: true,
            fitness: 1,
            profile_id: 2,
            password: "Water123",
            passports: ["India"],
            tmp_passports: [],
            permission_level: 1,
            activities: [],
            tmp_activities: [],
            cont_activities: [
                {
                    name: "Go to disneyland",
                    description: "Dont go on the Space Mountain, will make you vomit.",
                    id: "1001",
                },
            ],
            dur_activities: [
                {
                    name: "Create shortcut",
                    description: "You go to nano bashrc first",
                    id: "4",
                },
            ],
            location: {city: null, state: null, county: null},
        });

        actions.getDataFromUrl.mockResolvedValue({
            data: [{
                name: "Afghanistan",
                alpha3Code: "AFG"
            }],
        });
        wrapper = mount(ActivitySettingsPage, {
            store,
            stubs,
            vuetify,
            mocks
        })
    });

    it("There should be an activity name label on the add activity page", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#ActivityNameLabel").exists()).toBe(true);
    });
    it("There should be an activity input box to enter activity's name", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#name").exists()).toBe(true);
    });
    it("There should be a visibility label on the activity page", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#VisibilityLabel").exists()).toBe(true);
    });
    it("There should be a public visibility radio button on the activity page", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#publicVisibility").exists()).toBe(true);
    });
    it("There should be a restricted visibility radio button on the activity page", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#restrictedVisibility").exists()).toBe(true);
    });
    it("There should be a private visibility radio button on the activity page", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#privateVisibility").exists()).toBe(true);
    });
    it("There should be a description label on add Activity page", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#DescriptionLabel").exists()).toBe(true);
    });
    it("There should be a text box to write description for an activity", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#desc").exists()).toBe(true);
    });
    it("There should be an activity type label on the add activity page", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#ActivityTypeLabel").exists()).toBe(true);
    });
    it("There should be a back arrow button to go back to the previous part of the form on activity page", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#ActivityTypeOptions").exists()).toBe(true);
    });
    it("There should be a next arrow button to go to the next part of the form on activity page", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#NextArrow").exists()).toBe(true);
    });
    it("There should be a Basic Info tab for the activity on the activity page", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#BasicInfoTab").exists()).toBe(true);
    });
    it("There should be a TIme and date tab to set time for continuous or duration activity", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#TimeDateTab").exists()).toBe(true);
    });
    it("There should be a location tab to select the location for your activity", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#LocationTab").exists()).toBe(true);
    });
    it("There should be an achievement tab to create achievement for the activity ", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#AchievementTab").exists()).toBe(true);
    });
    it("There should be a start date label on the activity page ", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#startDateLabel").exists()).toBe(true);
    });
    it("There should be a end date label on the activity page ", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#endDateLabel").exists()).toBe(true);
    });
    it("There should be a end date label on the activity page ", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#startTimeLabel").exists()).toBe(true);
    });
    it("There should be a end date label on the activity page ", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#endTimeLabel").exists()).toBe(true);
    });
    it("There should be an input field to set the location for the activity", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#activityLocationAutocomplete").exists()).toBe(true);
    });
    it("There should be an add achievement button on the achievement tab", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#addAchievementLabel").exists()).toBe(true);
    });
    it("There should be an input field to enter achievement title", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#achievementTitle").exists()).toBe(true);
    });
    it("There should be an text box add enter achievement's description", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#achievementDescription").exists()).toBe(true);
    });
    it("There should be a drop down menu to select the type of achievement ", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#achieveType").exists()).toBe(true);
    })
});