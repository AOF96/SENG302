<template>
  <div id="settingsWrap">
    <UserSettingsMenu/>
    <div class="settingsContent">
      <h1>Activities</h1>
      <hr>
      <h3>Select your activities:</h3>
      <div class="settingsActivity">
        <div class="dropdownContent">
          <select class="dropDownBox"  v-model="selected_activities" id="Activities">
            <option v-for="addingactivity in activities_option" v-bind:key="addingactivity">
              {{addingactivity}}
            </option>
          </select>
<!--          <h3 class="activityType">{{ addingactivity }} </h3><button class="settingsActivitiesAdd">Remove</button>-->
          <button class="settingsActivitiesAdd" v-on:click="addActivity()">Add activity</button>
          <button v-on:click="updateActivities(user.user)">Save</button>

          <div v-for="activity in user.user.tmp_activities" v-bind:key="activity">
            <h4>{{activity}}</h4>
            <button v-on:click="removeActivityType(activity)">remove</button>
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
  import {mapActions, mapState} from 'vuex'

  export default {
    components: {
      UserSettingsMenu
    },
    data() {
      return {
        selected_activities: "",
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
        console.log('init')
        this.user.user.tmp_activities = this.user.user.activities.slice()
      },
      addActivity(){
        console.log("in activity");

        this.user.user.tmp_activities.push(this.selected_activities)
        console.log(this.user.user.tmp_activities)
        const index = this.activities_option.indexOf(this.selected_activities)
        if(index == -1) return
        this.activities_option.splice(index, 1)
        this.selected_activities = ""
        this.updateTmpActivities(this.user.user)
      },
      removeActivityType(activity) {
        const index = this.user.user.tmp_activities.indexOf(activity)
        if (index === -1) return
        this.user.user.tmp_activities.splice(index, 1)
        this.activities_option.push(activity)
        this.activities_option.sort()
        this.updateTmpPassports(this.user.user)
      },
    }
  }
</script>
