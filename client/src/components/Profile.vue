<template>
    <div>
      <NavBar/>
      <div id="profileBanner">
      </div>
      <div id="profileWrap">
          <div id="profilePublicInfo">
            <svg id="profileUserIcon" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24"><path d="M0 0h24v24H0V0z" fill="none"/><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v1c0 .55.45 1 1 1h14c.55 0 1-.45 1-1v-1c0-2.66-5.33-4-8-4z"/></svg>
            <h1 id="userName">{{ firstname }} {{ lastname }} <span id="userNickname">({{ nickname }})</span></h1>
            <router-link to="/settings/profile" id="profileEditButton">Edit profile</router-link>
            <div class="floatClear"></div>
          </div>
          <PassportCountries/>
      </div>
    </div>
</template>
<script>
  import axios from 'axios'
  import NavBar from '@/components/NavBar'
  import PassportCountries from '@/components/modules/passportCountries'
  import json from '../../public/json/data.json'
  import {userInfo} from '../globals';
  const SERVER_URL = 'https://4967d4f4-8301-42d1-a778-e3d150633644.mock.pstmn.io';

  export default {
    name: "Profile",
    components: {
        NavBar,
        PassportCountries
    },
    data: function() {
      return {
          firstname: userInfo.firstname,
          lastname: userInfo.lastname,
          middlename: userInfo.middlename,
          nickname :userInfo.nickname,
          gender:userInfo.gender,
          bio: userInfo.bio,
          email: userInfo.email,
          birthday: userInfo.birthday,
          fitnesslevel: userInfo.fitnesslevel,
          myJson: json,
          showNewButton: false,
          notFull: true ,
          textInput: ""
      }

    },
      // mounted() {
      //   this.getData()
      // },
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

</style>
