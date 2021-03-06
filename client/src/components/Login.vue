<template>
    <div>
        <v-alert type="error" v-model="alertComponent" :timeout="timeout" dismissible prominent>
            {{errorMessage}}
        </v-alert>
        <div class="loginContainer">
            <div class="loginFormContainer">
                <h1>Login</h1>
                <h2>Sign in to your account</h2>
                <form @submit.prevent>
                    <div class="loginRow">
                        <h6 class="loginErrorMessages" id="login-top-err-msg" v-if="topErrorMsg">{{ topErrorMsg }}</h6>
                    </div>
                    <div class="loginRow">
                        <input type="email" v-model="user.primary_email" name="email" placeholder="Email"
                               required id="loginEmailForm">
                    </div>
                    <div class="loginRow">
                        <h6 class="loginErrorMessages" id="incorrect_password" v-if="passwordErrorMsg">{{ passwordErrorMsg
                            }}</h6>
                    </div>
                    <div class="loginRow">
                        <input type="password" v-model="user.password" name="password"
                               placeholder="Password" required id="loginPasswordForm">
                    </div>
                    <div class="loginRow">
                        <h6 class="loginErrorMessages" id="other_error" v-if="otherErrorMsg">{{ otherErrorMsg }}></h6>
                    </div>
                    <div class="loginRow">
                        <button v-on:click="submitLogin()"
                                class="loginButton"
                               id="loginButton"
                        >
                            Login
                        </button>
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

  export default {
    name: 'Login',
    data: function () {
      return {
        topErrorMsg: "",
        passwordErrorMsg: "",
        otherErrorMsg: "",
        loadingLogin: false,
        timeout: 3000 ,
        alertComponent: false,
        errorMessage: null,

      }
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
        this.topErrorMsg = "";
        this.passwordErrorMsg = "";
        this.otherErrorMsg = "";
        this.loadingLogin = true;

        if (!this.user.primary_email || !this.user.password) {
          this.errorMessage = "Please enter email or password";
          this.alertComponent = true;
          this.loadingLogin = false;
          return;
        }
        if (this.user.primary_email.trim(), this.user.password.trim()) {
          apiUser.login(this.user.primary_email, this.user.password)
              .then((response) => {
                const responseData = response.data;
                this.updateUserProfile(responseData);
                this.$router.push('Profile');
                apiUser.getUserContinuousActivities(responseData.profile_id).then((response) => {
                  this.updateUserContinuousActivities(response.data);
                }).catch(err => {
                      this.errorMessage = err;
                      this.alertComponent = true;
                    }
                );
                apiUser.getUserDurationActivities(responseData.profile_id).then((response) => {
                  this.updateUserDurationActivities(response.data);
                }).catch(err => {
                      this.errorMessage = err;
                      this.alertComponent = true;
                    }
                );
                if (responseData.permission_level === 2) {
                  this.$router.push("/settings/admin_dashboard");
                } else {
                  this.$router.push("profile?u=" + responseData.profile_id);
                }
              })
              .catch(error => {
                const responseData = error.response.data;
                const responseCode = error.response.status;
                this.loadingLogin = false;

              if (responseCode === 403 && responseData === "Wrong username or password") {
                this.errorMessage = "Wrong username or password";
                this.alertComponent = true;

              } else if (responseCode === 403 && responseData === "Please enter email/password") {
                this.errorMessage = "Please enter email/password";
                this.alertComponent = true;

              } else {
                this.errorMessage = "Please enter email/password";
                this.alertComponent = true;
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
