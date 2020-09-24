<template>
  <div class="settingsContainer">
    <UserSettingsMenu />
    <div class="settingsContentContainer">
      <h1>Edit Activity Type</h1>
      <hr />
      <div class="itemContainer" v-for="activity in searchedUser.activities" v-bind:key="activity">
        <h4>{{activity}}</h4>
        <button class="genericDeleteButton" v-on:click="removeActivityType(activity)">remove</button>
        <div class="floatClear"></div>
      </div>
      <div id="activityActions">
        <form class="formTopSpacing">
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
          <button type="button" class="genericConfirmButton addItemButton" v-on:click="addActivityType()">Add</button>
          <button type="button" class="genericConfirmButton saveButton" v-on:click="saveActivityTypes()">Save All Activity Types</button>
        </form>
      </div>
      <v-snackbar
          v-model="snackbar"
          :color="snackbarColour"
          top
      >
        {{ snackbarText }}
        <v-btn
            @click="snackbar = false"
            color="white"
            text
        >
          Close
        </v-btn>
      </v-snackbar>

    </div>
    <div class="floatClear"></div>
  </div>
</template>

<script>
import UserSettingsMenu from "./ProfileSettingsMenu";
import { mapActions, mapState, mapGetters } from "vuex";

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
      searchedUser: {},
      snackbar: false,
      snackbarText: null,
      snackbarColour: '',
    };
  },
  computed: {
    ...mapState(["user"]),
    ...mapGetters(["user"])
  },
  mounted() {
      if (!this.user.isLogin) {
          this.$router.push('/login');
      } else {
          this.loadSearchedUser();
      }
  },
  methods: {
      ...mapActions(["updateActivities", "getUserById", "editUserActivityTypes", "getActivityTypes"]),

    /**
     * Sets up the user information and activity types
     */
    startUp() {
      this.searchedUser.activities = this.searchedUser.activities.slice();
      this.replaceDashesWithSpaces(this.searchedUser.activities);
      this.setActivityTypeOptions();
    },

    /**
     * Loads the activity types and removes the ones that have already been selected.
     */
    async setActivityTypeOptions() {
      // Ensures only activity types from the database can be selected and cannot select ones already selected
      await this.getActivityTypes()
              .then(response => {
                const activityTypes = response.data;
                this.replaceDashesWithSpaces(activityTypes);

                for (let activity of this.searchedUser.activities) {
                  const index = activityTypes.indexOf(activity);
                  if (index === -1) continue;
                  activityTypes.splice(index, 1);
                }
                this.activities_option = activityTypes;
              })
              .catch(error => {
                this.showSnackbar(error.response.data.Errors, "error");
              });
    },

    /**
     * Takes a list of strings and replaces all dashes with spaces
     * @param list of activity types
     */
    replaceDashesWithSpaces(list) {
      for (let i = 0; i < list.length; i++) {
        list[i] = list[i].replace(/-/g, " ")
      }
    },

    /**
     * Adds an activity type to the searchedUser on add button click
     */
    addActivityType() {
      if (!this.selected_activity || this.selected_activity === "Activity Type")
        return;
      this.searchedUser.activities.push(this.selected_activity);
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
      this.editUserActivityTypes({'id': this.searchedUser.profile_id, 'activities': this.searchedUser.activities})
        .then(() => {
            this.showSnackbar("Saved successfully.","success");
          },
          error => {
            this.showSnackbar(error.response.data.Errors, "error");
          }
        );
    },

    /**
     * Uses the user id from url to request user data.
     */
    async loadSearchedUser() {
      if (
        this.$route.params.profileId == null ||
        this.$route.params.profileId === ""
      ) {
        this.$router.push("/settings/activities/" + this.user.profile_id);
        this.searchedUser = this.user;
      } else {
        let tempUserData = await this.getUserById(this.$route.params.profileId);
        if (tempUserData === "Invalid permissions") {
          this.$router.push("/settings/activities/" + this.user.profile_id);
          this.searchedUser = this.user;
        } else {
          this.searchedUser = tempUserData;
        }
      }
      this.startUp();
    },

    /**
     * Shows a pop-up message to the user displaying the result of a function call.
     * @param text: the text to be displayed to the user.
     * @param colour: the colour to be displayed with the snackbar.
     */
    showSnackbar(text, colour) {
      this.snackbarText = text;
      this.snackbarColour = colour;
      this.snackbar = true;
    }
  }
};
</script>
