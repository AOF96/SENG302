<template>
  <div @click="showLocations = false">
    <NavBar />
    <div class="createActivityContainer">
      <div class="createActivityContentContainer">
        <router-link v-bind:to="'/profile/'+this.$route.params.profileId">
          <button class="genericConfirmButton">Back to Profile</button>
        </router-link>
        <h1>Edit Activity</h1>
        <form class="CreateActivityFormContainer">
          <label class="editActivityLabel" for="name">Activity Name</label>
          <input
            class="editActivityInput"
            type="text"
            id="name"
            v-model="activity_name"
            placeholder="Activity Name"
            required
          />

          <label class="editActivityLabel" for="time">Continuous?</label>
          <select class="editActivitySelect" id="time" v-model="duration">
            <option value="continuous">Continuous</option>
            <option value="duration">Duration</option>
          </select>

          <div v-if="isDuration">
            <label class="editActivityLabel" id="startDateLabel" for="start_date">Start Date</label>
            <input class="editActivityInput" type="date" id="start_date" v-model="start_date" />

            <label class="editActivityLabel" id="endDateLabel" for="end_date">End Date</label>
            <input class="editActivityInput" type="date" id="end_date" v-model="end_date" />

            <label class="editActivityLabel" id="startTimeLabel" for="start_time">Start Time</label>
            <input class="editActivityInput" type="time" id="start_time" v-model="start_time" />

            <label class="editActivityLabel" id="endTimeLabel" for="end_time">End Time</label>
            <input class="editActivityInput" type="time" id="end_time" v-model="end_time" />
          </div>

          <label class="editActivityLabel" for="desc">Description</label>
          <textarea
            class="editActivityTextarea"
            maxlength="255"
            type="text"
            id="desc"
            v-model="description"
            placeholder="Activity Description"
          ></textarea>

          <label class="editActivityLabel">Location</label>
          <div>
            <input id="locationInput" class="editActivityInput" type="text" onfocus="showLocations = true" v-model="location"/>
            <div v-if="location !== '' && showLocations && suggestedLocations.length > 0" class="dropdown" >
              <div v-for="(item, index) in suggestedLocations" v-bind:key="index" class="dropdown-content">
                <p v-on:click="location = item.summary">{{item.summary}}</p>
              </div>
            </div>
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
              <option
                v-for="addingActivity in activities_option"
                v-bind:key="addingActivity"
              >{{addingActivity}}</option>
            </select>
          </div>
          <div class="addedActivityTypeContainer">
            <div
              class="addedActivityContainer"
              v-for="addedActivity in activity_types_selected"
              v-bind:key="addedActivity"
            >
              <h4 class="addedTypeContainer">{{addedActivity}}</h4>
              <button
                class="deleteActivityTypeButton"
                v-on:click="removeActivityType(addedActivity)"
              >Remove</button>
              <div class="floatClear"></div>
            </div>
          </div>
          <h6 class="editSuccessMessage" id="activity_success" hidden="false">Saved successfully</h6>
          <h6 class="editErrorMessage" id="activity_error" hidden="false">An error has occurred</h6>
          <div class="confirmButtonContainer">
            <button
              id="deleteActivityButton"
              type="button"
              v-on:click="deleteActivity()"
            >Delete Activity</button>
            <button
              id="editActivityButton"
              type="button"
              v-on:click="saveEditedActivity()"
            >Save Changes</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from "vuex";
import { apiUser, apiActivity } from "../api";
import router from "../router";
import axios from "axios";
import NavBar from "./modules/NavBar";

export default {
  components: {
    NavBar
  },
  data() {
    return {
      selected_activity: "Activity Type",
      activities_option: [],
      location: "",
      duration: "duration",
      activity_types_selected: [],
      start_date: null,
      end_date: null,
      start_time: null,
      end_time: null,
      activity_name: "",
      combinedEndTime: null,
      combinedStartTime: null,
      continuous: false,
      description: "",
      author_id: null,
      suggestedLocations: [],
      showLocations: false
    };
  },

  /**
   * On start-up, adds a listener to locationInput such that a query is made to Photon when the user stops typing
   * after 1 second. Calls a support function to add a summary key for each of the location objects. Locations with
   * duplicate summaries are removed.
   */
  mounted: function() {
    let outer = this;
    let input = document.querySelector('#locationInput');
    let timeout = null;
    input.addEventListener('keyup', function () {
      clearTimeout(timeout);
      timeout = setTimeout(function () {
        const url = "https://photon.komoot.de/api/?q=" + input.value;
        axios.get(url)
                .then((response) => {
                  //We use a temporary list instead of using outer.suggestedLocations immediately so that the list
                  //is only displayed when it is finished, avoiding the problem of the user being taken to the
                  //middle of the list instead of the top
                  let temp = [];
                  let locationSummaries = [];
                  for(let location in response.data.features) {
                    let locationSummary = outer.getLocationSummary(response.data.features[location]);
                    if (!locationSummaries.includes(locationSummary)) {
                      temp.push(response.data.features[location]);
                      temp[temp.length - 1]["summary"] = locationSummary;
                      locationSummaries.push(locationSummary);
                    }
                  }
                  outer.suggestedLocations = temp;
                  outer.showLocations = true;
                })
                .catch(error => console.log(error));
      }, 1000);
    });
  },

  computed: {
    ...mapGetters(["user"]),
    isDuration() {
      return this.duration == "duration";
    }
  },
  created: async function() {
    this.loadActivity();
    // Ensures only activity types from the database can be selected and cannot select ones already selected
    await apiUser
      .getActivityTypes()
      .then(response => {
        this.activities_option = response.data;
        this.activity_types_selected.forEach(e => {
          this.activities_option.some((v, i) => {
            if (v == e) this.activities_option.splice(i, 1);
          });
        });
      })
      .catch(error => console.log(error));
  },
  methods: {
    ...mapActions(["createActivity"]),
    ...mapActions(["updateUserContinuousActivities"]),
    ...mapActions(["updateUserDurationActivities"]),

    /**
     * Adds the street and city if they exist, adds name, state and country and returns the result to the mounted
     * function.
     */
    getLocationSummary(location) {
      let result = "";

      result += location.properties.name;
      if ("street" in location.properties) {
        result += ", " + location.properties.street;
      }
      if ("city" in location.properties) {
        result += ", " + location.properties.city;
      }
      if ("state" in location.properties) {
        result += ", " + location.properties.state;
      }
      if ("country" in location.properties) {
        result += ", " + location.properties.country;
      }

      return result;
    },

    /*
      Uses activity id from url to request activity data.
    */
    async loadActivity() {
      if (
        this.$route.params.activityId == null ||
        this.$route.params.activityId == ""
      ) {
        this.$router.push("/profile");
      } else {
        var tempActivityData = await apiActivity.getActivityById(
          this.$route.params.activityId
        );
        if (tempActivityData == "Invalid permissions") {
          this.$router.push("/profile");
        } else {
          this.activity_name = tempActivityData.activity_name;
          this.continuous = tempActivityData.continuous;
          if (this.continuous) {
            this.duration = "continuous";
          } else {
            this.duration = "duration";
            this.end_time = this.formatTime(tempActivityData.end_time);
            this.start_time = this.formatTime(tempActivityData.start_time);
            this.start_date = this.formatDate(tempActivityData.start_time);
            this.end_date = this.formatDate(tempActivityData.end_time);
          }
          this.author_id = tempActivityData.author.profile_id;
          this.description = tempActivityData.description;
          this.activity_type = tempActivityData.activity_type.slice();
          this.location = tempActivityData.location;
          this.activity_types_selected = tempActivityData.activity_type.map(
            e => e.name
          );
        }
      }
    },

    /**
     * Shows/hides date and time selection if duration is duration/continuous
     */
    setDuration() {
      this.start_date = null;
      this.end_date = null;
      this.start_time = null;
      this.end_time = null;
    },
    /**
     * This function converts the milli seconds to the format YYYY-MM-DD
     */
    formatDate(date) {
      let d = new Date(date);
      let month = "" + (d.getMonth() + 1);
      let day = "" + d.getDate();
      let year = d.getFullYear();
      if (month.length < 2) month = "0" + month;
      if (day.length < 2) day = "0" + day;
      return [year, month, day].join("-");
    },

    formatTime(date) {
      let d = new Date(date);
      let hour = "" + d.getHours();
      let minutes = "" + d.getMinutes();
      if (hour.length < 2) hour = "0" + hour;
      if (minutes.length < 2) minutes = "0" + minutes;
      return [hour, minutes].join(":");
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
      let timeZone = currentDate
        .toString()
        .slice(currentDate.toString().indexOf("+"), 5);

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
        this.combinedStartTime =
          this.start_date + "T" + this.start_time + ":00" + timeZone;
        this.combinedEndTime = this.end_date + "T00:00:00" + timeZone;
      } else {
        this.combinedStartTime =
          this.start_date + "T" + this.start_time + ":00" + timeZone;
        this.combinedEndTime =
          this.end_date + "T" + this.end_time + ":00" + timeZone;
      }
    },

    /**
     * Checks if the datetime passes conditions. Ensures time is in the future and start is not later than end
     * @return boolean true if passes, false if fails
     */
    checkTimeContinuity() {
      let currentDate = new Date(Date.now());
      let timeZone = currentDate
        .toString()
        .slice(currentDate.toString().indexOf("+"), 5);

      currentDate.setHours(0);
      currentDate.setMinutes(0);
      currentDate.setSeconds(0);
      currentDate.setMilliseconds(0);
      if (new Date(this.combinedStartTime) > new Date(this.combinedEndTime)) {
        this.displayError("End time must be after start time.");
        return false;
      } else if (
        currentDate > new Date(this.start_date + "T00:00:00" + timeZone)
      ) {
        this.displayError("Start date must be in the future.");
        return false;
      } else if (
        currentDate.getFullYear() + 2 <
          new Date(this.start_date + "T00:00:00" + timeZone).getFullYear() ||
        currentDate.getFullYear() + 2 <
          new Date(this.end_date + "T00:00:00" + timeZone).getFullYear()
      ) {
        this.displayError("Must be less than 2 years in the future.");
        return false;
      }
      return true;
    },

    /**
     * Check all activity form conditions
     * @return boolean true if passes, false if fails
     */
    checkFormConditions() {
      if (this.activity_name === null || this.activity_name.trim() === "") {
        // Name is empty
        this.displayError("Please select an activity name.");
        return false;
      } else if (
        this.duration !== "duration" &&
        this.duration !== "continuous"
      ) {
        // Duration is not set
        return false;
      } else if (
        this.duration === "duration" &&
        (this.start_date === null ||
          this.end_date === null ||
          this.start_date === "" ||
          this.end_date === "")
      ) {
        return false;
      } else if (this.activity_types_selected.length < 1) {
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
     * Checks form conditions and sends create activity request if conditions pass
     */
    saveEditedActivity() {
      // Combines dates and times, must be done before checking form
      this.combineDateTime();

      // Checks all activity attribute conditions
      if (!this.checkFormConditions()) {
        return;
      }

      // Sets duration to a boolean for the request
      this.duration = this.duration !== "duration";

      apiActivity
        .editActivity(
          this.user.profile_id,
          this.activity_name,
          this.duration,
          this.combinedStartTime,
          this.combinedEndTime,
          this.description,
          this.location,
          this.activity_types_selected,
          this.$route.params.activityId
        )
        .then(
          response => {
            console.log(response);
            apiUser
              .getUserContinuousActivities(this.user.profile_id)
              .then(response => {
                this.updateUserContinuousActivities(response.data);
              });
            apiUser
              .getUserDurationActivities(this.user.profile_id)
              .then(response => {
                this.updateUserDurationActivities(response.data);
              });
            router.push("/profile/" + this.author_id);
          },
          error => {
            console.log(error.data.error);
          }
        );
    },
    deleteActivity() {
      apiActivity
        .deleteActivity(this.user.profile_id, this.$route.params.activityId)
        .then(response => {
          console.log(response);
          apiUser
            .getUserContinuousActivities(this.user.profile_id)
            .then(response => {
              this.updateUserContinuousActivities(response.data);
            });
          apiUser
            .getUserDurationActivities(this.user.profile_id)
            .then(response => {
              this.updateUserDurationActivities(response.data);
            });
          router.push("/profile/" + this.author_id);
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
  }
};
</script>
