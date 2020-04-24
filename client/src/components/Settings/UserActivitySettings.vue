<template>
  <div id="settingsWrap">
    <UserSettingsMenu />
    <div class="settingsContent">
      <h1>Edit Activities</h1>
      <hr>
      <div class="activityBox"  v-for="activity in user.user.tmp_activities" v-bind:key="activity">
        <h4 class="activityDisplay">{{activity}}</h4>
        <button class="removeActivityButton" v-on:click="removeActivityType(activity)">remove</button>
        <div class="floatClear"></div>
      </div>

      <div id="activityActions">
        <form @submit.prevent>
          <select
                  v-model="selected_activities"
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
        selected_activities: "Activity Type",
        activities_option: ["relaxing","fun","swimming"],
        num_of_activities: 1,
      }
    },
    computed: {
      ...mapState(['user'])
    },
    mounted() {
      this.startUp()
    },
    methods: {
      ...mapActions(['updateActivities', 'updateTmpActivities']),
      startUp() {
        console.log('init');
        this.user.user.tmp_activities = this.user.user.activities.slice()
      },
      addActivityType(){
        console.log("in activity");

        this.user.user.tmp_activities.push(this.selected_activities);
        console.log(this.user.user.tmp_activities);
        const index = this.activities_option.indexOf(this.selected_activities);
        if(index == -1) return;
        this.activities_option.splice(index, 1);
        this.selected_activities = "Activity Type";
        this.updateTmpActivities(this.user.user)
      },
      removeActivityType(activity) {
        const index = this.user.user.tmp_activities.indexOf(activity);
        if (index === -1) return
        this.user.user.tmp_activities.splice(index, 1);
        this.activities_option.push(activity);
        this.activities_option.sort();
        this.updateTmpPassports(this.user.user)
      },
      saveActivityTypes() {
        this.updateActivities(this.user.user);
        console.log(this.user.user.activities);
        apiUser.editProfile(this.user.user.profile_id, this.user.user.firstname, this.user.user.lastname, this.user.user.middlename,
                this.user.user.nickname, this.user.user.primary_email, this.user.user.bio, this.user.user.date_of_birth, this.user.user.gender,
                this.user.user.fitness, this.user.user.additional_email, this.user.user.passports, this.user.user.activities);
      }
    }
  }
</script>
