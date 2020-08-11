<template>
  <div @click="showLocations = false">
    <v-snackbar
            v-model="snackbar"
    >
      {{ snackbarText }}
      <v-btn
              color="primary"
              text
              @click="snackbar = false"
      >
        Close
      </v-btn>
    </v-snackbar>
    <v-overlay style="z-index: 1000" :value="overlayLoader">
      <v-progress-circular indeterminate size="64"></v-progress-circular>
    </v-overlay>
    <div class="createActivityContainer">
      <div class="createActivityContentContainer">
        <h1 id="createHeading">Create Activity</h1>
        <h1 id="createSubheading">Create your own activity.</h1>
        <form class="CreateActivityFormContainer">
          <v-card style="margin-top:20px;border-radius:15px;">
            <v-tabs v-model="tabs" grow show-arrows>
              <v-tab>
                Basic Info*
              </v-tab>
              <v-tab>
                Time/Date*
              </v-tab>
              <v-tab>
                Location
              </v-tab>
              <v-tab>
                Achievements
              </v-tab>
            </v-tabs>

            <v-tabs-items v-model="tabs">
              <v-tab-item>
                <v-card flat class="py-3">
                  <label class="editActivityLabel" for="name">Activity Name*</label>
                  <input class="editActivityInput" type="text" id="name" v-model="name" placeholder="Activity Name" required />
                  <label class="editActivityLabel">Visibility</label>
                  <v-row no-gutters style="margin:0 20px;">
                    <v-radio-group v-model="visibility" row>
                        <v-radio
                                label="Public"
                                color="green"
                                value="public"
                        ></v-radio>
                        <v-radio
                                label="Restricted"
                                color="orange"
                                value="restricted"
                        ></v-radio>
                        <v-radio
                                label="Private"
                                color="red"
                                value="private"
                        ></v-radio>
                    </v-radio-group>
                  </v-row>

                  <label class="editActivityLabel" for="desc">Description</label>
                  <textarea
                        class="editActivityTextarea"
                        maxlength="255"
                        type="text"
                        id="desc"
                        v-model="description"
                        placeholder="Activity Description">
                  </textarea>
                  <label class="editActivityLabel" style="">Activity Types*</label>
                  <v-select
                          style="margin:0 20px;margin-top:5px;"
                          v-model="activity_types_selected"
                          :items="activities_option"
                          attach
                          chips
                          label="Select Activity Types"
                          multiple
                          rounded
                          outlined
                  ></v-select>
                </v-card>
              </v-tab-item>
              <v-tab-item>
                <v-card flat class="py-3">
                  <label class="editActivityLabel">Continuous?</label>
                  <v-row no-gutters style="margin:0 20px;">
                    <v-radio-group v-model="duration" v-on:change="setDuration" row>
                      <v-radio
                              label="Continuous"
                              color="green"
                              value="continuous"
                      ></v-radio>
                      <v-radio
                              label="Duration"
                              color="green"
                              value="duration"
                      ></v-radio>
                    </v-radio-group>
                  </v-row>

                  <label class="editActivityLabel" id="startDateLabel" for="start_date">Start Date*</label>
                  <input class="editActivityInput" type="date" id="start_date" v-model="start_date" />

                  <label class="editActivityLabel" id="endDateLabel" for="end_date">End Date*</label>
                  <input class="editActivityInput" type="date" id="end_date" v-model="end_date" />

                  <label class="editActivityLabel" id="startTimeLabel" for="start_time">Start Time*</label>
                  <input class="editActivityInput" type="time" id="start_time" v-model="start_time" />

                  <label class="editActivityLabel" id="endTimeLabel" for="end_time">End Time*</label>
                  <input class="editActivityInput" type="time" id="end_time" v-model="end_time" />
                </v-card>
              </v-tab-item>
              <v-tab-item>
                <v-card flat class="py-3">
                  <div>
                    <v-combobox
                            v-model="city"
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
                            style="margin: 0 20px;"
                    />
                    <v-combobox
                            v-model="state"
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
                            style="margin: 0 20px;"
                    />
                    <v-combobox
                            v-model="country"
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
                            style="margin: 0 20px;"
                    />
                  </div>
                </v-card>
              </v-tab-item>
              <v-tab-item>
                <v-card flat>
                  <!-- Your stuff here -->
                </v-card>
              </v-tab-item>
            </v-tabs-items>
            <v-divider></v-divider>
            <v-row no-gutters>
              <v-btn :disabled="tabs <= 0" v-on:click="tabs -= 1" text rounded color="black" style="margin: 15px;padding:0 10px;">
                <v-icon style="padding-right:5px;">mdi-arrow-left</v-icon> Back
              </v-btn>
              <v-spacer></v-spacer>
              <v-btn v-if="tabs < 3" v-on:click="tabs += 1" text rounded color="black" style="margin: 15px;padding:0 10px;">
                Next <v-icon style="padding-left:5px;">mdi-arrow-right</v-icon>
              </v-btn>
              <v-btn
                      v-if="tabs >= 3"
                      v-on:click="addActivity"
                      style="margin:15px 20px;"
                      color="primary"
                      rounded
                      outlined
                      right
                      :disabled="overlayLoader"
              >
                Create Activity
              </v-btn>
            </v-row>
          </v-card>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from "vuex";
import { apiUser, apiActivity } from "../../../api";
import router from "../../../router";
const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all';

export default {
    data() {
        return {
            tabs: null,
            selected_activity: "Activity Type",
            activities_option: [],
            countries_option: [],
            location: null,
            duration: "duration",
            name: "",
            description: "",
            activity_types_selected: [],
            start_date: null,
            end_date: null,
            start_time: null,
            end_time: null,
            combinedStartTime: null,
            combinedEndTime: null,
            suggestedLocations: [],
            showLocations: false,
            visibility: "public",
            city: "",
            dialog: false,
            country: "",
            state: "",
            locationCity: null,
            locationState: null,
            search: null,
            searchState: null,
            isLoading: false,
            stateLoading: false,
            features: [],
            createdId: null,
            setAchievement: false,
            snackbar: false,
            snackbarText: "",
            overlayLoader: false,
        };
    },

    /**
     * On start-up, the mounted function calls the rest api countries and updates the select drop down with the list of
     * countries for the user to choose.
     */
    mounted: function() {
        if (!this.user.isLogin) {
            this.$router.push('/login');
        } else {
            this.loadCountries();
        }
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
    created: async function () {
        if(this.$route.params.profileId != this.user.profile_id && this.user.permission_level == 0){
            this.$router.push('/profile');
        }
        // Ensures only activity types from the database can be selected and cannot select ones already selected
        await apiUser
            .getActivityTypes()
            .then(response => {
                this.activities_option = response.data;
                for (let i = 0; i < this.activities_option.length; i++) {
                    this.activities_option[i] = this.activities_option[i].replace(/-/g, " ")
                }
            })
            .catch(error => console.log(error));
    },

    methods: {
        ...mapActions(["createActivity"]),
        ...mapActions(["updateUserContinuousActivities","getDataFromUrl"]),
        ...mapActions(["updateUserDurationActivities"]),

      /**
       * This method filters the the data received from the api and only suggests cities to the user.
       *
       */
      getLocationCity(location) {
        let city = "Almora";
        if(location.properties.city !== undefined){
          city = location.properties.city;
          return city;
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
         * Sets the location and resets the location input after the user has selected a location from the dropdown
         * box.
         */
        setLocation() {
          this.location = "";
          this.location = this.city.Description + ', ' + this.state.Description + ', ' + this.country;
        },

      /**
       * Navigates the user to the Edit Achievement component page if they would like
       * to add achievements to the their activities.
       */
       navigateToCreateAchievement(){
        this.setAchievement = true;
        this.addActivity()
        if(this.setAchievement === true) {
          this.$router.push({path: '/activity/achievement_setting/' + this.createdId});
        }
       },

        /**
         * Shows/hides date and time selection if duration is duration/continuous
         */
        setDuration() {
            if (this.duration === "duration") {
                document.getElementById("start_date").type = "date";
                document.getElementById("end_date").type = "date";
                document.getElementById("start_time").type = "time";
                document.getElementById("end_time").type = "time";
                document.getElementById("startDateLabel").hidden = false;
                document.getElementById("endDateLabel").hidden = false;
                document.getElementById("startTimeLabel").hidden = false;
                document.getElementById("endTimeLabel").hidden = false;
            } else {
                document.getElementById("start_date").type = "hidden";
                document.getElementById("end_date").type = "hidden";
                document.getElementById("start_time").type = "hidden";
                document.getElementById("end_time").type = "hidden";
                document.getElementById("startDateLabel").hidden = true;
                document.getElementById("endDateLabel").hidden = true;
                document.getElementById("startTimeLabel").hidden = true;
                document.getElementById("endTimeLabel").hidden = true;
            }
        },
        /**
         * Adds activity type to selected options
         */
        selectActivityType() {
            if (this.selected_activity !== undefined) {
                this.activity_types_selected.push(this.selected_activity);
                let index = this.activities_option.indexOf(this.selected_activity);
                if (index !== -1) {
                    this.activities_option.splice(index, 1);
                }
            }
        },
        /**
         * Removes activity type from selection
         */
        removeActivityType(addedActivity) {
            this.activities_option.push(addedActivity);
            let index = this.activity_types_selected.indexOf(addedActivity);
            if (index !== -1) {
                this.activity_types_selected.splice(index, 1);
            }
        },
        /**
         * Combines the times and dates given in the form to a single datetime format
         * Sets datetime to null if continuous activity
         */
        combineDateTime() {
            let currentDate = new Date(Date.now());
            let timeZone = currentDate.toString().slice(currentDate.toString().indexOf("+"), 5);

            if (this.duration !== "duration") {
                this.combinedStartTime = null;
                this.combinedEndTime = null;
            } else if (this.start_time === null && this.end_time === null) {
                this.combinedStartTime = this.start_date + "T00:00:00" + timeZone;
                this.combinedEndTime = this.end_date + "T00:00:00" + timeZone;
            } else if (this.start_time === null) {
                this.combinedStartTime = this.start_date + "T00:00:00" + timeZone;
                this.combinedEndTime =
                    this.end_date + "T" + this.end_time + ":00" + timeZone;
            } else if (this.end_time === null) {
                this.combinedStartTime = this.start_date + "T" + this.start_time + ":00" + timeZone;
                this.combinedEndTime = this.end_date + "T00:00:00" + timeZone;
            } else {
                this.combinedStartTime = this.start_date + "T" + this.start_time + ":00" + timeZone;
                this.combinedEndTime = this.end_date + "T" + this.end_time + ":00" + timeZone;
            }
        },
        /**
         * Checks if the datetime passes conditions. Ensures time is in the future and start is not later than end
         * @return boolean true if passes, false if fails
         */
        checkTimeContinuity() {
            let currentDate = new Date(Date.now());
            let timeZone = currentDate.toString().slice(currentDate.toString().indexOf("+"), 5);

            currentDate.setHours(0);
            currentDate.setMinutes(0);
            currentDate.setSeconds(0);
            currentDate.setMilliseconds(0);
            if (new Date(this.combinedStartTime) > new Date(this.combinedEndTime)) {
                this.displayError("End time must be after start time.");
                return false;
            } else if (currentDate > new Date(this.start_date + "T00:00:00" + timeZone)) {
                this.displayError("Start date must be in the future.");
                return false;
            } else if (currentDate.getFullYear() + 2 < new Date(this.start_date + "T00:00:00" + timeZone).getFullYear()
                || currentDate.getFullYear() + 2 < new Date(this.end_date + "T00:00:00" + timeZone).getFullYear()) {
                this.displayError("Must be less than 2 years in the future.");
                return false;
            }
            return true;
        },

        /**
         * The method is used to populate the drop down menu, that allows user to select their current country.
         */
        loadCountries() {
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
         * Check all activity form conditions
         * @return boolean true if passes, false if fails
         */
        checkFormConditions() {
            if (this.name === null || this.name.trim() === "") {
                // Name is empty
                this.displayError("Please select an activity name.");
                this.tabs = 0;
                return false;
            } else if (this.activity_types_selected.length < 1) {
              // No activity types selected
              this.displayError("Please select at least one activity type.");
              this.tabs = 0;
              return false;
            } else if (this.duration !== "duration" && this.duration !== "continuous") {
                // Duration is not set
                this.displayError("Please select a duration.");
                this.tabs = 1;
                return false;
            } else if (this.duration === "duration" &&
                (this.start_date === null || this.end_date === null || this.start_date === "" || this.end_date === "")) {
                // Start or end date not set
                this.displayError("Please select start and end date.");
                this.tabs = 1;
                return false;
            } else if (this.duration === "duration" && !this.checkTimeContinuity()) {
                // Time check failed
                this.tabs = 1;
                return false;
            } else {
                // All passed
                return true;
            }
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
      },

        /**
         * Checks form conditions and sends create activity request if conditions pass
         */
        addActivity() {
            this.overlayLoader = true;
            // Combines dates and times, must be done before checking form
            this.combineDateTime();

            // Checks all activity attribute conditions
            if (!this.checkFormConditions()) {
                this.overlayLoader = false;
                return;
            }

            // Sets duration to a boolean for the request
            var tempIsDuration = this.duration !== "duration";
            this.setLocation(location);

            // Send a create request
            apiActivity.addActivity(this.$route.params.profileId, this.name, tempIsDuration, this.combinedStartTime,
                this.combinedEndTime, this.description, this.location, this.activity_types_selected, this.visibility)
                .then(
                    response => {
                      if(response){
                        this.createdId = response.data;
                        apiUser.getUserContinuousActivities(this.user.profile_id)
                            .then(response => {
                              this.updateUserContinuousActivities(response.data);
                            });
                        apiUser.getUserDurationActivities(this.user.profile_id)
                            .then(response => {
                              this.updateUserDurationActivities(response.data);
                            });
                        router.push("/activity/"+this.createdId);
                      }
                    },
                    error => {
                        this.overlayLoader = true;
                        if(error){
                            this.displayError("An error occurred.");
                        }
                    });
        },
        /**
         * Shows error text for given error string
         * @param error
         */
        displayError(error) {
          this.snackbar = true;
          this.snackbarText = error;
        }
    },
  watch: {
    /**
     * The function starts makes a call to the photon api once the user has types at least 3 characters and suggest different
     * cities
     * @param val of the city set in the vuetify component
     */
    search (val) {
      if(val.length < 3){
        return
      }
      this.isLoading = true;

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
    /**
     * The function starts makes a call to the photon api once the user has types at least 3 characters and suggest different
     * states
     * @param val of the state set in the vuetify component
     */
    searchState (val) {

      if(val.length < 3){
        return
      }
      this.stateLoading = true;

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
};
</script>

<style scoped>
    @import "../../../../public/styles/pages/activitySettingsStyle.css";

</style>
