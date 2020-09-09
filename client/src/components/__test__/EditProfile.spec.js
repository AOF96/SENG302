/* eslint-env jest*/
import Vuex from "vuex";
// import Profile from "../profile/Profile";
import {createLocalVue, shallowMount} from "@vue/test-utils";
import ProfileInfoSettings from "../profile/settings/ProfileInfoSettings";
import ProfileSettingsMenu from "../profile/settings/ProfileSettingsMenu";
import {expect} from "@jest/globals";
import ProfileLocationSettings from "../profile/settings/ProfileLocationSettings";
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

describe("Check user's edit profile page", () => {
    let actions = {
        getUserById: jest.fn(),
        getUserContinuousActivities: jest.fn(),
        getUserDurationActivities: jest.fn(),
        updateUserProfile: jest.fn(),
        getDataFromUrl: jest.fn(),
    };

    let store;
    let wrapper;

    beforeEach(() => {
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
        actions.updateUserProfile.mockResolvedValue({data: []});

        actions.getDataFromUrl.mockResolvedValue({
            data: [{
                name: "Afghanistan",
                alpha3Code: "AFG"
            }],
        });
        wrapper = shallowMount(ProfileInfoSettings, {
            localVue,
            store,
            mocks,
            stubs
        })
    });

    it("There should be an update profile button on the edit profile page", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#profileUpdateButton").exists()).toBe(true);
    });

    it("There should be an back to  profile button on the edit profile page", async () => {
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#backToProfileButton").exists()).toBe(true);
    });

    it("should have an input field with the user's first name ", () => {
        expect(wrapper.find("#firstName").exists()).toBe(true);
    });

    it("should have an input field with the user's middle name ", () => {
        expect(wrapper.find("#middleName").exists()).toBe(true);
    });

    it("should have an input field with the user's last name ", () => {
        expect(wrapper.find("#lastName").exists()).toBe(true);
    });

    it("should have an input field with the user's nick name ", () => {
        expect(wrapper.find("#nickName").exists()).toBe(true);
    });

    it("should have a drop down field for user to set their gender", () => {
        expect(wrapper.find("#userGender").exists()).toBe(true);
    });

    it("should have a drop down field for user to set fitness level", () => {
        expect(wrapper.find("#userFitnessLevel").exists()).toBe(true);
    });

    it("should have a calender drop down field for user to set their date of birthday", () => {
        expect(wrapper.find("#userBirthday").exists()).toBe(true);
    });

    it("should have a input text field for user to set a bio for their profile", () => {
        expect(wrapper.find("#userBio").exists()).toBe(true);
    });

    it("should have a input text field for user to set a bio for their profile", () => {
        expect(wrapper.find("#userBio").exists()).toBe(true);
    });

    it("should have a quick navigation Settings info panel to navigate to different setting pages ", () => {
        const wrapper = shallowMount(ProfileSettingsMenu, { store, localVue, mocks, stubs });
        expect(wrapper.find("#settingContainer").exists()).toBe(true);
    });

    it('A link to the Email settings page exists in the navigation panel ', async () => {
        const wrapper = shallowMount(ProfileSettingsMenu, { store, localVue, mocks, stubs });
        expect(wrapper.find("#navigateToEmailSettings").exists()).toBe(true);
    });
    it('A link to the Activity settings page exists in the navigation panel ', async () => {
        const wrapper = shallowMount(ProfileSettingsMenu, { store, localVue, mocks, stubs });
        expect(wrapper.find("#navigateToActivityTypesSettings").exists()).toBe(true);
    });
    it('A link to the Passport settings page exists in the navigation panel ', async () => {
        const wrapper = shallowMount(ProfileSettingsMenu, { store, localVue, mocks, stubs });
        expect(wrapper.find("#navigateToPassportSettings").exists()).toBe(true);
    });
    it('A link to the Password settings page exists in the navigation panel ', async () => {
        const wrapper = shallowMount(ProfileSettingsMenu, { store, localVue, mocks, stubs });
        expect(wrapper.find("#navigateToPasswordSettings").exists()).toBe(true);
    });

    it("should have an input field for user to set their current city", () => {
        const wrapper = shallowMount(ProfileLocationSettings, { store, localVue, mocks, stubs });
        expect(wrapper.find("#inputCity").exists()).toBe(true);
    });

    it("should have an input field for user to set their current state", () => {
        const wrapper = shallowMount(ProfileLocationSettings, { store, localVue, mocks, stubs });
        expect(wrapper.find("#inputState").exists()).toBe(true);
    });

    it("should have an input field for user to set their current country", () => {
        const wrapper = shallowMount(ProfileLocationSettings, { store, localVue, mocks, stubs });
        expect(wrapper.find("#inputCountry").exists()).toBe(true);
    });

    it("should have a map for the user to set their location", () => {
        const wrapper = shallowMount(ProfileLocationSettings, { store, localVue, mocks, stubs });
        expect(wrapper.find("#userSettingsMap").exists()).toBe(true);
    });
});