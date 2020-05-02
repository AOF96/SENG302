<template>
  <div id="settingsWrap">
    <UserSettingsMenu />
    <div class="settingsContent">
      <h1>Edit Activities</h1>
      <hr>
      <div class="activityBox"  v-for="activity in user.user.activities" v-bind:key="activity">
        <h4 class="activityDisplay">{{activity}}</h4>
        <button class="removeActivityButton" v-on:click="removeActivityType(activity)">remove</button>
        <div class="floatClear"></div>
      </div>

      <div id="activityActions">
        <form @submit.prevent>
          <select
                  v-model="selected_activity"
                  name="activityType"
                  placeholder="Activity Type"
                  value="Activity Type"
                  id="activityInput"
                  required
          >
            <option selected disabled hidden>Activity Type</option>
            <option v-for="addingActivity in activities_option" v-bind:key="addingActivity">
              {{addingActivity}}
            </option>
          </select>
          <button id = "addActivityButton" v-on:click="addActivityType()">Add</button>
          <button id = "saveChangesButton"  v-on:click="saveActivityTypes()">Save</button>
        </form>
      </div>
      <h6 class="edit_success" id="activity_type_success" hidden="false">Saved successfully</h6>
      <h6 class="edit_error" id="activity_type_error" hidden="false">An error has occurred</h6>
    </div>
  </div>
</template>

<script>
  import UserSettingsMenu from "./UserSettingsMenu";
  import {mapActions, mapState} from 'vuex'
  import {apiUser} from "../../api";

  export default {
    components: {
      UserSettingsMenu
    },
    data() {
      return {
        selected_activity: "Activity Type",
        activities_option: [],
        num_of_activities: 1,
      }
    },
    computed: {
      ...mapState(['user'])
    },
    created: async function() {
        // Ensures only activity types from the database can be selected and cannot select ones already selected
        await apiUser.getActivityTypes().then((response) => {
        const activityTypes = response.data;

        for(let activity of this.user.user.activities) {
          const index = activityTypes.indexOf(activity);
          if (index === -1) continue;
          activityTypes.splice(index, 1)
        }
        this.activities_option = activityTypes;
      }).catch(error => console.log(error))
    },
    mounted() {
      this.startUp()
    },
    methods: {
      ...mapActions(['updateActivities']),

      startUp() {
        console.log('init');
        this.user.user.activities = this.user.user.activities.slice()
      },

      /**
       * Adds an activity type to the user on add button click
       */
      addActivityType(){
        if(!this.selected_activity || this.selected_activity === "Activity Type") return;
        this.user.user.activities.push(this.selected_activity);
        console.log(this.user.user.activities);
        const index = this.activities_option.indexOf(this.selected_activity);
        if(index === -1) return;
        this.activities_option.splice(index, 1);
        this.selected_activity = "Activity Type";
        this.updateActivities(this.user.user)
      },

      /**
       * Removes an activity from the user on remove button click
       * @param activity to remove
       */
      removeActivityType(activity) {
        const index = this.user.user.activities.indexOf(activity);
        if (index === -1) return;
        this.user.user.activities.splice(index, 1);
        this.activities_option.push(activity);
        this.activities_option.sort();
        this.updateActivities(this.user.user)
      },

      /**
       * Sends update request for all activity type changes to server on save button click
       */
      saveActivityTypes() {
        this.updateActivities(this.user.user);
        console.log(this.user.user.activities);
        apiUser.editUserActivityTypes(this.user.user.profile_id, this.user.user.activities).then((response) => {
          document.getElementById("activity_type_success").hidden = false;
          document.getElementById("activity_type_error").hidden = true;
          console.log(response);
        }, (error) => {
          document.getElementById("activity_type_error").hidden = false;
          document.getElementById("activity_type_error").innerText = error.response.data.Errors;
          document.getElementById("activity_type_success").hidden = true;
          console.log(error);
        });
      }
    }
  }
</script>
