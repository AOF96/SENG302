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
        <v-text-field id="locationInput" v-model="address" class="locationInput" label="Address" outlined dense></v-text-field>
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
        snackbar: false,
        snackbarText: null,
        snackbarColour: "primary",
        location: {
          street_address: null,
          suburb: null,
          postcode: null,
          city: null,
          state: null,
          country: null,
          latitude: null,
          longitude: null,
        },
        autocomplete: null,
        address: ""
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
            zoom: 9,
            streetViewControl: false,
            fullscreenControl: false,
            rotateControl: false,
            mapTypeControl: false
        });

        let address = this.address;
        let marker = null;
        let thisInner = this;

        map.addListener('click', function(e) {
          if(marker != null) {
            marker.setMap(null);
          }
          marker = new window.google.maps.Marker({
            position: e.latLng,
            map: map
          });
          map.panTo(e.latLng);
          thisInner.getLocationFromLatLng(e.latLng);
        });

        this.geocoder.geocode({'address': address}, function (results, status) {
          if (status === 'OK') {
            map.setCenter(results[0].geometry.location);
            marker = new window.google.maps.Marker({
              map: map,
              position: results[0].geometry.location
            });
          } else {
            this.snackbarText = status;
            this.snackbarColour = "error";
            this.snackbar = true;
          }
        });

        this.autocomplete = new window.google.maps.places.Autocomplete(
            document.getElementById("locationInput"),
            {
              types: ["address"],
            }
        );

        this.autocomplete.addListener("place_changed", this.fillInAddress);
      },

      /**
       * Fills in address field and location object from address autocomplete
       */
      fillInAddress() {
        this.location = this.extractLocationData(this.autocomplete.getPlace());
        this.updateAddressString();
      },

      /**
       * Extractor function that parses the google maps response and returns a location object.
       */
      extractLocationData(place) {
        let newLocation = {street_address:"",suburb:"",postcode:"",city:"",state:"",country:"",latitude:"",longitude:""};
        let addressComponents = place["address_components"];
        newLocation["latitude"] = place["geometry"]["location"].lat();
        newLocation["longitude"] = place["geometry"]["location"].lng();
        let findingRoute = false;

        for(let i = 0; i < addressComponents.length; i++){
          let content = addressComponents[i]["long_name"];
          if(addressComponents[i]["types"].includes("street_number")){newLocation.street_address = content+" ";findingRoute = true;}
          if(addressComponents[i]["types"].includes("route")){
            if(findingRoute){newLocation.street_address += content}else{newLocation.street_address = content}
          }
          if(addressComponents[i]["types"].includes("sublocality")){newLocation.suburb = content}
          if(addressComponents[i]["types"].includes("locality")){newLocation.city = content}
          if(addressComponents[i]["types"].includes("administrative_area_level_1")){newLocation.state = content}
          if(addressComponents[i]["types"].includes("country")){newLocation.country = content}
          if(addressComponents[i]["types"].includes("postal_code")){newLocation.postcode = content}
        }

        return newLocation;
      },

      /**
       * Calls google maps api with lat lng and updates the location object
       */
      getLocationFromLatLng(latlng) {
        let thisInner = this;
        this.geocoder.geocode({'location': latlng}, function (results, status) {
          if (status === 'OK') {
            if(results.length == 0){
              this.snackbarText = "Invalid Location";
              this.snackbarColour = "error";
              this.snackbar = true;
            }else{
              thisInner.location = thisInner.extractLocationData(results[0]);
              thisInner.updateAddressString();
            }
          } else {
            this.snackbarText = status;
            this.snackbarColour = "error";
            this.snackbar = true;
          }
        });
      },

      /**
       * Updates the address string from the location object
       */
      updateAddressString() {
        this.address = "";
        if(this.location.street_address != ""){this.address += this.location.street_address}
        if(this.location.suburb != ""){
          if(this.address != ""){this.address += ", "}
          this.address += this.location.suburb;
        }
        if(this.location.city != ""){
          if(this.address != ""){this.address += ", "}
          this.address += this.location.city;
        }
        if(this.location.state != ""){
          if(this.address != ""){this.address += ", "}
          this.address += this.location.state;
        }
        if(this.location.country != ""){
          if(this.address != ""){this.address += ", "}
          this.address += this.location.country;
        }
      },

      /**
       * Sets the location and each of the individual components by splitting the comma-separated location. Also resets
       * the location input.
       */
      setLocation(location) {
        this.location = location;
        const l = {
          city: document.getElementById('inputCity').value,
          state: document.getElementById('inputState').value,
          country: document.getElementById('inputCountry').value
        };

        if (l.city.length === 0) {
          l.city = this.searchedUser.city;
        }
        if (l.state.length === 0) {
          l.state = this.searchedUser.state;
        }
        if (l.country.length === 0) {
          l.country = this.searchedUser.country;
        }
        this.searchedUser.location = l;
      },

      /**
       * This method filters the the data received from the api and only suggests cities to the user.
       *
       */
      getLocationCity(location) {
        let city = "Almora";
        if (location.properties.city !== undefined) {
          city = location.properties.city;
          return city
        }
        return city;
      },
      /**
       * This method filters the the data received from the api and only suggests states to the user.
       *
       */
      getLocationState(location) {
        let state = "Angland";
        if (location.properties.state !== undefined) {
          state = location.properties.state;
          return state
        }
        return state;
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
        }
      },
    },
  }
</script>

<style scoped>
  @import "../../../../public/styles/pages/profileSettingsStyle.css";
</style>