<template>
  <v-card class="activityPageCard">
    <v-card-title>Achievements</v-card-title>
    <v-container>
      <v-row>
        <v-col>
          <v-row>
            <v-col>
              <h3>{{title}}</h3>
            </v-col>
          </v-row>
          <v-row justify="center" align="center">
            <v-col cols="5">
              Latest Result: {{latestResult}}
            </v-col>
            <v-col cols="4">
              <v-text-field
                label="New Result"
                outlined
                dense
              >
              </v-text-field>
            </v-col>
            <v-col cols="3">
              <v-btn color="primary">Save</v-btn>
            </v-col>
          </v-row>
        </v-col>
      </v-row>
    </v-container>
  </v-card>
</template>

<script>
  export default {
    name: "AchievementsCard",
    data() {
      return {
        title: "Time to finish",
        description: "Time take from starting the race to finishing the race",
        resultType: "time",
        latestResult: "0:41",
        result: null
      }
    }
  }
</script>

<style scoped>

</style>