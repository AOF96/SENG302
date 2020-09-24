<template>
<div>
  <div id="profileBanner"></div>
  <div class="profileContainer">
    <div class="leftSidebarContainer">
      <v-card class="profileInfoContainer" style="border-radius: 14px;" :loading="loadingProfileInfo">
        <v-container>
          <h1>About {{ searchedUser.firstname }}</h1>
          <div v-if="!loadingProfileInfo">
            <hr/>
            <div class="profileRow">Gender: {{ searchedUser.gender }}</div>
            <hr/>
            <div class="profileRow">Date of Birth: {{ searchedUser.date_of_birth }}</div>
            <hr/>
            <div class="profileRow">Email: {{ searchedUser.primary_email }}</div>
            <div v-if="searchedUser.bio">
              <hr/>
              <div class="profileRow">Bio: {{ searchedUser.bio }} <hr/> </div>
            </div>
            <div v-if="searchedUser.location.city" class="profileRow">
              City: {{ searchedUser.location.city }}  <hr/> </div>
            <div v-if="searchedUser.location.state" class="profileRow">
              State: {{searchedUser.location.state}} <hr/> </div>
            <div v-if="searchedUser.location.country" class="profileRow">
              Country: {{searchedUser.location.country}} <hr/> </div>

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
                  <span id="userNickname" style="color:grey;" v-if="searchedUser.nickname != null">({{ searchedUser.nickname }})</span>
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
      <v-card :loading="mapLoading" v-if="this.searchedUser.location != null" id="profileMapCard">
        <div id="profileMap"></div>
        <button class="genericConfirmButton profileMapButton" id="profileFullMapButton" type="button" v-on:click="goToFullMap">Full Map</button>
      </v-card>
      <template v-if="this.showPassport">
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
import {apiUser} from "../../api";
import mapStyles from "../../util/mapStyles";

const COUNTRIES_URL = "https://restcountries.eu/rest/v2/all";

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
      mapLoading: true,
      showPassport: false
    };
  },
  props: ['darkModeGlobal'],
  async mounted() {
    if (!this.user.isLogin) {
      this.$router.push('/login');
    } else {
      this.setUserBanner();
      this.loadSearchedUser();
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

    /**
     * Sets the user banner depending on what theme mode is active
     */
    setUserBanner() {
      if (document.getElementById("profileBanner") !== null) {
        if (this.darkModeGlobal) {
          document.getElementById("profileBanner").classList.remove("profileBanner");
          document.getElementById("profileBanner").classList.add("profileBannerDark");
        } else {
          document.getElementById("profileBanner").classList.remove("profileBannerDark");
          document.getElementById("profileBanner").classList.add("profileBanner");
        }
      }
    },

    /**
     * Creates an event handler to check if the theme has changed
     */
    setThemeCheckEvent(map) {
      let outer = this;
      document.addEventListener("click", function(){
        outer.setUserBanner();
        map.setOptions({
          styles: mapStyles[outer.darkModeGlobal ? "dark" : "light"]
        });
      });
    },

    /**
     * Uses user id from url to request user data.
     */
    async loadSearchedUser() {
      if (this.user.permission_level === 2 && this.user.profile_id === this.$route.params.profileId) {
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
      if(this.searchedUser.location != null && this.searchedUser.location.latitude !== ""){
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

        map.setOptions({
          styles: mapStyles[this.darkModeGlobal ? "dark" : "light"]
        });

        this.setThemeCheckEvent(map);
        this.positionMap(map, position);
      }
    },

    /**
     * Positions map from location depending on if logged in user or searching
     */
    positionMap(map, position) {
      let outer = this;
      if (this.user.profile_id !== this.searchedUser.profile_id) {
        this.geocoder.geocode({'address': this.searchedUser.location.city + ' ' + this.searchedUser.location.country}, function(results, status) {
          if (status === 'OK') {
            outer.setLocationMarker(map, results[0].geometry.location);
            map.setCenter(results[0].geometry.location);
          }
        });
      } else {
        this.setLocationMarker(map, position);
      }
    },

    /**
     * Sets location marker at position given
     */
    setLocationMarker(map, position) {
      let homeIcon = {
        url: "https://i.imgur.com/mNfVgmC.png",
        scaledSize: new window.google.maps.Size(20, 20),
        origin: new window.google.maps.Point(0, 0),
        anchor: new window.google.maps.Point(10, 10)
      };

      new window.google.maps.Marker({
        map: map,
        position: position,
        icon: homeIcon
      });
      this.mapLoading = false;
    },

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

    /**
     * Loads country information and map
     */
    startUp() {
      this.searchedUser.passports = this.searchedUser.passports.slice();
      if(this.searchedUser.passports.length > 0){
        this.showPassport = true;
      }
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
          this.loadMap();
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
    },

    /**
     * Routes the user the Map page, adding the coordinates to the URL if the user is not the searchedUser.
     */
    goToFullMap()  {
      if (this.user.profile_id === this.searchedUser.profile_id) {
        this.$router.push('/map/');
      } else {
        let outer = this;
        this.geocoder.geocode({'address': this.searchedUser.location.city + ' ' + this.searchedUser.location.country}, function(results, status) {
          if (status === 'OK') {
            outer.$router.push('/map/user@' + results[0].geometry.location.lat() + ',' + results[0].geometry.location.lng());
          }
        });
      }
    }
  }
};
</script>

<style scoped>
@import "../../../public/styles/pages/profileStyle.css";
</style>
