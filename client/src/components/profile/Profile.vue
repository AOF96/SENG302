<template>
  <div>
    <NavBar />
    <div class="profileBanner"></div>
    <div class="profileContainer">
      <div class="leftSidebarContainer">
        <div class="profileInfoContainer">
          <h3>Profile Info</h3>
          <hr />
          <div class="profileRow">Gender: {{ searchedUser.gender }}</div>
          <hr />
          <div class="profileRow">Date of Birth: {{ searchedUser.date_of_birth }}</div>
          <hr />
          <div class="profileRow">Email: {{ searchedUser.primary_email }}</div>
          <hr />
          <div class="profileRow">Bio: {{ searchedUser.bio }}</div>
        </div>
      </div>
      <div class="centreContainer">
        <div class="profileHeaderContainer">
          <svg
            class="profileMainInfoIcon"
            xmlns="http://www.w3.org/2000/svg"
            height="24"
            viewBox="0 0 24 24"
            width="24"
          >
            <path d="M0 0h24v24H0V0z" fill="none" />
            <path
              d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v1c0 .55.45 1 1 1h14c.55 0 1-.45 1-1v-1c0-2.66-5.33-4-8-4z"
            />
          </svg>
          <div class="profileMainInfoContainer">
            <h1>
              {{ searchedUser.firstname }} {{searchedUser.middlename}} {{ searchedUser.lastname }}
              <span
                id="userNickname"
              >({{ searchedUser.nickname }})</span>
            </h1>
            <h2>Fitness Level: {{ fitnessDict[searchedUser.fitness] }}</h2>
          </div>
          <router-link v-bind:to="'/settings/profile/' + searchedUser.profile_id">
            <button class="genericConfirmButton">Edit Profile</button>
          </router-link>
          <div class="floatClear"></div>
        </div>
        <div class="profileActivitiesContainer">
          <router-link v-bind:to="'/activity_settings/' + searchedUser.profile_id">
            <button class="genericConfirmButton" type="button" onclick>Add Activity</button>
          </router-link>
          <h2>Activities</h2>
          <h3>Activity Types</h3>
          <ul class="activityTypesList">
            <li v-for="type in searchedUser.activities" v-bind:key="type">{{type}}</li>
          </ul>
          <hr class="profileActivitySeparator" />
          <h3>Duration Activities</h3>
          <div
            class="activitySummaryContainer"
            v-for="activity in dur_activities"
            v-bind:key="activity"
          >
            <div class="activityTextWrapDiv">
              <router-link to="activity/:activityId">
                <a
                  class="profileActivityTitle"
                  v-on:click="goToActivity(activity.id)"
                >{{activity.name}}</a>
              </router-link>
              <h4 class="profileActivityDescription">{{activity.description}}</h4>
            </div>
            <router-link v-bind:to="'/activity_editing/' + activity.id">
              <button
                class="genericConfirmButton profileActivityConfirmButton"
                type="button"
              >Edit Activity</button>
            </router-link>
          </div>
          <hr class="profileActivitySeparator" />
          <h3>Continuous Activities</h3>
          <div
            class="activitySummaryContainer"
            v-for="activity in cont_activities"
            v-bind:key="activity"
          >
            <div class="activityTextWrapDiv">
              <router-link to="activity/:activityId">
                <a
                  class="profileActivityTitle"
                  v-on:click="goToActivity(activity.id)"
                >{{activity.name}}</a>
              </router-link>
              <h4 class="profileActivityDescription">{{activity.description}}</h4>
            </div>
            <router-link v-bind:to="'/activity_editing/' + activity.id">
              <button
                class="genericConfirmButton profileActivityConfirmButton"
                type="button"
              >Edit Activity</button>
            </router-link>
          </div>
        </div>
      </div>
      <div class="rightSidebarContainer">
        <template v-if="searchedUser.passports">
          <PassportCountries :passports="searchedUser.passports" :key="componentKey"></PassportCountries>
        </template>
      </div>
      <div class="floatClear"></div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters, mapState } from "vuex";

import NavBar from "../modules/NavBar";
import PassportCountries from "../modules/PassportCountries";
import json from "../../../public/json/data.json";
import axios from "axios";
const COUNTRIES_URL = "https://restcountries.eu/rest/v2/all";
import { apiUser, apiActivity } from "../../api";
import router from "../../router";

export default {
  name: "Profile",
  components: {
    NavBar,
    PassportCountries
  },
  computed: {
    ...mapState(["user"]),
    ...mapGetters(["user"])
  },
  data: function() {
    return {
      myJson: json,
      showNewButton: false,
      notFull: true,
      textInput: "",
      adding_country: "Passport Countries",
      countries_option: [],
      searchedUser: {},
      cont_activities: [],
      dur_activities: [],
      country: "",
      componentKey: 0,
      fitnessDict: {
        0: "I never exercise",
        1: "I can walk a short distance",
        2: "I can jog a short distance",
        3: "I can run a medium distance",
        4: "I can run a marathon"
      }
    };
  },

  mounted() {
    this.loadSearchedUser();
  },
  watch: {
    "$route.params": {
      handler() {
        this.loadSearchedUser();
      }
    }
  },
  methods: {
    ...mapActions(["updatePassports"]),
    ...mapActions(["createActivity"]),
    ...mapActions(["updateUserDurationActivities"]),
    ...mapActions(["updateUserContinuousActivities"]),

    /*
          Uses user id from url to request user data.
       */
    async loadSearchedUser() {
      if (
        this.$route.params.profileId == null ||
        this.$route.params.profileId == ""
      ) {
        this.$router.push("/profile/" + this.user.profile_id);
        this.searchedUser = this.user;
      } else {
        var tempUserData = await apiUser.getUserById(
          this.$route.params.profileId
        );
        if (tempUserData == "Invalid permissions") {
          this.$router.push("/profile/" + this.user.profile_id);
          this.searchedUser = this.user;
        } else {
          this.searchedUser = tempUserData;
        }
        apiUser
          .getUserContinuousActivities(this.$route.params.profileId)
          .then(response => {
            this.cont_activities = response.data;
          });
        apiUser
          .getUserDurationActivities(this.$route.params.profileId)
          .then(response => {
            this.dur_activities = response.data;
          });
      }
      this.startUp();
      this.componentKey++;
    },
    startUp() {
      this.searchedUser.passports = this.searchedUser.passports.slice();
      axios
        .get(COUNTRIES_URL)
        .then(response => {
          const countries = [];
          const data = response.data;
          for (let country in data) {
            let country_name = data[country].name;
            countries.push(country_name);
          }

          for (let country of this.searchedUser.passports) {
            const index = countries.indexOf(country);
            if (index === -1) continue;
            countries.splice(index, 1);
          }
          this.countries_option = countries;
        })
        .catch(error => console.log(error));
    },
    goToActivity(id) {
      apiActivity
        .getActivity(id)
        .then(response => {
          this.createActivity(response.data);
          router.push("/activity/" + id);
        })
        .catch(err => {
          console.log(err);
        });
    },
    getActivity(id) {
      apiActivity
        .getActivity(id)
        .then(response => {
          this.createActivity(response.data);
        })
        .catch(err => {
          console.log(err);
        });
    },
    deleteDurationActivity(activity) {
      let index = this.dur_activities.indexOf(activity);
      this.dur_activities.splice(index, 1);
      apiActivity.deleteActivity(this.searchedUser.profile_id, activity.id);
      this.updateUserDurationActivities(this.dur_activities);
    },
    deleteContinuousActivity(activity) {
      let index = this.cont_activities.indexOf(activity);
      this.cont_activities.splice(index, 1);
      apiActivity.deleteActivity(this.searchedUser.profile_id, activity.id);
      this.updateUserContinuousActivities(this.cont_activities);
    }
  }
};
</script>

<style scoped>
@import "../../../public/styles/pages/profileStyle.css";
</style>
