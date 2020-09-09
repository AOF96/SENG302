<template>
  <div class="settingsContainer" @click="showLocations = false">
    <v-snackbar v-model="snackbar" top :color="snackbarColour">{{ snackbarText }}</v-snackbar>
    <UserSettingsMenu/>
    <div class="settingsContentContainer">
      <router-link v-bind:to="'/profile/'+this.$route.params.profileId">
        <button class="genericConfirmButton backButton" id="backToProfileButton">Back to Profile</button>
      </router-link>
      <h1 class="settingsTitle">Edit Profile Location</h1>
      <hr/>
      <div id="userSettingsMap"></div>
      <hr/>
      <v-container>
        <v-row justify="center" align="center">
          <v-icon large>mdi-arrow-up-thick</v-icon>
        </v-row>
        <v-row justify="center" align="center"><h4>Select on map or type in the box</h4></v-row>
        <v-row justify="center" align="center">
          <v-icon large>mdi-arrow-down-thick</v-icon>
        </v-row>
      </v-container>
      <div class="locationFieldDiv">
        <v-text-field id="locationInput" v-model="location" class="locationInput" label="Address" outlined dense></v-text-field>
      </div>
      <button class="genericConfirmButton updatePasswordButton" v-on:click="updateProfile()" type="submit">Save
        Location
      </button>
    </div>
  </div>
</template>

<script>
  import UserSettingsMenu from "./ProfileSettingsMenu";
  import {mapActions, mapGetters, mapState} from "vuex";

  export default {
    name: "ProfileLocationSettings",
    components: {
      UserSettingsMenu
    },
    data: function () {
      return {
        searchedUser: {},
        location: "Christchurch",
        snackbar: false,
        snackbarText: null,
        snackbarColour: "primary",
      };
    },
    computed: {
      ...mapState(["user"]),
      ...mapGetters(["user"]),
    },
    /**
     * On start-up, adds a listener to locationInput such that a query is made to Photon when the user stops typing
     * after 1 second. Calls a support function to add a summary key for each of the location objects. Locations with
     * duplicate summaries are removed.
     */
    mounted() {
      this.loadSearchedUser();
      this.loadMap();
    },
    methods: {
      ...mapActions(["logout", "updateUserProfile", "getUserById", "editProfile", "getDataFromUrl"]),
      /**
       * Loads the map onto the page and centres on the users home city.
       * Adds a marker on the city's centre.
       */
      loadMap() {
        if (!window.google) {
          return;
        }
        this.geocoder = new window.google.maps.Geocoder();

        let map = new window.google.maps.Map(document.getElementById("userSettingsMap"), {
          center: {
            lat: -34.397,
            lng: 150.644
          },
          zoom: 9
        });

        let address = this.location;

        this.geocoder.geocode({'address': address}, function (results, status) {
          if (status === 'OK') {
            map.setCenter(results[0].geometry.location);
            new window.google.maps.Marker({
              map: map,
              position: results[0].geometry.location
            });
          } else {
            this.snackbarText = status;
            this.snackbarColour = "error";
            this.snackbar = true;
          }
        });
      },
      /**
       Sends a request to the server side to update the searchedUser's profile info. Displays error messages if the update
       was unsuccessful.
       */
      updateProfile() {
        this.editProfile(
            this.searchedUser
        )
            .then(
                response => {
                  this.updateUserProfile(this.searchedUser);
                  this.snackbarText = response.data;
                  this.snackbarColour = "success";
                  this.snackbar = true;
                },
                error => {
                  this.snackbarText = error.response.data.Errors;
                  this.snackbarColour = "error";
                  this.snackbar = true;
                }
            );
      },
      /**
       * Uses user id from url to request user data.
       */
      async loadSearchedUser() {
        if (
            this.$route.params.profileId == null ||
            this.$route.params.profileId === ""
        ) {
          this.$router.push("/settings/profile/" + this.user.profile_id);
          this.searchedUser = this.user;
        } else {
          let tempUserData = await this.getUserById(this.$route.params.profileId);
          if (tempUserData === "Invalid permissions") {
            this.$router.push("/settings/profile/" + this.user.profile_id);
            this.searchedUser = this.user;
          } else {
            this.searchedUser = tempUserData;
          }
          this.location = this.searchedUser.location.city;
        }
      },
    },
  }
</script>

<style scoped>
  @import "../../../../public/styles/pages/profileSettingsStyle.css";
</style>