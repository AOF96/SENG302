<template>
    <div>
        <NavBar/>
        <div class="loginContainer">
            <div class="loginFormContainer">
                <h1>Login</h1>
                <h2>Sign in to your account</h2>
                <form @submit.prevent>
                    <div class="loginRow">
                        <h6 class="errorMessage" id="login-top-err-msg" v-if="topErrorMsg">{{ topErrorMsg }}</h6>
                    </div>
                    <div class="loginRow">
                        <input type="email" v-model="user.primary_email" name="email" placeholder="Email"
                               required>
                    </div>
                    <div class="loginRow">
                        <h6 class="errorMessage" id="incorrect_password" v-if="passwordErrorMsg">{{ passwordErrorMsg
                            }}</h6>
                    </div>
                    <div class="loginRow">
                        <input type="password" v-model="user.password" name="password"
                               placeholder="Password" required>
                    </div>
                    <div class="loginRow">
                        <h6 class="errorMessage" id="other_error" v-if="otherErrorMsg">{{ otherErrorMsg }}></h6>
                    </div>
                    <hr>
                    <div class="loginRow">
                        <button type="submit" v-on:click="submitLogin()" class="loginButton"> Login</button>
                    </div>
                </form>
            </div>
            <h4>Don't have an account?
                <router-link to="/signup">Sign Up</router-link>
            </h4>
        </div>
    </div>
</template>

<script>
  import {mapActions, mapGetters} from 'vuex';
  import {apiUser} from '../api'

  import NavBar from "./modules/NavBar";

  export default {
    name: 'Login',
    data: function () {
      return {
        topErrorMsg: "",
        passwordErrorMsg: "",
        otherErrorMsg: "",

      }
    },
    components: {
      NavBar
    },
    computed: {
      ...mapGetters(['user']),
    },
    methods: {
      ...mapActions(['updateUserProfile']),
      ...mapActions(['updateUserContinuousActivities']),
      ...mapActions(['updateUserDurationActivities']),

      /*
        Sanitizes the email and password provided. Sends a request to the server side and provides appropriate error
        messages if the email or password provided is wrong. Server side provides a cookie if the login was successful
      */
      submitLogin() {
        this.topErrorMsg = ""
        this.passwordErrorMsg = ""
        this.otherErrorMsg = ""

        if (!this.user.primary_email || !this.user.password) {
          this.topErrorMsg = "Please enter email or password"
          return;
        }
        if (this.user.primary_email.trim(), this.user.password.trim()) {
          apiUser.login(this.user.primary_email, this.user.password)
            .then((response) => {
              const responseData = response.data;
              //Save token to local storage
              localStorage.setItem("s_id", responseData[1]["sessionToken"]);
              apiUser.refreshInstance();
              this.updateUserProfile(responseData[0]);
              this.$router.push('Profile');
              apiUser.getUserContinuousActivities(responseData[0].profile_id).then((response) => {
                this.updateUserContinuousActivities(response.data);
              }).catch(err => console.log(err));
              apiUser.getUserDurationActivities(responseData[0].profile_id).then((response) => {
                this.updateUserDurationActivities(response.data);
              }).catch(err => console.log(err));
              if (responseData[0].permission_level == 2) {
                this.$router.push("/settings/admin_dashboard");
              } else {
                this.$router.push("profile?u=" + responseData[0].profile_id);
              }
            })
            .catch(error => {

              const responseData = error.response.data;
              const responseCode = error.response.status;

              if (responseCode === 403 && responseData === "Email does not exist") {
                this.topErrorMsg = "Account does not exist"

              } else if (responseCode === 403 && responseData === "Incorrect password") {
                this.passwordErrorMsg = "Incorrect Password"

              } else if (responseCode === 403 && responseData === "Please enter email/password") {
                this.topErrorMsg = "Please enter email/password"

              } else {
                this.otherErrorMsg = responseData
              }
            })

          ;
        }
      }
    }
  };
</script>

<style scoped>
    @import '../../public/styles/pages/loginStyle.css';
</style>
