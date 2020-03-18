<template>
  <div>
    <NavBar/>
    <div id="loginBox" class="credentials-box-wrap">
      <div id="credentials-box">
        <h1>Login</h1>
        <h2>Sign in to your account</h2>
        <form @submit.prevent>
          <div class="signup-row">
            <input type="email" v-model="user.primary_email" class="loginInput-email" name="email" placeholder="Email"
              required>
          </div>
          <div class="signup-row">
            <input type="password" v-model="user.password" class="loginInput-password" name="password"
              placeholder="Password" required>
          </div>
          <hr>
          <input type="submit" v-on:click="submitLogin()" id="signupButton-submit" value="Login">
        </form>
      </div>
      <h4>Don't have an account?
          <router-link to="/Signup">Sign Up</router-link>
      </h4>
    </div>
  </div>
</template>

<script>
  import axios from 'axios'
  import router from "../router";
  import { mapGetters, mapActions} from 'vuex';
  import { helperFunction } from '../api'
  //import {getEncryptPassword} from "../common.js"

  import NavBar from '@/components/NavBar'
  const SERVER_URL = 'http://localhost:9499';

  export default {
    name: 'Login',
    components: {
      NavBar
    },
    computed: {
      ...mapGetters(['user']),

    },
    methods: {
      ...mapActions(['updateUserProfile']),

      submitLogin() {
        if (this.user.primary_email.trim(), this.user.password.trim()) {
          axios.post(SERVER_URL + '/login', {
            email: this.user.primary_email,
            password: this.user.password,
          })
            .then((response) => {
              var responseData = response.data;
              var responseCode = response.status;

              if (responseCode == 201) {
                console.log(responseData);
                console.log(responseCode);
                helperFunction.addCookie("s_id", responseData.data[1]["sessionToken"], 365);
                this.updateUserProfile(responseData[0]);
                router.push('Profile');
              } else {
                  alert(responseData);
              }
            }, (error) => {
                console.log(error);
            })
        }
      }
    }
  }
</script>
