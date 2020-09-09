<template>
    <div class="settingsContainer" @click="showLocations = false">
        <UserSettingsMenu/>
        <div class="settingsContentContainer">
            <router-link v-bind:to="'/profile/'+this.$route.params.profileId">
                <button class="genericConfirmButton backButton" id="backToProfileButton">Back to Profile</button>
            </router-link>
            <h1 class="settingsTitle">Edit Profile Info</h1>
            <hr/>
            <div id="userSettingsMap"></div>
            <div class="locationFieldDiv">
                <v-combobox
                        v-model="searchedUser.city"
                        :items="items"
                        :search-input.sync="search"
                        color="primary"
                        :loading="isLoading"
                        no-filter
                        hide-no-data
                        item-text="Description"
                        item-value="API"
                        label="City"
                        placeholder="Start typing to Search"
                        return-object
                        id="inputCity"
                        outlined
                        class="locationCombo"
                        autocomplete="new"
                        dense
                />
                <v-combobox
                        v-model="searchedUser.state"
                        :items="itemsState"
                        :search-input.sync="searchState"
                        color="primary"
                        no-filter
                        :loading="stateLoading"
                        hide-no-data
                        hide-selected
                        item-text="Description"
                        item-value="API"
                        label="State"
                        placeholder="Start typing to Search"
                        return-object
                        id="inputState"
                        outlined
                        class="locationCombo"
                        autocomplete="new"
                        dense
                />
                <v-combobox
                        v-model="searchedUser.country"
                        :items="countries_option"
                        color="primary"
                        hide-no-data
                        hide-selected
                        item-text="Description"
                        label="Country"
                        placeholder="Start typing to Search"
                        return-object
                        id="inputCountry"
                        outlined
                        class="locationCombo"
                        autocomplete="new"
                        dense
                />
                <div v-if="showLocations && suggestedLocations.length > 0" class="locationDropdown">
                    <div
                            v-for="(item, index) in suggestedLocations"
                            v-bind:key="index"
                            class="dropdown-content"
                    >
                        <p v-on:click="setLocation(item.summary)">{{item.summary}}</p>
                    </div>
                </div>
            </div>
            <button class="genericConfirmButton updatePasswordButton" v-on:click="updateProfile()" type="submit">Save Location</button>
        </div>
    </div>
</template>

<script>
  import UserSettingsMenu from "./ProfileSettingsMenu";
  import {mapActions, mapGetters, mapState} from "vuex";

  const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all';

  export default {
    name: "ProfileLocationSettings",
    components: {
      UserSettingsMenu
    },
    data: function () {
      return {
        searchedUser: {},
        suggestedLocations: [],
        showLocations: false,
        location: null,
        locationCity: null,
        locationState: null,
        dialog: false,
        isLoading: false,
        stateLoading: false,
        search: null,
        searchState: null,
        model: null,
        locationCountry: null,
        countries_option: [],
        showAdmin: false,
        features: []
      };
    },
    computed: {
      ...mapState(["user"]),
      ...mapGetters(["user"]),

      items () {
        return this.features.map(entry => {
          const Description = this.getLocationCity(entry);
          return Object.assign({}, entry, { Description })
        })
      },
      itemsState () {
        return this.features.map(entry => {
          const Description = this.getLocationState(entry);
          return Object.assign({}, entry, { Description })
        })
      }
    },
    watch: {
      search(val) {
        if (val.length < 3) {
          return
        }
        this.isLoading = true;

        fetch("https://photon.komoot.de/api/?q=" + val)
            .then(res => res.json())
            .then(res => {
              const {features} = res;
              this.features = this.removeNullCities(features)
            })
            .catch(err => {
              console.log(err)
            })
            .finally(() => (this.isLoading = false))
      },
      searchState(val) {

        if (val.length < 3) {
          return
        }
        this.stateLoading = true;

        fetch("https://photon.komoot.de/api/?q=" + val)
            .then(res => res.json())
            .then(res => {
              const {features} = res;
              this.features = this.removeNullState(features)
            })
            .catch(err => {
              console.log(err)
            })
            .finally(() => (this.stateLoading = false))
      }
    },

    /**
     * On start-up, adds a listener to locationInput such that a query is made to Photon when the user stops typing
     * after 1 second. Calls a support function to add a summary key for each of the location objects. Locations with
     * duplicate summaries are removed.
     */
    mounted() {
      this.loadSearchedUser();
      this.loadCountries();
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

        let address = this.user.location.city;

        this.geocoder.geocode({'address': address}, function (results, status) {
          if (status === 'OK') {
            map.setCenter(results[0].geometry.location);
            new window.google.maps.Marker({
              map: map,
              position: results[0].geometry.location
            });
          } else {
            this.errorMessage = status;
            this.snackbar = true;
          }
        });
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
        this.setLocation(location);

        console.log(this.location);

        this.editProfile(
            this.searchedUser
        )
            .then(
                response => {
                  this.updateUserProfile(this.searchedUser);
                  document.getElementById("success").hidden = false;
                  document.getElementById("success").innerText =
                      "Updated Successfully";
                  document.getElementById("error").hidden = true;
                  console.log(response.data)
                },
                error => {
                  document.getElementById("error").hidden = false;
                  document.getElementById("error").innerText =
                      error.response.data.Errors;
                  document.getElementById("success").hidden = true;
                  console.log(error);
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
          var tempUserData = await this.getUserById(this.$route.params.profileId);
          if (tempUserData === "Invalid permissions") {
            this.$router.push("/settings/profile/" + this.user.profile_id);
            this.searchedUser = this.user;
          } else {
            this.searchedUser = tempUserData;
          }
        }
        this.showAdmin = true;
      },
      /**
       * The method is used to populate the drop down menu, that allows user to select their current country.
       */
      loadCountries() {
        // this.searchedUser.passports = this.searchedUser.passports.slice();
        this.getDataFromUrl(COUNTRIES_URL)
            .then((response) => {
              const countries = [];
              const data = response.data;
              for (let country in data) {
                let country_name = data[country].name;
                countries.push(country_name)
              }
              this.countries_option = countries
            })
            .catch(error => console.log(error));
      },
      /**
       * The method is used to filter out the feature object without any cities
       */
      removeNullCities(features) {
        let featuresCity = [];
        for (const feature of features) {
          if (feature.properties.city !== undefined) {
            featuresCity.push(feature);
          }
        }
        return featuresCity;
      },
      /**
       * The method is used to filter out the feature object without any states
       */
      removeNullState(features) {
        let featuresState = [];
        for (const feature of features) {
          if (feature.properties.state !== undefined) {
            featuresState.push(feature);
          }
        }
        return featuresState;
      },
    },
  }
</script>

<style scoped>
    @import "../../../../public/styles/pages/profileSettingsStyle.css";
</style>