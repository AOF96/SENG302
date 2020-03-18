<template>
  <div id="settingsWrap">
    <UserSettingsMenu/>
    <div class="settingsContent">
      <h1>Activities</h1>
      <hr>
      <h3>Select your pokemon</h3>
      <div>
        <label for="cars">Choose a car:</label>
        <select v-model="selectedActivity" id="cars">
          <option value="volvo">Volvo</option>
          <option value="saab">Saab</option>
          <option value="mercedes">Mercedes</option>
          <option value="audi">Audi</option>
        </select>
        <button @click="addActivity" type="button">Click</button>

        <div v-for="activity in activities" v-bind:key="activity">
          <h3>
            {{ activity }}
          </h3>
          <hr>
        </div>
      </div>

    </div>
  </div>
</template>

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
