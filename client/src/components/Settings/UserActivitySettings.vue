<template>
  <div id="settingsWrap">
    <UserSettingsMenu/>
    <div class="settingsContent">
      <h1>Activities</h1>
      <hr>
      <h3>Select your activities</h3>

      <div class="settingsActivity">

        <div class="testing">
          <select  v-model="selectedActivity" id="Activities">
            <option value="relaxing">Relaxing</option>
            <option value="fun">Fun</option>
            <option value="adventurous">Adventurous</option>
            <option value="extreme">Extreme</option>
            <option value="teamsport">Team Sport</option>
          </select>



          <button class="testing" @click="addActivity" type="button">Click</button>

          <div v-for="activity in activities" v-bind:key="activity">
            <h3>
              {{ activity }}
            </h3>
            <hr>
          </div>
        </div>
      </div>



    </div>
  </div>
</template>


<style>
  .testing {
    margin:15px;

  }
</style>

<script>
  import UserSettingsMenu from "./UserSettingsMenu";

  export default {
    data: function() {
      return {
        selectedActivity: "",
        activities: []
      }
    },
    components: {
      UserSettingsMenu
    },
    methods: {
      addActivity: function() {
        this.activities.push(this.selectedActivity);
        // Do some sanity check
      }
    }
  }
</script>
