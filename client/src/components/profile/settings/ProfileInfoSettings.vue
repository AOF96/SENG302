<template>
  <div class="settingsContainer" @click="showLocations = false">
    <UserSettingsMenu />
    <div class="settingsContentContainer">
      <router-link v-bind:to="'/profile/'+this.$route.params.profileId">
        <button class="genericConfirmButton backButton" id="backToProfileButton">Back to Profile</button>
      </router-link>
      <h1 class="settingsTitle">Edit Profile Info</h1>
      <hr />
      <form @submit.prevent class="editForm">
        <div
          id="adminToggle"
          v-bind:class="{ showadmin: showAdmin }"
          v-if="user.permission_level === 2 && searchedUser.permission_level !== 2 && user.profile_id !== searchedUser.profile_id"
        >
          <h2>Enable Admin Abilities</h2>
          <div
            class="togswitch"
            :position="searchedUser.permission_level === 1 ? 'on' : 'off'"
            v-on:click="toggleAdmin()"
          >
            <div class="togswitchnob"></div>
            <div class="togswitchnob_touch"></div>
          </div>
          <div class="floatClear"></div>
        </div>
        <h2>First Name</h2>
        <input
          type="text"
          name="fname"
          id="firstName"
          v-model="searchedUser.firstname"
          placeholder="First Name*"
          required
        />
        <h2>Middle Name</h2>
        <input type="text" name="lname" id="middleName" v-model="searchedUser.middlename" placeholder="Middle Name" />
        <h2>Last Name</h2>
        <input
          type="text"
          name="lname"
          id="lastName"
          v-model="searchedUser.lastname"
          placeholder="Last Name*"
          required
        />
        <h2>Nickname</h2>
        <input type="text" name="nickname" id="nickName" v-model="searchedUser.nickname" placeholder="Nickname" />
        <div>
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

        <h2>Gender</h2>
        <select
          v-model="searchedUser.gender"
          name="gender"
          id="userGender"
          placeholder="Gender"
          value="Gender"
          required
        >
          <option selected disabled hidden>Gender</option>
          <option>Non-Binary</option>
          <option>Female</option>
          <option>Male</option>
        </select>
        <h2>Fitness Level</h2>
        <select
          v-model="searchedUser.fitness"
          name="fitnesslevel"
          placeholder="fitness"
          value="fitness"
          id="userFitnessLevel"
          required
        >
          <option value="0">I never exercise</option>
          <option value="1">I can walk a short distance</option>
          <option value="2">I can jog a short distance</option>
          <option value="3">I can run a medium distance</option>
          <option value="4">I can run a marathon</option>
        </select>
        <h2>Birthday</h2>
        <input v-model="searchedUser.date_of_birth" name="birthday" id="userBirthday" type="date" required />
        <h2>Bio</h2>
        <textarea
            maxlength="255"
            name="bio"
            id="userBio"
            v-model="searchedUser.bio"
            placeholder="Write about yourself"
        />
        <h6 class="editProfileInfoErrorMessage" id="error" hidden="true"/>
        <h6 class="updateInfoSuccessMessage" id="success" hidden="true"/>
        <button
          class="genericDeleteButton deleteProfileButton"
          @click.stop="dialog = true"
        >
          Delete Account
        </button>
        <v-dialog
          v-model="dialog"
          max-width="290"
        >
          <v-card>
            <v-card-title class="headline">Delete Account</v-card-title>
            <v-card-text>
              Are you sure you want to delete this account?
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <button
                @click="dialog = false"
                v-on:click="deleteAccount()"
                class="genericConfirmButton updateProfileButton"
              >
                Yes
              </button>

              <button
                class="genericDeleteButton deleteProfileButton"
                @click="dialog = false"
              >
                No
              </button>
            </v-card-actions>
          </v-card>
        </v-dialog>
        <button
          class="genericConfirmButton updateProfileButton" id="profileUpdateButton"
          v-on:click="updateProfile()"
          type="submit"
        >Save Changes</button>
      </form>
    </div>
    <div class="floatClear"></div>
  </div>
</template>

<script>
  import { mapGetters, mapActions } from "vuex";
  // import axios from 'axios';
  const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all'
  import UserSettingsMenu from "./ProfileSettingsMenu";

export default {
  name: "EditUserInfo",
  components: {
    UserSettingsMenu
  },

    computed: {
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

    data: function() {
      return {
        searchedUser: {},
        showAdmin: false,
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
        descriptionLimit: 60,
        features: [],
        countries_option: [],
        adding_country: "Passport Countries",
      };
    },
    methods: {
      ...mapActions(["logout", "updateUserProfile", "getUserById", "editProfile", "deleteUserAccount", "getDataFromUrl"]),

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

        if(l.city.length === 0){
          l.city = this.searchedUser.city;
        }
        if(l.state.length === 0){
          l.state = this.searchedUser.state;
        }
        if(l.country.length === 0){
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
        if(location.properties.city !== undefined){
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
        if(location.properties.state !== undefined){
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

      toggleAdmin() {
        if (this.searchedUser.permission_level === 1) {
          this.searchedUser.permission_level = 0;
        } else if (this.searchedUser.permission_level === 0) {
          this.searchedUser.permission_level = 1;
        }
      },

      /**
       * Allows user or admin to delete the account
       */
      deleteAccount() {
        this.deleteUserAccount({'id': this.searchedUser.profile_id})
                .then(() => {
                  if (this.user.permission_level > 0) {
                    if (this.searchedUser.profile_id === this.user.profile_id) {
                      location.reload();
                    } else {
                      this.$router.push("/settings/admin_dashboard");
                    }
                  }
                  else {
                    location.reload();
                  }
                })
                .catch((error) => {
                          console.log(error);
                        }
                )
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
      removeNullCities(features){
        let featuresCity = [];
        for(const feature of features){
          if(feature.properties.city !== undefined) {
            featuresCity.push(feature);
          }
        }
        return featuresCity;
      },
      /**
       * The method is used to filter out the feature object without any states
       */
      removeNullState(features){
        let featuresState = [];
        for(const feature of features){
          if(feature.properties.state !== undefined) {
            featuresState.push(feature);
          }
        }
        return featuresState;
      }
    },


    watch: {
      search (val) {
        if(val.length < 3){
          return
        }
        this.isLoading = true

        fetch("https://photon.komoot.de/api/?q=" + val)
                .then(res => res.json())
                .then(res => {
                  const { features } = res;
                  this.features = this.removeNullCities(features)
                })
                .catch(err => {
                  console.log(err)
                })
                .finally(() => (this.isLoading = false))
      },
      searchState (val) {

        if(val.length < 3){
          return
        }
        this.stateLoading = true

        fetch("https://photon.komoot.de/api/?q=" + val)
                .then(res => res.json())
                .then(res => {
                  const { features } = res;
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
      if (!this.user.isLogin) {
          this.$router.push('/login');
      } else {
        this.loadSearchedUser();
        this.loadCountries();
      }
  }
};
</script>
