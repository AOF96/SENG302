<template>
    <div>
      <NavBar/>
      <div id="profileBanner">
      </div>
      <div id="profileWrap">
        <div id="profilePublicInfo">
          <svg id="profileUserIcon" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24"><path d="M0 0h24v24H0V0z" fill="none"/><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v1c0 .55.45 1 1 1h14c.55 0 1-.45 1-1v-1c0-2.66-5.33-4-8-4z"/></svg>
          <div id="userQuickInfoWrap">
            <h1 id="userName">{{ user.firstname }} {{ user.lastname }} <span id="userNickname">({{ user.nickname }})</span></h1>
            <h2 id="userFitnessLevel">Fitness Level: {{ user.fitness }}</h2>
          </div>
          <router-link to="/settings/profile" id="profileEditButton">Edit profile</router-link>
          <div class="floatClear"></div>
        </div>
        <PassportCountries/>
      </div>

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
            <td class="profileTableTd">{{ user.additional_email }}</td>
          </tr>
        </table>
      </div>
    </div>
</template>

<script>
  import axios from 'axios'
  import { mapGetters } from 'vuex'

  import NavBar from '@/components/NavBar'
  import PassportCountries from '@/components/modules/passportCountries'
  import json from '../../public/json/data.json'
  const SERVER_URL = 'https://4967d4f4-8301-42d1-a778-e3d150633644.mock.pstmn.io';

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
        textInput: ""
      }

    },
    methods: {
      addNewEmailPanel() {
        this.showNewButton = this.showNewButton !== true;
      },

      addEmail(textInput) {
        if (textInput === "") {
          alert("Please enter a valid email");
        } else {
          axios.post(SERVER_URL + '/editemail', {
            new_email: textInput
          })
            .then((response) => {
              console.log(response.data.msg3);
            }, (error) => {
              console.log(error);
            });
        }
        if (this.test === "") {
            alert("Please enter a valid email");
        } else {
          if (json.secondary_emails.length === 4) {
            alert("You already have 5 emails, delete one if you want to add more.");
            this.showNewButton = false;
            this.notFull = false;
            return;
          }
          json.secondary_emails.push(this.textInput);
          console.log(json.secondary_emails.length);
        }
      }
    }
  }
</script>

<style scoped>
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
      width: 760px;
      margin: auto;


      background: white;
      box-shadow: 0 2px 2px 0 rgba(0,0,0,0.14), 0 3px 1px -2px rgba(0,0,0,0.12), 0 1px 5px 0 rgba(0,0,0,0.20);
      border-radius: 14px;

      font-family: Roboto;
  }

    .profileInfoP {
        padding: 1px 10px;
        font-family: Roboto;
    }
</style>
