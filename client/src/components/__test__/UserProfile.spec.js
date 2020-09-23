/* eslint-env jest*/
import Vuex from "vuex";
import Profile from "../profile/Profile";
import {createLocalVue, mount} from "@vue/test-utils";
import flushPromises from 'flush-promises'
import VueRouter from "vue-router";
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
describe("Check user profile page", () => {
    let actions = {
        getUserById: jest.fn(),
        getUserContinuousActivities: jest.fn(),
        getUserDurationActivities: jest.fn(),
        getDataFromUrl: jest.fn(),
    };

    let store;
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
                    location: {
                        street_address: "300 Somewhere Road",
                        suburb: "Somewhere",
                        city: "Christchurch",
                        postcode: 8000,
                        state: "Canterbury",
                        country: "New Zealand",
                        latitude: 120.47,
                        longitude: -20.12
                    },
                }),
                isAdmin: () => false,
            },
            actions,
        });
        actions.getUserById.mockResolvedValue({
            firstname: "John",
            lastname: "Doe",
            middlename: "hi",
            nickname: "hi",
            gender: "Female",
            primary_email: "jd@12uclive.ac.nz",
            additional_email: [],
            date_of_birth: "1995-01-11",
            bio: "nanana",
            isLogin: true,
            fitness: 1,
            profile_id: 2,
            password: "Water123",
            passports: ["Afghanistan"],
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
            location: {
                street_address: "300 Somewhere Road",
                suburb: "Somewhere",
                city: "Christchurch",
                postcode: 8000,
                state: "Canterbury",
                country: "New Zealand",
                latitude: 120.47,
                longitude: -20.12
            },
        });
        actions.getUserContinuousActivities.mockResolvedValue({data: []});
        actions.getUserDurationActivities.mockResolvedValue({data: []});
        actions.getDataFromUrl.mockResolvedValue({
            data: [{
                name: "Afghanistan",
                alpha3Code: "AFG"
            }],
        });

    });

    it("There should be an edit profile button on the profile page", async () => {
        const wrapper = mount(Profile, {localVue, store, mocks, stubs});
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#editProfileButton").exists()).toBe(true);
    });
    //
    it("There Should be an Add activity button to the Add activity page", async () => {
        const wrapper = mount(Profile, { store, localVue, mocks, stubs });
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#addActivityButton").exists()).toBe(true);
        wrapper.find("#addActivityButton").trigger('click')
    });

    it('should display the Logged in user details in the panel on profile page', () => {
        const wrapper = mount(Profile, { store, localVue, mocks, stubs });
        expect(wrapper.find(".leftSidebarContainer").text()).toContain("Profile Info")
    });

    it('should display the Logged in user passport country details in the panel on the right of the page ', async () => {
        const wrapper = mount(Profile, { store, localVue, mocks, stubs });
        await flushPromises();
        expect(wrapper.findComponent({name: "PassportCountries"}).exists()).toBe(true)
    });

    it("should display a Full Map button.", async () => {
        const wrapper = mount(Profile, { store, localVue, mocks, stubs });
        await flushPromises();
        expect(wrapper.find("#profileFullMapButton").exists()).toBe(true);
    });

    it('should take the user to the full map page with the correct coordinates when the Full Map button is clicked.' +
        '\n +  Passport country pane is not displayed when user does not have any countries selected.', async () => {
        localVue.use(VueRouter);
        const router = new VueRouter();

        store = new Vuex.Store({
            getters: {
                userSearch: () => ({
                    searchTerm: null,
                }),
                user: () => ({
                    profile_id: 2,
                    primary_email: "test@test.com",
                    password: "anything",
                    permission_level: 0,
                    isLogin: true,
                    location: {
                        street_address: "300 Somewhere Road",
                        suburb: "Somewhere",
                        city: "Christchurch",
                        postcode: 8000,
                        state: "Canterbury",
                        country: "New Zealand",
                        latitude: 120.47,
                        longitude: -20.12
                    },
                }),
                isAdmin: () => false,
            },
            actions,
        });
        actions.getUserById.mockResolvedValue({
            firstname: "John",
            lastname: "Doe",
            middlename: "hi",
            nickname: "hi",
            gender: "Female",
            primary_email: "jd@12uclive.ac.nz",
            additional_email: [],
            date_of_birth: "1995-01-11",
            bio: "nanana",
            isLogin: true,
            fitness: 1,
            profile_id: 2,
            password: "Water123",
            passports: [],
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
            location: {
                street_address: "300 Somewhere Road",
                suburb: "Somewhere",
                city: "Christchurch",
                postcode: 8000,
                state: "Canterbury",
                country: "New Zealand",
                latitude: 120.47,
                longitude: -20.12
            },
        });
        actions.getUserContinuousActivities.mockResolvedValue({data: []});
        actions.getUserDurationActivities.mockResolvedValue({data: []});
        actions.getDataFromUrl.mockResolvedValue({
            data: [{
                name: "Afghanistan",
                alpha3Code: "AFG"
            }],
        });

        const wrapper = mount(Profile, {store, localVue, mocks, stubs, router});
        await flushPromises();
        wrapper.find("#profileFullMapButton").trigger('click');
        expect(window.location.href).toBe('http://localhost/#/map/')
    });
});