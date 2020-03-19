<template>
  <div id="settingsWrap">
    <UserSettingsMenu/>
    <div class="settingsContent">
      <h1>Activities</h1>
      <hr>
      <h3>Select your activities:</h3>

      <div class="settingsActivity">

        <div class="dropdownContent">
          <select class="dropDownBox"  v-model="selectedActivity" id="Activities">
            <option value="relaxing">Relaxing</option>
            <option value="fun">Fun</option>
            <option value="adventurous">Adventurous</option>
            <option value="extreme">Extreme</option>
            <option value="teamsport">Team Sport</option>
          </select>



          <button class="settingsActivitiesAdd" @click="addActivity" type="button">Add activity</button>

          <div style="margin-top: 10px" v-for="activity in activities" v-bind:key="activity">
            <h3 class="activityType">{{ activity }} </h3><button class="settingsActivitiesAdd">Remove</button>
            <hr>
          </div>
        </div>
      </div>



    </div>
  </div>
</template>


<style>
  .dropdownContent {
      margin-left: 20px;
      margin-bottom: 10px;


  }

  .settingsActivitiesAdd {
    padding: 6px 16px;
    border-radius: 50px;
    font-size: 12px;
    color: #1cca92;
    font-weight: 500;
    background: #ffffff;
    border: 2px solid #1cca92;
    cursor: pointer;
    transition: all 0.2s ease;
    text-decoration: none;
    margin-left:10px;
  }

  .dropDownBox {
    /*box-shadow: 4px 4px #ccc;*/
    /*font-weight: 500;*/
    /*font-size: 20px;*/
    width: 140px;
    height: 35px;
    padding: 4px;
    border-radius:4px;
    box-shadow: 2px 2px 8px #999;
    background: #eee;
    border: none;
    outline: none;
    display: inline-block;
  }

  .activityType {
    display: inline;
    margin-top: 20px;
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
