<template>
<div>
  <router-link v-if="userSearch.searchTerm!==null" v-bind:to="'/search'">
    <button class="genericConfirmButton profileBackToSearchButton">
      Back to Search
    </button>
  </router-link>
  <div class="profileBanner"></div>
  <div class="profileContainer">
    <div class="leftSidebarContainer">
      <v-card class="profileInfoContainer" style="border-radius: 14px;" :loading="loadingProfileInfo">
        <v-container>
          <h3>Profile Info</h3>
          <div v-if="!loadingProfileInfo">
            <hr />
            <div class="profileRow">Gender: {{ searchedUser.gender }}</div>
            <hr />
            <div class="profileRow">Date of Birth: {{ searchedUser.date_of_birth }}</div>
            <hr />
            <div class="profileRow">Email: {{ searchedUser.primary_email }}</div>
            <hr />
            <div class="profileRow">Bio: {{ searchedUser.bio }}</div>
            <hr />
            <div class="profileRow">City: {{ searchedUser.city }}</div>
            <hr />
            <div v-if="searchedUser.state">
              <div class="profileRow">State: {{ searchedUser.state }}</div>
              <hr />
            </div>
            <div class="profileRow">Country: {{ searchedUser.country }}</div>
          </div>
        </v-container>
      </v-card>
      <button id="profileAdminRightsButton" class="adminRightsButton" v-if="user.permission_level > 0 && searchedUser.permission_level === 0 && showAdminButton" v-on:click="grantAdminRights">Make admin</button>
      <h6 v-show="showResult" class="adminRightsResult">{{adminRightsResult}}</h6>
    </div>
    <div class="centreContainer">
      <v-card class="profileHeaderContainer" style="border-radius: 14px;" :loading="loadingProfileInfo">
        <v-container>
          <v-row align="center" justify="center" class="mx-0">
            <v-col cols="auto">
              <svg class="profileMainInfoIcon" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
                <path d="M0 0h24v24H0V0z" fill="none" />
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v1c0 .55.45 1 1 1h14c.55 0 1-.45 1-1v-1c0-2.66-5.33-4-8-4z" />
              </svg>
            </v-col>
            <v-col class="userNameInfoRow">
              <div class="profileMainInfoContainer">
                <h1>
                  {{ searchedUser.firstname }} {{searchedUser.middlename}} {{ searchedUser.lastname }}
                  <span id="userNickname" v-if="searchedUser.nickname != null">({{ searchedUser.nickname }})</span>
                </h1>
                <h2>Fitness Level: {{ fitnessDict[searchedUser.fitness] }}</h2>
              </div>
            </v-col>
            <v-col cols="auto">
              <div v-if="user.permission_level > 0 || user.profile_id === searchedUser.profile_id">
                <router-link v-bind:to="'/settings/profile/' + searchedUser.profile_id">
                  <button class="genericConfirmButton" id="editProfileButton">Edit Profile</button>
                </router-link>
              </div>
            </v-col>
          </v-row>
        </v-container>
      </v-card>
      <div class="profileActivitiesContainer">
        <div v-if="user.permission_level > 0 || user.profile_id === searchedUser.profile_id">
          <router-link v-bind:to="'/activity_settings/' + searchedUser.profile_id">
            <button class="genericConfirmButton profileAddActivityButton" id="addActivityButton" type="button">Add Activity</button>
          </router-link>
        </div>
        <h2>Activities</h2>
        <h3>Activity Types</h3>
        <ul class="activityTypesList">
          <li v-for="(type, idx) in searchedUser.activities" v-bind:key="idx">{{type}}</li>
        </ul>
        <hr class="profileActivitySeparator" />
        <h3>Duration Activities</h3>
        <v-row v-if="loadingDurationActivities" justify="center">
          <v-col cols="6">
            <v-progress-linear v-if="loadingDurationActivities" indeterminate rounded>
            </v-progress-linear>
          </v-col>
        </v-row>
        <div class="activitySummaryContainer" v-for="(activity, idx) in dur_activities" v-bind:key="idx">
          <div class="activityTextWrapDiv">
            <router-link v-bind:to="'/activity/' + activity.id"><a class="profileActivityTitle">{{activity.name}}</a></router-link>
            <h4 class="profileActivityDescription">{{activity.description}}</h4>
          </div>
          <div v-if="user.permission_level > 0 || user.profile_id === searchedUser.profile_id">
            <router-link v-bind:to="'/activity_editing/' + activity.id">
              <button class="genericConfirmButton profileActivityConfirmButton" type="button">Edit Activity</button>
            </router-link>
          </div>
        </div>
        <h4 v-if="dur_activities.length === 0 && !loadingDurationActivities">None</h4>
        <hr class="profileActivitySeparator" />
        <h3>Continuous Activities</h3>
        <v-row v-if="loadingContinuousActivities" justify="center">
          <v-col cols="6">
            <v-progress-linear v-if="loadingContinuousActivities" indeterminate rounded>
            </v-progress-linear>
          </v-col>
        </v-row>
        <div class="activitySummaryContainer" v-for="(activity, idx) in cont_activities" v-bind:key="idx">
          <div class="activityTextWrapDiv">
            <router-link v-bind:to="'/activity/' + activity.id"><a class="profileActivityTitle">{{activity.name}}</a></router-link>
            <h4 class="profileActivityDescription">{{activity.description}}</h4>
          </div>
          <div v-if="user.permission_level > 0 || user.profile_id === searchedUser.profile_id">
            <router-link v-bind:to="'/activity_editing/' + activity.id">
              <button class="genericConfirmButton profileActivityConfirmButton" type="button">Edit Activity</button>
            </router-link>
          </div>
        </div>
        <h4 v-if="cont_activities.length === 0 && !loadingContinuousActivities">None</h4>
      </div>
    </div>
    <div class="rightSidebarContainer">
      <v-card v-if="this.searchedUser.location != null" id="profileMapCard">
        <div id="profileMap"></div>
      </v-card>
      <template v-if="searchedUser.passports">
        <PassportCountries :passports="searchedUser.passports" :key="componentKey" />
      </template>
    </div>
    <div class="floatClear"></div>
  </div>
</div>
</template>

<script>
import {
  mapActions,
  mapGetters,
  mapState
} from "vuex";

import PassportCountries from "../modules/PassportCountries";
import json from "../../../public/json/data.json";
const COUNTRIES_URL = "https://restcountries.eu/rest/v2/all";
import {
  apiUser
} from "../../api";

export default {
  name: "Profile",
  components: {
    PassportCountries
  },
  computed: {
    ...mapState(["user", "userSearch"]),
    ...mapGetters(["user", "userSearch"])
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
      },
      adminRightsResult: "",
      showResult: false,
      showAdminButton: true,
      loadingProfileInfo: true,
      loadingDurationActivities: true,
      loadingContinuousActivities: true,
    };
  },
  async mounted() {
    if (!this.user.isLogin) {
      this.$router.push('/login');
    } else {
      await this.loadSearchedUser();
      this.loadMap();
    }
  },
  watch: {
    "$route.params": {
      handler() {
        this.loadSearchedUser();
      }
    }
  },
  methods: {
    ...mapActions(["updatePassports", "createActivity", "updateUserDurationActivities", "updateUserContinuousActivities", "getUserById", "getUserContinuousActivities", "getUserDurationActivities", "getDataFromUrl"]),

    /*
          Uses user id from url to request user data.
       */
    async loadSearchedUser() {
      if (this.user.permission_level == 2 && this.user.profile_id == this.$route.params.profileId) {
        this.$router.push('/settings/admin_dashboard');
      } else if (
        this.$route.params.profileId === null ||
        this.$route.params.profileId === ""
      ) {
        this.$router.push("/profile/" + this.user.profile_id);
        this.searchedUser = this.user;
        this.replaceDashesWithSpaces(this.searchedUser.activities);
      } else {
        var tempUserData = await this.getUserById(this.$route.params.profileId);
        if (tempUserData === "Invalid permissions") {
          this.$router.push("/profile/" + this.user.profile_id);
          this.searchedUser = this.user;
          this.replaceDashesWithSpaces(this.searchedUser.activities);
        } else {
          this.searchedUser = tempUserData;
          this.replaceDashesWithSpaces(this.searchedUser.activities);
        }
        this.loadingProfileInfo = false;
        await this.getUserContinuousActivities(this.$route.params.profileId)
          .then(response => {
            this.cont_activities = response.data;
            this.loadingContinuousActivities = false;
          });
        await this.getUserDurationActivities(this.$route.params.profileId)
          .then(response => {
            this.dur_activities = response.data;
            this.loadingDurationActivities = false;
          });
      }
      this.startUp();
      this.componentKey++;
    },

    /**
     * Loads the map onto the page and centres on the users home city.
     * Adds a marker on the city's centre.
     */
    loadMap() {
      if(this.searchedUser.location != null && this.searchedUser.location.lat != ""){
        if (!window.google) {
          return;
        }
        this.geocoder = new window.google.maps.Geocoder();

        let position = new window.google.maps.LatLng(this.searchedUser.location.latitude, this.searchedUser.location.longitude);

        let map = new window.google.maps.Map(document.getElementById("profileMap"), {
          center: position,
          zoom: 8,
          maxZoom: 10,
          minZoom: 3,
          disableDefaultUI: true
        });

      if (this.searchedUser.location) {
        //Use me once the address is available in the user object
        let address = this.searchedUser.location.street_address;

        let latLng = new window.google.maps.LatLng(this.searchedUser.location.latitude, this.searchedUser.location.longitude);

        this.geocoder.geocode({'address': address}, function (results, status) {
          if (status === 'OK') {
            map.setCenter(latLng);
            new window.google.maps.Marker({
              map: map,
              position: position
            });
          } else {
            this.snackbarText = status;
            this.snackbarColour = "error";
            this.snackbar = true;
          }
        });
      }

    /**
     * Returns a formatted address string from the location object
     */
    getAddressString(locationObj) {
      let address = "";
      if (locationObj.street_address !== "") {
        address += locationObj.street_address
      }
      if (locationObj.suburb !== "") {
        if (address !== "") {
          address += ", "
        }
        address += locationObj.suburb;
      }
      if (locationObj.city !== "") {
        if (address !== "") {
          address += ", "
        }
        address += locationObj.city;
      }
      if (locationObj.state !== "") {
        if (address !== "") {
          address += ", "
        }
        address += locationObj.state;
      }
      if (locationObj.country !== "") {
        if (address !== "") {
          address += ", "
        }
        address += locationObj.country;
      }
      return address;
    },

    /**
     * Takes a list of strings and replaces all dashes with spaces
     */
    replaceDashesWithSpaces(list) {
      for (let i = 0; i < list.length; i++) {
        list[i] = list[i].replace(/-/g, " ")
      }
    },

    startUp() {
      this.searchedUser.passports = this.searchedUser.passports.slice();
      this.getDataFromUrl(COUNTRIES_URL)
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
    /***
     * Makes a request to the server to give the searched user admin rights given the user is not already an admin and
     * the requesting user is already an admin.
     */
    grantAdminRights() {
      if (this.user.permission_level === 0 || this.searchedUser.permission_level > 0) {
        this.adminRightsResult = "This user is already an admin or you don't have the rights to perform this.";
      } else {
        apiUser.promoteToAdmin(this.searchedUser.profile_id)
          .then(response => {
            this.adminRightsResult = response.data;
            this.showAdminButton = false;
          })
          .catch((error) => {
            this.adminRightsResult = error.response.data.Errors;
          })
      }
      this.showResult = true;
      setTimeout(() => this.showResult = false, 5000)
    }
  }
};
</script>

<style scoped>
@import "../../../public/styles/pages/profileStyle.css";
</style>
