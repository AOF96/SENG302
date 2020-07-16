<template>
  <div class="settingsContainer">
    <UserSettingsMenu />
    <div class="settingsContentContainer">
      <router-link v-bind:to="'/profile/'+this.$route.params.profileId">
        <button class="genericConfirmButton backButton">Back to Profile</button>
      </router-link>
      <h1>Edit Activity Type</h1>
      <hr />
      <div class="itemContainer" v-for="activity in searchedUser.activities" v-bind:key="activity">
        <h4>{{activity}}</h4>
        <button class="genericDeleteButton" v-on:click="removeActivityType(activity)">remove</button>
        <div class="floatClear"></div>
      </div>
      <div id="activityActions">
        <form class="formTopSpacing" @submit.prevent>
          <select
            v-model="selected_activity"
            name="activityType"
            placeholder="Activity Type"
            value="Activity Type"
            class="itemSelect"
            required
          >
            <option selected disabled hidden>Activity Type</option>
            <option
              v-for="addingActivity in activities_option"
              v-bind:key="addingActivity"
            >{{addingActivity}}</option>
          </select>
          <button class="genericConfirmButton addItemButton" v-on:click="addActivityType()">Add</button>
          <button class="genericConfirmButton saveButton" v-on:click="saveActivityTypes()">Save</button>
        </form>
      </div>
      <div class="errorMessageContainer">
        <h6 class="editSuccessMsg" id="activity_type_success" hidden="false">Saved successfully</h6>
        <h6 class="editErrorMsg" id="activity_type_error" hidden="false">An error has occurred</h6>
      </div>

    </div>
    <div class="floatClear"></div>
  </div>
</template>

<script>
import UserSettingsMenu from "./ProfileSettingsMenu";
import { mapActions, mapState, mapGetters } from "vuex";
import { apiUser } from "../../../api";

export default {
  name: "AddUserActivity",
  components: {
    UserSettingsMenu
  },
  data() {
    return {
      selected_activity: "Activity Type",
      activities_option: [],
      num_of_activities: 1,
      searchedUser: {}
    };
  },
  computed: {
    ...mapState(["user"]),
    ...mapGetters(["user"])
  },
  created: async function() {
    // Ensures only activity types from the database can be selected and cannot select ones already selected
    await apiUser
      .getActivityTypes()
      .then(response => {
        const activityTypes = response.data;

        for (let activity of this.searchedUser.activities) {
          const index = activityTypes.indexOf(activity);
          if (index === -1) continue;
          activityTypes.splice(index, 1);
        }
        this.activities_option = activityTypes;
      })
      .catch(error => console.log(error));
  },
  mounted() {
    this.loadSearchedUser();
  },
  methods: {
    ...mapActions(["updateActivities"]),

    startUp() {
      console.log("init");
      this.searchedUser.activities = this.searchedUser.activities.slice();
    },

    /**
     * Adds an activity type to the searchedUser on add button click
     */
    addActivityType() {
      if (!this.selected_activity || this.selected_activity === "Activity Type")
        return;
      this.searchedUser.activities.push(this.selected_activity);
      console.log(this.searchedUser.activities);
      const index = this.activities_option.indexOf(this.selected_activity);
      if (index === -1) return;
      this.activities_option.splice(index, 1);
      this.selected_activity = "Activity Type";
      this.updateActivities(this.searchedUser);
    },

    /**
     * Removes an activity from the searchedUser on remove button click
     * @param activity to remove
     */
    removeActivityType(activity) {
      const index = this.searchedUser.activities.indexOf(activity);
      if (index === -1) return;
      this.searchedUser.activities.splice(index, 1);
      this.activities_option.push(activity);
      this.activities_option.sort();
      this.updateActivities(this.searchedUser);
    },

    /**
     * Sends update request for all activity type changes to server on save button click
     */
    saveActivityTypes() {
      this.updateActivities(this.searchedUser);
      console.log(this.searchedUser.activities);
      apiUser
        .editUserActivityTypes(
          this.searchedUser.profile_id,
          this.searchedUser.activities
        )
        .then(
          response => {
            document.getElementById("activity_type_success").hidden = false;
            document.getElementById("activity_type_error").hidden = true;
            console.log(response);
          },
          error => {
            document.getElementById("activity_type_error").hidden = false;
            document.getElementById("activity_type_error").innerText =
              error.response.data.Errors;
            document.getElementById("activity_type_success").hidden = true;
            console.log(error);
          }
        );
    },

    /*
        Uses user id from url to request user data.
     */
    async loadSearchedUser() {
      if (
        this.$route.params.profileId == null ||
        this.$route.params.profileId == ""
      ) {
        this.$router.push("/settings/activities/" + this.user.profile_id);
        this.searchedUser = this.user;
      } else {
        var tempUserData = await apiUser.getUserById(
          this.$route.params.profileId
        );
        if (tempUserData == "Invalid permissions") {
          this.$router.push("/settings/activities/" + this.user.profile_id);
          this.searchedUser = this.user;
        } else {
          this.searchedUser = tempUserData;
        }
      }
      this.startUp();
    }
  }
};
</script>
