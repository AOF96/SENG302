<template>
    <div>
      <NavBar/>
      <div id="profileBanner">
      </div>
      <div id="profileWrap">
        <div id="sidebarLeft">
          <div class="profileInfo">
            <table id ="profileTable">
              <tr>
                <td class="profileTableTd" col width = "150">Gender:</td>
                <td class="profileTableTd">{{ user.gender }}</td>
              </tr>
              <tr>
                <td class="profileTableTd">DOB:</td>
                <td class="profileTableTd">{{ user.date_of_birth }}</td>
              </tr>
              <tr>
                <td class="profileTableTd">Primary Email:</td>
                <td class="profileTableTd">{{ user.primary_email }}</td>
              </tr>
              <tr>
                <td class="profileTableTd">Bio:</td>
                <td class="profileTableTd">{{ user.bio }}</td>
              </tr>
              <tr>
                <td class="profileTableTd">Secondary Emails:</td>
                <div style="margin-top: 4px" v-for="email in user.additional_email" v-bind:key="email">
                  <p >{{email}}</p>
                </div>
              </tr>

            </table>

            <router-link to="/activity_settings">
              <button type="button" onclick="">Add Activity</button>
            </router-link>
          </div>
        </div>
        <div id="centreColumn">
          <div id="profilePublicInfo">
            <svg id="profileUserIcon" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24"><path d="M0 0h24v24H0V0z" fill="none"/><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v1c0 .55.45 1 1 1h14c.55 0 1-.45 1-1v-1c0-2.66-5.33-4-8-4z"/></svg>
            <div id="userQuickInfoWrap">
              <h1 id="userName">{{ user.firstname }} {{user.middlename}} {{ user.lastname }} <span id="userNickname">({{ user.nickname }})</span></h1>
              <h2 id="userFitnessLevel">Fitness Level: {{ fitnessDict[user.fitness] }}</h2>
            </div>



            <router-link to="/settings/profile" id="profileEditButton">Edit profile</router-link>
            <div class="floatClear"></div>
          </div>

          <div id="userActivities">
<!--              <table>-->


                <h3>Activity types:</h3>
<!--              <tr>-->
<!--                <td class="profileTableTd">Activity types:</td>-->
<!--                <td class="profileTableTd">{{ user.activities }}</td>-->
<!--              </tr>-->
<!--              <tr>-->
<!--                <td class="profileTableTd">Continuous Activities:</td>-->
<!--                <td class="profileTableTd">{{ user.cont_activities }}</td>-->
<!--              </tr>-->
<!--              <tr>-->
                <h3 style="display: inline-block">Duration Activities:</h3>
              <hr>
                <div  v-for="activity in user.dur_activities" v-bind:key="activity">
                    <label >Name:</label> {{activity.name}}<br> <label>Description:</label> {{activity.description}} <hr>

                </div>
<!--              </tr>-->
<!--              </table>-->

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
      startUp() {
        console.log('init')
        this.user.passports = this.user.passports.slice()
      },
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

  .profileInfo {
      width: 100%;
      margin: auto;
      background: white;
      box-shadow: 0 2px 2px 0 rgba(0,0,0,0.14), 0 3px 1px -2px rgba(0,0,0,0.12), 0 1px 5px 0 rgba(0,0,0,0.20);
      border-radius: 14px;
    font-family: Roboto;
  }
</style>
