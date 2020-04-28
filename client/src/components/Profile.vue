<template>
    <div>
      <NavBar/>
      <div id="profileBanner">
      </div>
      <div id="profileWrap">
        <div id="profilePublicInfo">
          <svg id="profileUserIcon" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24"><path d="M0 0h24v24H0V0z" fill="none"/><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v1c0 .55.45 1 1 1h14c.55 0 1-.45 1-1v-1c0-2.66-5.33-4-8-4z"/></svg>
          <div id="userQuickInfoWrap">
            <h1 id="userName">{{ user.firstname }} {{user.middlename}} {{ user.lastname }} <span id="userNickname">({{ user.nickname }})</span></h1>
            <h2 id="userFitnessLevel">Fitness Level: {{ fitnessDict[user.fitness] }}</h2>
          </div>
          <router-link to="/settings/profile" id="profileEditButton">Edit profile</router-link>
          <div class="floatClear"></div>
        </div>
        <PassportCountries/>
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
            <tr>
              <td class="profileTableTd">Activities:</td>
              <td class="profileTableTd">{{ user.activities }}</td>
            </tr>
            <tr>
              <td class="profileTableTd">Continuous Activities:</td>
              <td class="profileTableTd">{{ user.cont_activities }}</td>
            </tr>
            <tr>
              <td class="profileTableTd">Duration Activities:</td>
              <td class="profileTableTd">{{ user.dur_activities }}</td>
            </tr>
          </table>
          <button type="button" onclick="document.getElementById('myForm').style.display = 'block'">Add Activity</button>
        </div>

        <div class="form-popup" id="myForm">
          <form action="/action_page.php" class="form-container">
            <h1>New Activity</h1>

            <label for="name"><b>Activity Name</b></label>
            <input type="text" id="name" required>

            <label for="time">Continuous?</label>
            <select id="time">
              <option value="continuous">Continuous</option>
              <option value="duration">Duration</option>
            </select>

            <label for="start_date">Start Date</label>
            <input type = "date" id="start_date">

            <label for="end_date">End Date</label>
            <input type = "date" id="end_date">

            <label for="start_time">Start Time</label>
            <input type = "time" id="start_time">

            <label for="end_time">End Time</label>
            <input type = "time" id="end_time">

            <label for="desc"><b>Description</b></label>
            <input type="text" id="desc">

            <label for="location"><b>Location</b></label>
            <input type="text" id="location">

            <button type="submit" class="btn">Create</button>
            <button type="submit" class="btn cancel" onclick="document.getElementById('myForm').style.display = 'none'">Close</button>
          </form>
        </div>

      </div>
    </div>
</template>

<script>
  import {mapGetters} from 'vuex';

  import NavBar from '@/components/NavBar';
  import PassportCountries from '@/components/modules/passportCountries';
  import json from '../../public/json/data.json';

  export default {
    name: "Profile",
    components: {
        NavBar,
        PassportCountries
    },
    computed: {
      ...mapGetters(['user'])
    },
    data: function() {
      return {
        myJson: json,
        showNewButton: false,
        notFull: true ,
        textInput: "",
        fitnessDict: {0: "I never exercise", 1: "I can walk a short distance", 2: "I can jog a short distance",
          3: "I can run a medium distance", 4: "I can run a marathon"}
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
  .success {
    color: green;
  }

  .btn:hover {
    background-color: #4CAF50;
    color: white;
  }
  body{
    padding-top:20px;
    padding-left: 10px;
  }

  .w3- img {

  }

  #input-field {
       width: 300px;
       margin-top: 15px;
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
      margin-top: 15px;
  }

    .profileInfoP {
        padding: 1px 10px;
        font-family: Roboto;
    }
</style>
