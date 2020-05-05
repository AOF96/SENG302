<template>
    <div>
      <NavBar/>
      <div id="profileBanner">
      </div>
      <div id="profileWrap">
        <div id="sidebarLeft">
          <div class="profileInfoContainer">
            <h3>Profile Info</h3>
            <hr>
            <div class="profileRow">Gender: {{ user.gender }}</div>
            <hr>
            <div class="profileRow">Date of Birth: {{ user.date_of_birth }}</div>
            <hr>
            <div class="profileRow">Email: {{ user.primary_email }}</div>
            <hr>
            <div class="profileRow">Bio: {{ user.bio }}</div>
          </div>
        </div>
        <div id="centreColumn">
          <div id="profilePublicInfo">
            <svg id="profileUserIcon" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24"><path d="M0 0h24v24H0V0z" fill="none"/><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v1c0 .55.45 1 1 1h14c.55 0 1-.45 1-1v-1c0-2.66-5.33-4-8-4z"/></svg>
            <div id="userQuickInfoWrap">
              <h1 id="userName">{{ user.firstname }} {{user.middlename}} {{ user.lastname }} <span id="userNickname">({{ user.nickname }})</span></h1>
              <h2 class="userFitnessLevel">Fitness Level: {{ fitnessDict[user.fitness] }}</h2>
            </div>

            <router-link to="/settings/profile" id="profileEditButton">Edit profile</router-link>
            <div class="floatClear"></div>
          </div>

          <div id="userActivities">
            <router-link to="/activity_settings">
              <button class="addActivityButton" type="button" onclick="">Add Activity</button>
            </router-link>
            <h2 id="activityTitle">Activities</h2>
            <h3>Activity Types:</h3>
            <ul id="activityTypesList">
              <li  v-for="type in user.activities" v-bind:key="type">
                {{type}}
              </li>
            </ul>
            <hr>
            <h3 style="display: inline-block">Duration Activities:</h3>
            <div class="activitySummaryDiv" v-for="activity in user.dur_activities" v-bind:key="activity">
              {{activity.name}}<br> <h2 class="userFitnessLevel">{{activity.description}}</h2>

            </div>
            <hr>
            <h3 style="display: inline-block">Continuous Activities:</h3>

            <div class="activitySummaryDiv" v-for="activity in user.cont_activities" v-bind:key="activity">
              {{activity.name}}<br> <h2 class="userFitnessLevel">{{activity.description}}</h2>
              <button id="deleteActivityButton" type="button" v-on:click="deleteContinuousActivity(activity)">Delete Activity</button>
            </div>
          </div>

        </div>
        <div id="sidebarRight">
          <PassportCountries/>
        </div>
      </div>
    </div>
</template>

<script>
  import {mapActions, mapGetters} from 'vuex';

  import NavBar from '@/components/NavBar';
  import PassportCountries from '@/components/modules/passportCountries';
  import json from '../../public/json/data.json';
  import axios from "axios";
  import {apiActivity} from "../api";
  const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all'

  export default {
    name: "Profile",
    components: {
      NavBar,
      PassportCountries
    },
    computed: {
      ...mapGetters(['user'])
    },
    data: function () {
      return {
        myJson: json,
        showNewButton: false,
        notFull: true,
        textInput: "",
        adding_country: "Passport Countries",
        countries_option: [],
        country:'',
        fitnessDict: {
          0: "I never exercise", 1: "I can walk a short distance", 2: "I can jog a short distance",
          3: "I can run a medium distance", 4: "I can run a marathon"
        }
      }
    },

    mounted() {
      this.startUp();
        axios.get(COUNTRIES_URL)
                .then((response) => {
                  const countries = []
                  const data = response.data
                  for (let country in data) {
                    let country_name = data[country].name
                    countries.push(country_name)
                  }

                  for(let country of this.user.passports) {
                    const index = countries.indexOf(country)
                    if (index === -1) continue
                    countries.splice(index, 1)
                  }
                  this.countries_option = countries
                })
                .catch(error => console.log(error))
      },
    methods: {
      ...mapActions(['updatePassports']),
      ...mapActions(['updateUserDurationActivities']),
      ...mapActions(['updateUserContinuousActivities']),

      startUp() {
        console.log('init')
        this.user.passports = this.user.passports.slice()
      },
      deleteDurationActivity(activity) {
        let index = this.user.dur_activities.indexOf(activity);
        this.user.dur_activities.splice(index, 1);
        apiActivity.deleteActivity(this.user.profile_id, this.$route.params.activityId);
      },
      deleteContinuousActivity(activity) {
        let index = this.user.cont_activities.indexOf(activity);
        this.user.cont_activities.splice(index, 1);
        apiActivity.deleteActivity(this.user.profile_id, this.$route.params.activityId);
        this.updateUserContinuousActivities(this.user.cont_activities);
      }
    }
  }
</script>

<style scoped>
  .form-popup {
    display: none;
    position: fixed;
    bottom: 0;
    right: 15px;
    border: 3px solid #f1f1f1;
    z-index: 9;
  }

  .btn {
    border: black;
    background-color: inherit;
    padding: 14px 28px;
    font-size: 16px;
    cursor: pointer;
    display: inline-block;
  }

  /* Green */
  .btn:hover {
    background-color: #4CAF50;
    color: white;
  }
  body{
    padding-top:20px;
    padding-left: 10px;
  }

  button {
      margin-left: 8px;
  }
</style>
