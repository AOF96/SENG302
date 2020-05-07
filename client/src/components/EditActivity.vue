<template>
  <div>
    <NavBar />
    <div class="createActivityContainer">
      <div class="createActivityContentContainer">
        <form class="CreateActivityFormContainer">
          <h1>Edit an Activity</h1>

          <label class="editActivityLabel" for="name">Activity Name</label>
          <input class="editActivityInput" type="text" id="name" v-model="activity_name" required />

          <label class="editActivityLabel" for="time">Continuous?</label>
          <select
            class="editActivityDropbox"
            id="time"
            v-on:change="setDuration"
            v-model="duration"
          >
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
          ></textarea>

          <label class="editActivityLabel">Location</label>
          <div>
            <select v-model="adding_country" name="countries" class="editActivityDropbox" required>
              <option selected disabled hidden>Countries</option>
              <option
                v-for="addingCountry in countries_option"
                v-bind:key="addingCountry"
              >{{addingCountry}}</option>
            </select>
          </div>

          <label class="editActivityLabel">Activity Types</label>
          <div>
            <select
              v-on:change="selectActivityType"
              v-model="selected_activity"
              name="activityType"
              class="editActivityDropbox"
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
              <label class="addedTypeContainer">{{addedActivity}}</label>
              <button
                class="deleteActivityTypeButton"
                v-on:click="removeActivityType(addedActivity)"
              >Remove</button>
            </div>
          </div>

          <h6 class="editSuccessMessage" id="activity_success" hidden="false">Saved successfully</h6>
          <h6 class="editErrorMessage" id="activity_error" hidden="false">An error has occurred</h6>

          <div class="confirmButtonContainer">
            <button
              id="editActivityButton"
              type="button"
              v-on:click="saveEditedActivity()"
            >Save Changes</button>
            <button id="deleteActivityButton">Delete Activity</button>
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

const COUNTRIES_URL = "https://restcountries.eu/rest/v2/all";

export default {
  components: {
    NavBar
  },
  data() {
    return {
      selected_activity: "Activity Type",
      activities_option: [],
      countries_option: [],
      adding_country: "Countries",
      duration: "duration",
      activity_types_selected: [],
      start_date: null,
      end_date: null,
      start_time: null,
      end_time: null,
      activity_name: "",
      continuous: "",
      description: ""
    };
  },
  computed: {
    ...mapGetters(["activity"]),
    ...mapGetters(["user"]),
    isDuration() {
      return this.duration == "duration";
    }
  },
  created: async function() {
    // Ensures only activity types from the database can be selected and cannot select ones already selected
    await apiUser
      .getActivityTypes()
      .then(response => {
        this.activities_option = response.data;
        this.activity_name = this.activity.name;
        this.end_time = this.formatTime(this.activity.end_time);
        this.start_time = this.formatTime(this.activity.start_time);
        this.continous = this.activity.continous;
        this.description = this.activity.description;
        this.activity_type = this.activity.activity_types.slice();
        this.adding_country = this.activity.location;
        this.activity_types_selected = this.activity.activity_types.map(
          e => e.name
        );
        this.activity_types_selected.forEach(e => {
          this.activities_option.some((v, i) => {
            if (v == e) this.activities_option.splice(i, 1);
          });
        });
        this.start_date = this.formatDate(this.activity.start_time);
        this.end_date = this.formatDate(this.activity.end_time);
      })
      .catch(error => console.log(error));

    // Gets list of countries that can be selected
    await axios
      .get(COUNTRIES_URL)
      .then(response => {
        const countries = [];
        const data = response.data;
        for (let country in data) {
          let country_name = data[country].name;
          countries.push(country_name);
        }
        this.countries_option = countries;
      })
      .catch(error => console.log(error));
  },
  methods: {
    ...mapActions(["createActivity"]),
    ...mapActions(["updateUserContinuousActivities"]),
    ...mapActions(["updateUserDurationActivities"]),

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
     * Checks form conditions and sends create activity request if conditions pass
     */
    saveEditedActivity() {
      let currentDate = new Date(Date.now());
      let timeZone = currentDate
        .toString()
        .slice(currentDate.toString().indexOf("+"), 5);
      if (this.activity.name === null || this.activity.name.trim() === "") {
        this.displayError("Please select an activity name.");
      } else if (
        this.duration !== "duration" &&
        this.duration !== "continuous"
      ) {
        this.displayError("Please select a duration.");
      } else if (
        this.duration === "duration" &&
        (this.start_date === null ||
          this.end_date === null ||
          this.start_date === "" ||
          this.end_date === "")
      ) {
        this.displayError("Please select start and end date.");
      } else if (this.activity_types_selected.length < 1) {
        this.displayError("Please select at least one activity type.");
      } else {
        let combinedStartTime;
        let combinedEndTime;
        if (this.duration !== "duration") {
          combinedStartTime = null;
          combinedEndTime = null;
        } else if (this.start_time === null && this.end_time === null) {
          combinedStartTime = this.start_date + "T00:00:00" + timeZone;
          combinedEndTime = this.end_date + "T00:00:00" + timeZone;
        } else if (this.start_time === null) {
          combinedStartTime = this.start_date + "T00:00:00" + timeZone;
          combinedEndTime =
            this.end_date + "T" + this.end_time + ":00" + timeZone;
        } else if (this.end_time === null) {
          combinedStartTime =
            this.start_date + "T" + this.start_time + ":00" + timeZone;
          combinedEndTime = this.end_date + "T00:00:00" + timeZone;
        } else {
          combinedStartTime =
            this.start_date + "T" + this.start_time + ":00" + timeZone;
          combinedEndTime =
            this.end_date + "T" + this.end_time + ":00" + timeZone;
        }

        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        currentDate.setMilliseconds(0);
        if (new Date(combinedStartTime) > new Date(combinedEndTime)) {
          this.displayError("End time must be after start time.");
          return;
        } else if (
          currentDate > new Date(this.start_date + "T00:00:00" + timeZone)
        ) {
          this.displayError("Start date must be in the future.");
          return;
        } else if (
          currentDate.getFullYear() + 2 <
            new Date(this.start_date + "T00:00:00" + timeZone).getFullYear() ||
          currentDate.getFullYear() + 2 <
            new Date(this.end_date + "T00:00:00" + timeZone).getFullYear()
        ) {
          this.displayError("Must be less than 2 years in the future.");
          return;
        }

        this.duration = this.duration !== "duration";

        apiActivity
          .editActivity(
            this.user.profile_id,
            this.activity_name,
            this.duration,
            combinedStartTime,
            combinedEndTime,
            this.description,
            this.adding_country,
            this.activity_types_selected,
            this.activity.activity_id
          )
          .then(
            response => {
              document.getElementById("activity_success").hidden = false;
              document.getElementById("activity_error").hidden = true;
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
              router.push("profile");
            },
            error => {
              document.getElementById("activity_error").hidden = false;
              document.getElementById("activity_error").innerText =
                error.response.data.error;
              document.getElementById("activity_success").hidden = true;
            }
          );
      }
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
