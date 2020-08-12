<template>
  <v-card class="activityPageCard">
    <h2>Achievements</h2>
    <v-container class="achievementsContainer">
      <v-card class="achievementCard" v-for="achievement in achievements" v-bind:key="achievement.id" flat outlined>
        <v-container>
          <v-row>
            <v-col class="achievementCol">
              <h3>{{achievement.title}}</h3>
            </v-col>
            <v-col class="achievementCol">
              <v-tooltip top max-width="500px">
                <template v-slot:activator="{ on }">
                  <v-icon v-on="on">mdi-help-circle-outline</v-icon>
                </template>
                <span style="color: white;">{{achievement.description}}</span>
              </v-tooltip>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="5" class="achievementCol">
              <p class="achievementText">Latest Result: {{latestResult}}</p>
            </v-col>
            <v-col cols="4" class="achievementCol">
              <v-menu v-if="achievement.resultType === 'time'"
                      ref="menu"
                      v-model="menu2"
                      :close-on-content-click="false"
                      :nudge-right="40"
                      :return-value.sync="result"
                      transition="scale-transition"
                      offset-y
                      max-width="290px"
                      min-width="290px"
              >
                <template v-slot:activator="{ on, attrs }">
                  <v-text-field
                          v-model="result"
                          label="New Result"
                          outlined
                          dense
                          readonly
                          v-bind="attrs"
                          v-on="on"
                  />
                </template>
                <v-time-picker
                        ampm-in-title
                        v-if="menu2"
                        v-model="result"
                        full-width
                        @click:minute="$refs.menu.save(result)"
                />
              </v-menu>

              <v-text-field v-else
                label="New Result"
                outlined
                dense
              >
              </v-text-field>
            </v-col>
            <v-col cols="3" class="achievementCol">
              <v-btn color="primary" class="achievementSaveButton" outlined>Save</v-btn>
            </v-col>
          </v-row>
        </v-container>
      </v-card>
    </v-container>
  </v-card>
</template>

<script>
  export default {
    name: "AchievementsCard",
    data() {
      return {
        achievements: [
          {
            id: 0,
            title: "Time to finish",
            description: "Time take from starting the race to finishing the race. Time take from starting the race to finishing the race. Time take from starting the race to finishing the race. Time take from starting the race to finishing the race",
            resultType: "time",
          },
          {
            id: 1,
            title: "Time to win",
            description: "Time take from starting the race to finishing the race",
            resultType: "distance",
          },
          {
            id: 2,
            title: "Time to lose",
            description: "Time take from starting the race to finishing the race",
            resultType: "word",
          }
        ],

        latestResult: "0:41",
        result: null,
        menu2: false
      }
    }
  }
</script>

<style scoped>

</style>