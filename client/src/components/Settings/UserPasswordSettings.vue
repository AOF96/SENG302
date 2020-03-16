<template>
  <div id="settingsWrap">
    <UserSettingsMenu/>
    <div class="settingsContent">
      <h1>Change Password</h1>
      <hr>
      <div>
        <form @submit.prevent>
            <input class="changePasswordFeild" type="password" name="password" placeholder="Current Password" v-model="password">
            <input class="changePasswordFeild" type="password" name="newPassword" placeholder="New Password" v-model="newPassword">
            <input class="changePasswordFeild" type="password" name="confirmPassword" placeholder="Re-enter Password" v-model="confirmPassword">
            <button id="settingsPasswordSubmit" v-on:click="submitPasswordChange()" type="submit">Change Password</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
  import UserSettingsMenu from '@/components/Settings/UserSettingsMenu'
  import axios from 'axios'
  import {
      userInfo,
      helper
  } from "../../globals";


  const SERVER_URL = 'http://localhost:9499';
  export default {
    components: {
      UserSettingsMenu
    },
    data () {
      return {
          profileId: userInfo.profileId,
          password: '',
          newPassword: '',
          confirmPassword: '',
      }
    },
    computed: {
      validation() {
        return {
          oldPassword: this.password !== '',
          match: this.newPassword == this.confirmPassword,
          length: /.{8,}/.test(this.newPassword),
          number: /\d/.test(this.newPassword),
          uppercase: /[A-Z]/.test(this.newPassword),
        }
      }
    },
    methods: {
      submitPasswordChange() {
        if(this.validation.oldPassword){
          if(this.validation.match){
            if(this.validation.length){
              if(this.validation.number){
                if(this.validation.uppercase){
                    axios(SERVER_URL + '/profiles/' + userInfo.profileId + '/password', {
                      method: "put",
                      withCredentials: true,
                      headers: {'Authorization': helper.getCookie("s_id")},
                      data: {
                          old_password: this.password,
                          new_password: this.newPassword,
                          repeat_password: this.confirmPassword
                      }
                    });
                    console.log("Request sent");
                  alert("Password updated")
                }else{alert("Must contain at least one uppercase character.");}
              }else{alert("Must contain at least one number.");}
            }else{alert("Password length must at least be 8");}
          }else{alert("New passwords must match");}
        }else{alert("Enter your old password");}
      }
    }
  }
</script>
