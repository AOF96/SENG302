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
          v-if="user.permission_level == 2 && searchedUser.permission_level != 2 && user.profile_id != searchedUser.profile_id"
        >
          <h2>Enable Admin Abilities</h2>
          <div
            class="togswitch"
            :position="searchedUser.permission_level == 1 ? 'on' : 'off'"
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

<!--        <h2 id="locationHeader">Location: <b>{{ location }}</b></h2>-->
<!--        <button v-if="location !== null" class="removeLocationButton profileRemoveLocationButton"-->
<!--                v-on:click="deleteLocation()"><b>x</b></button>-->
        <div>
          <!--          <input id="locationInput" autocomplete="on" type="text" placeholder="Search here..."-->
          <!--                 onfocus="showLocations = true" />-->

          <v-combobox
                  v-model="model"
                  v-bind="locationCity"
                  :items="items"
                  :loading="isLoading"
                  :search-input.sync="search"
                  color="primary"
                  no-filter
                  hide-no-data
                  hide-selected
                  item-text="Description"
                  item-value="API"
                  label="City"
                  placeholder="Start typing to Search"
                  return-object
                  id="inputCity"
          ></v-combobox>


          <v-combobox
                  v-bind="locationState"
                  :items="itemsState"
                  :search-input.sync="searchState"
                  :loading="isLoading"
                  color="primary"
                  no-filter
                  hide-no-data
                  hide-selected
                  item-text="Description"
                  item-value="API"
                  label="State"
                  placeholder="Start typing to Search"
                  return-object
                  id="inputState"
          ></v-combobox>

          <v-combobox
                  v-bind="locationCountry"
                  :items="countries_option"
                  :loading="isLoading"
                  color="primary"
                  hide-no-data
                  hide-selected
                  item-text="Description"
                  label="Country"
                  placeholder="Start typing to Search"
                  return-object
                  id="inputCountry"
          ></v-combobox>
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
        ></textarea>
        <h6 class="editProfileInfoErrorMessage" id="error" hidden="true"></h6>
        <h6 class="updateInfoSuccessMessage" id="success" hidden="true"></h6>
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
        >Update Profile</button>
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

        if(l.city.length == 0){
          delete l.city
        }
        if(l.state.length == 0){
          delete l.state
        }
        if(l.country.length == 0){
          delete l.country
        }
        this.searchedUser.location = l;
        // console.log("l here")
        // console.log(l);
        // this.searchedUser.location = {
        //   city:this.searchedUser.city,
        //   // state: document.getElementById('inputState').value,
        //   // country: document.getElementById('inputCountry').value
        // };
        //
        //
        //
        // if(document.getElementById('inputCity').value.length == 0){
        //   console.log("in here ")
        //   delete this.searchedUser.location.city;
        // }
        // this.searchedUser.location = {
        //   city: document.getElementById('inputCity').value,
        //   state: document.getElementById('inputState').value,
        //   country: document.getElementById('inputCountry').value
        // };
      },

      // /**
      //  * Sets the location and each of its individual components to be null.
      //  */
      // deleteLocation() {
      //   this.location = null;
      //   this.searchedUser.location = {
      //     city: null,
      //     state: null,
      //     country: null
      //   };
      // },

      /**
       * This method filters the the data received from the api and only suggests cities to the user.
       *
       */
      getLocationCity(location) {
        let city = null;
        console.log(typeof(location.properties.city));

        if(location.properties.city !== undefined){
          city += location.properties.city;
          return city
        }
        return city;
      },


      /**
       * This method filters the the data received from the api and only suggests states to the user.
       *
       */
      getLocationState(location) {
        let state = null;
        console.log("we here in state")
        state += location.properties.state;
        return state;
      },

      /*
      Sends a request to the server side to update the searchedUser's profile info. Displays error messages if the update
      was unsuccessful.
      */
      updateProfile() {
        this.setLocation(location)

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
        if (this.searchedUser.permission_level == 1) {
          this.searchedUser.permission_level = 0;
        } else if (this.searchedUser.permission_level == 0) {
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
                    if (this.searchedUser.profile_id == this.user.profile_id) {
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
                this.$route.params.profileId == ""
        ) {
          this.$router.push("/settings/profile/" + this.user.profile_id);
          this.searchedUser = this.user;
        } else {
          var tempUserData = await this.getUserById(this.$route.params.profileId);
          if (tempUserData == "Invalid permissions") {
            this.$router.push("/settings/profile/" + this.user.profile_id);
            this.searchedUser = this.user;
          } else {
            this.searchedUser = tempUserData;
          }
        }
        // if (this.searchedUser.city) {
        //   this.setLocation(
        //           this.searchedUser.city +
        //           ", " +
        //           this.searchedUser.state +
        //           ", " +
        //           this.searchedUser.country
        //   );
        // }
        this.showAdmin = true;
      },


      /**
       * The method is used to populate the drop down menu, that allows user to select their current country.
       */
      loadCountries() {
        // this.searchedUser.passports = this.searchedUser.passports.slice();
        this.getDataFromUrl(COUNTRIES_URL)
                .then((response) => {
                  const countries = []
                  const data = response.data
                  for (let country in data) {
                    let country_name = data[country].name
                    countries.push(country_name)
                  }
                  // for(let country of this.searchedUser.passports) {
                  //   const index = countries.indexOf(country)
                  //   if (index === -1) continue
                  //   countries.splice(index, 1)
                  // }
                  this.countries_option = countries
                })
                .catch(error => console.log(error));
      },
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
                  const { features } = res
                  this.features = features
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
        this.isLoading = true

        fetch("https://photon.komoot.de/api/?q=" + val)
                .then(res => res.json())
                .then(res => {
                  const { features } = res
                  this.features = features
                })
                .catch(err => {
                  console.log(err)
                })
                .finally(() => (this.isLoading = false))
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
      // let outer = this;
      // let input = document.querySelector("#locationInput");
      // // let timeout = null;
      // if(input == null){
      //   return;
      // }
      // input.addEventListener("keyup", function() {
      //   clearTimeout(timeout);
      //   timeout = setTimeout(function() {
      //     const url = "https://photon.komoot.de/api/?q=" + input.value;
      //     this.isLoading = true;
      //     axios.get(url)
      //       .then(response => {
      //         //We use a temporary list instead of using outer.suggestedLocations immediately so that the list
      //         //is only displayed when it is finished, avoiding the problem of the user being taken to the
      //         //middle of the list instead of the top
      //         let temp = [];
      //         let locationSummaries = [];
      //         for (let location in response.data.features) {
      //           console.log("we here")
      //           console.log(response.data.features[location].state)
      //           if (response.data.features[location].properties.osm_value === "city" ||
      //                   response.data.features[location].properties.osm_value === "town") {
      //             let locationSummary = outer.getLocationSummary(
      //               response.data.features[location]
      //             );
      //             if (!locationSummaries.includes(locationSummary)) {
      //               temp.push(response.data.features[0]);
      //               temp[temp.length - 1]["summary"] = locationSummary;
      //               locationSummaries.push(locationSummary);
      //             }
      //           }
      //         }
      //         outer.suggestedLocations = temp;
      //         outer.showLocations = true;
      //       })
      //       .catch(error => console.log(error));
      //   }, 1000);
      // });
    }
  };
</script>
