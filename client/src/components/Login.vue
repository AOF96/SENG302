<template>
  <div>
    <NavBar/>
    <div id="loginBox" class="credentials-box-wrap">
      <div id="credentials-box">
        <h1>Login</h1>
        <h2>Sign in to your account</h2>
        <form @submit.prevent>
          <div class="signup-row">
            <h6 class="login_error" id="email_exist" hidden="true">Account does not exist</h6>
            <h6 class="login_error" id="empty_fields" hidden="true">Please enter email/password</h6>
          </div>
          <div class="signup-row">
            <input type="email" v-model="user.primary_email" class="loginInput-email" name="email" placeholder="Email"
              required>
          </div>
          <div class="signup-row">
            <h6 class="login_error" id="incorrect_password" hidden="true">Incorrect Password</h6>
          </div>
          <div class="signup-row">
            <input type="password" v-model="user.password" class="loginInput-password" name="password"
              placeholder="Password" required>
          </div>
          <div class="signup-row">
            <h6 class="login_error" id="other_error" hidden="true"></h6>
          </div>
          <hr>
          <input type="submit" v-on:click="submitLogin()" id="signupButton-submit" value="Login">
        </form>
      </div>
      <h4>Don't have an account?
          <router-link to="/signup">Sign Up</router-link>
      </h4>
    </div>
  </div>
</template>

<script>
  import router from "../router";
  import { mapGetters, mapActions} from 'vuex';
  import { apiUser } from '../api'
  //import {getEncryptPassword} from "../common.js"
  //import store from '../store/index.js';

  import NavBar from '@/components/NavBar'

  export default {
    name: 'Login',
    components: {
      NavBar
    },
    computed: {
      ...mapGetters(['user','adminUser']),

    },
    methods: {
      ...mapActions(['updateUserProfile','loginAdminUser']),

      /*
        Sanitizes the email and password provided. Sends a request to the server side and provides appropriate error
        messages if the email or password provided is wrong. Server side provides a cookie if the login was successful
      */
      submitLogin() {
        console.log(typeof(this.user.primary_email));
        if (this.user.primary_email == null || this.user.password == null){
          document.getElementById("empty_fields").hidden = false;
          return;
        }
        if (this.user.primary_email.trim(), this.user.password.trim()) {
          apiUser.login(this.user.primary_email, this.user.password).then((response) => {
            const responseData = response.data;
            console.log(responseData)
            //Save token to local storage
            localStorage.setItem('thisUser', responseData[0].profile_id);
            localStorage.setItem('userLoggedIn', 'true');
            localStorage.setItem("s_id", responseData[1]["sessionToken"]);
            apiUser.refreshInstance();
            if(responseData[0].permission_level == 2){
              console.log(responseData[0].permission_level);
              this.loginAdminUser(responseData[0]);
              router.push('/settings/admin_dashboard');
            } else {
              this.updateUserProfile(responseData[0]);
              router.push('Profile');
            }
          }, (error) => {
            const responseData = error.response.data;
            const responseCode = error.response.status;
            console.log(responseCode + ": " + responseData);

            if (responseCode === 403 && responseData === "Email does not exist") {
              document.getElementById("email_exist").hidden = false;
              document.getElementById("incorrect_password").hidden = true;
              document.getElementById("empty_fields").hidden = true;
              document.getElementById("other_error").hidden = true;
            } else if (responseCode === 403 && responseData === "Incorrect password") {
              document.getElementById("incorrect_password").hidden = false;
              document.getElementById("empty_fields").hidden = true;
              document.getElementById("email_exist").hidden = true;
              document.getElementById("other_error").hidden = true;
            }else if (responseCode === 403 && responseData === "Please enter email/password") {
              document.getElementById("incorrect_password").hidden = true;
              document.getElementById("empty_fields").hidden = false;
              document.getElementById("email_exist").hidden = true;
              document.getElementById("other_error").hidden = true;
            }
            else {
              document.getElementById("email_exist").hidden = true;
              document.getElementById("incorrect_password").hidden = true;
              document.getElementById("empty_fields").hidden = true;
              document.getElementById("other_error").hidden = false;
              document.getElementById("other_error").innerText = responseData;
            }
          });
        }
      }
    }
  }
</script>
