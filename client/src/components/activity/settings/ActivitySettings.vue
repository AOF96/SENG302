<template>
    <div @click="showLocations = false">
        <div class="createActivityContainer">
            <div class="createActivityContentContainer">
                <router-link v-bind:to="'/profile/'+this.$route.params.profileId">
                    <button class="genericConfirmButton backButton">Back to Profile</button>
                </router-link>
                <h1>Create a new Activity</h1>
                <form class="CreateActivityFormContainer">
                    <label class="editActivityLabel" for="name">Activity Name</label>
                    <input class="editActivityInput" type="text" id="name" v-model="name" placeholder="Activity Name" required />

                    <label class="editActivityLabel" for="time">Continuous?</label>
                    <select
                        class="editActivitySelect"
                        id="time"
                        v-on:change="setDuration"
                        v-model="duration"
                    >
                        <option value="continuous">Continuous</option>
                        <option value="duration">Duration</option>
                    </select>

                    <label class="editActivityLabel" id="startDateLabel" for="start_date">Start Date</label>
                    <input class="editActivityInput" type="date" id="start_date" v-model="start_date" />

                    <label class="editActivityLabel" id="endDateLabel" for="end_date">End Date</label>
                    <input class="editActivityInput" type="date" id="end_date" v-model="end_date" />

                    <label class="editActivityLabel" id="startTimeLabel" for="start_time">Start Time</label>
                    <input class="editActivityInput" type="time" id="start_time" v-model="start_time" />

                    <label class="editActivityLabel" id="endTimeLabel" for="end_time">End Time</label>
                    <input class="editActivityInput" type="time" id="end_time" v-model="end_time" />

                    <label class="editActivityLabel" for="desc">Description</label>
                    <textarea
                        class="editActivityTextarea"
                        maxlength="255"
                        type="text"
                        id="desc"
                        v-model="description"
                        placeholder="Activity Description">
                    </textarea>

                    <label class="editActivityLabel">Location: <b>{{ location }}</b></label>
                    <div>
<!--                        <input id="locationInput" autocomplete="off" class="editActivityInput" type="text"-->
<!--                               placeholder="Search here..." onfocus="showLocations = true"/>-->
<!--                        <div v-if="showLocations && suggestedLocations.length > 0" class="dropdown" >-->
<!--                            <div v-for="(item, index) in suggestedLocations" v-bind:key="index" class="dropdown-content">-->
<!--                                <p v-on:click="setLocation(item.summary)">{{item.summary}}</p>-->
<!--                            </div>-->
<!--                        </div>-->
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
                          dense
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
                          dense
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
                          dense
                      />
                    </div>

                    <label class="editActivityLabel">Activity Types</label>
                    <div>
                        <select
                                v-on:change="selectActivityType"
                                v-model="selected_activity"
                                name="activityType"
                                class="editActivitySelect"
                                required
                        >
                            <option selected disabled hidden>Activity Type</option>
                            <option v-for="addingActivity in activities_option" v-bind:key="addingActivity">
                                {{addingActivity}}
                            </option>
                        </select>
                    </div>
                    <div class="addedActivityTypeContainer">
                      <div class="addedActivityContainer" v-for="addedActivity in activity_types_selected" v-bind:key="addedActivity">
                        <h4 class="addedTypeContainer">{{addedActivity}}</h4>
                        <button class="deleteActivityTypeButton" v-on:click="removeActivityType(addedActivity)">Remove</button>
                        <div class="floatClear"></div>
                      </div>
                    </div>
                    <h6 class="editSuccessMessage" id="activity_success" hidden="false">Saved successfully</h6>
                    <h6 class="editErrorMessage" id="activity_error" hidden="false">An error has occurred</h6>

          <div class="confirmButtonContainer">
            <button class="genericConfirmButton" type="button" v-on:click="addActivity">Create Activity</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from "vuex";
import { apiUser, apiActivity } from "../../../api";
import router from "../../../router";
// import axios from "axios";
const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all'


export default {
    data() {
        return {
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
            city: null,
            country: null,
            state: null,
            locationCity: null,
            locationState: null,
            search: null,
            searchState: null,
            isLoading: false,
            stateLoading: false,
            features: []

        };
    },

    /**
     * On start-up, adds a listener to locationInput such that a query is made to Photon when the user stops typing
     * after 1 second. Calls a support function to add a summary key for each of the location objects. Locations with
     * duplicate summaries are removed.
     */
    mounted: function() {
        // let outer = this;
        // let input = document.querySelector('#locationInput');
        // let timeout = null;
        // input.addEventListener('keyup', function () {
        //     clearTimeout(timeout);
        //     timeout = setTimeout(function () {
        //         const url = "https://photon.komoot.de/api/?q=" + input.value;
        //         axios.get(url)
        //             .then((response) => {
        //                 //We use a temporary list instead of using outer.suggestedLocations immediately so that the list
        //                 //is only displayed when it is finished, avoiding the problem of the user being taken to the
        //                 //middle of the list instead of the top
        //                 let temp = [];
        //                 let locationSummaries = [];
        //                 for(let location in response.data.features) {
        //                     let locationSummary = outer.getLocationSummary(response.data.features[location]);
        //                     if (!locationSummaries.includes(locationSummary)) {
        //                         temp.push(response.data.features[location]);
        //                         temp[temp.length - 1]["summary"] = locationSummary;
        //                         locationSummaries.push(locationSummary);
        //                     }
        //                 }
        //                 outer.suggestedLocations = temp;
        //                 outer.showLocations = true;
        //             })
        //             .catch(error => console.log(error));
        //     }, 1000);
        // });
      this.loadCountries();
      console.log(this.countries_option)
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
            })
            .catch(error => console.log(error));
    },

    methods: {
        ...mapActions(["createActivity"]),
        ...mapActions(["updateUserContinuousActivities","getDataFromUrl"]),
        ...mapActions(["updateUserDurationActivities"]),


        /**
         * Adds the street and city if they exist, adds name, state and country and returns the result to the mounted
         * function.
         */
        // getLocationSummary(location) {
        //     let result = "";
        //
        //     result += location.properties.name;
        //     if ("street" in location.properties) {
        //         result += ", " + location.properties.street;
        //     }
        //     if ("city" in location.properties) {
        //         result += ", " + location.properties.city;
        //     }
        //     if ("state" in location.properties) {
        //         result += ", " + location.properties.state;
        //     }
        //     if ("country" in location.properties) {
        //         result += ", " + location.properties.country;
        //     }
        //
        //     return result;
        // },

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
         * Sets the location and resets the location input after the user has selected a location from the dropdown
         * box.
         */
        setLocation() {
          this.location ="";
          this.city = document.getElementById('inputCity').value,
          this.state = document.getElementById('inputState').value,
          this.country = document.getElementById('inputCountry').value
          this.location = this.city + ',' + this.state + ',' + this.country
          console.log(this.location);
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
          console.log("in here")
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
                return false;
            } else if (this.duration !== "duration" && this.duration !== "continuous") {
                // Duration is not set
                this.displayError("Please select a duration.");
                return false;
            } else if (this.duration === "duration" &&
                (this.start_date === null || this.end_date === null || this.start_date === "" || this.end_date === "")) {
                // Start or end date not set
                this.displayError("Please select start and end date.");
                return false;
            } else if (this.activity_types_selected.length < 1) {
                // No activity types selected
                this.displayError("Please select at least one activity type.");
                return false;
            } else if (this.duration === "duration" && !this.checkTimeContinuity()) {
                // Time check failed
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
            // Combines dates and times, must be done before checking form
            this.combineDateTime();

            // Checks all activity attribute conditions
            if (!this.checkFormConditions()) {
                return;
            }

            // Sets duration to a boolean for the request
            var tempIsDuration = this.duration !== "duration";
            this.setLocation(location)

            // Send a create request
            apiActivity.addActivity(this.$route.params.profileId, this.name, tempIsDuration, this.combinedStartTime,
                this.combinedEndTime, this.description, this.location, this.activity_types_selected)
                .then(
                    response => {
                        document.getElementById("activity_success").hidden = false;
                        document.getElementById("activity_error").hidden = true;
                        console.log(response);
                        apiUser.getUserContinuousActivities(this.user.profile_id)
                            .then(response => {
                                this.updateUserContinuousActivities(response.data);
                            });
                        apiUser.getUserDurationActivities(this.user.profile_id)
                            .then(response => {
                                this.updateUserDurationActivities(response.data);
                            });
                        router.push("/profile/"+this.$route.params.profileId);
                    },
                    error => {
                        if(error){
                            this.displayError("An error occured");
                        }
                    });
        },
        /**
         * Shows error text for given error string
         * @param error
         */
        displayError(error) {
            document.getElementById("activity_error").hidden = false;
            document.getElementById("activity_error").innerText = error;
            document.getElementById("activity_success").hidden = true;
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
};
</script>

<style scoped>
    @import "../../../../public/styles/pages/activitySettingsStyle.css";

</style>
