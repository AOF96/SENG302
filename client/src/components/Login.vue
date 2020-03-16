<template>
  <div>
    <NavBar/>
    <div id="loginBox" class="credentials-box-wrap">
      <div id="credentials-box">
        <h1>Login</h1>
        <h2>Sign in to your account</h2>
        <form @submit.prevent>
          <div class="signup-row">
            <input type="email" v-model="user.email" class="loginInput-email" name="email" placeholder="Email"
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
  //import {getEncryptPassword} from "../common.js"

  import NavBar from '@/components/NavBar'
  // import {userInfo} from '../globals';

  const SERVER_URL = 'http://localhost:9499';

  export default {
    name: 'Login',
    components: {
      NavBar
    },
    // data() {
    //   return {
    //     email: "",
    //     password: ""
    //   }
    // },
    computed: {
      ...mapGetters(['user']),

    },
    methods: {
      ...mapActions(['updateUserProfile']),

      submitLogin() {
        console.log(this.user.email.trim());
        console.log(this.user.password.trim());
        if (this.user.email.trim(), this.user.password.trim()) {
          axios.post(SERVER_URL + '/login', {
            email: this.user.email,
            password: this.user.password,
          })
            .then((response) => {
              var responseData = response.data;
              var responseCode = response.status;

              if (responseCode == 201) {
                console.log(responseData);
                console.log(responseCode);


                this.updateUserProfile(responseData[0]);



                // userInfo.profile_id = responseData.profile_id;
                // userInfo.firstname = responseData.firstname;
                // userInfo.lastname = responseData.lastname;
                // userInfo.middlename = responseData.middlename;
                // userInfo.nickname = responseData.nickname;
                // userInfo.gender = responseData.gender;
                // userInfo.primary_email = responseData.primary_email;
                // userInfo.birthday = responseData.date_of_birth;
                // userInfo.isLogin = true;

                router.push('Profile');
              } else {
                  alert(responseData);
              }
            }, (error) => {
                console.log(error);
            })
        }
      },
      setCookie(cname, cvalue, exdays) {
        var d = new Date();
        d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
        var expires = "expires=" + d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
      },
    }
  }
</script>
