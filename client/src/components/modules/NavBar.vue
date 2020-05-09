<template>
  <header class="navBarContainer">
    <router-link to="/logout" v-if="user.isLogin">
      <button class="navBarButton" v-on:click="logoutUser">Logout</button>
    </router-link>

    <router-link v-bind:to="'/profile/'+user.profile_id" v-if="user.permission_level < 2 && user.isLogin">
      <button class="myaccount navBarButton" >Profile</button>
    </router-link>

    <router-link v-bind:to="'/settings/admin_dashboard'" v-if="isAdmin && user.isLogin">
      <button class="navBarButton" >Dashboard</button>
    </router-link>

    <router-link to="/signup" v-if="!user.isLogin">
      <button class="signup navBarButton" name="Sign Up">Sign Up</button>
    </router-link>
    <router-link to="/login" v-if="!user.isLogin">
      <button class="login navBarButton" value="Login In">Login</button>
    </router-link>
  </header>
</template>

<script>
    import { mapGetters, mapActions } from 'vuex';
    import {apiUser} from "../../api";

    export default {
        name: "NavBar",
        computed: {
            ...mapGetters(["user", "isAdmin"]),
        },
        methods: {
            ...mapActions(['logout']),
            ...mapActions(['updateUserProfile']),
            ...mapActions(['resetUser']),
            ...mapActions(['updateUserContinuousActivities']),
            ...mapActions(['updateUserDurationActivities']),
            /*
                Redirects the user into the profile page. Refreshes the data by making a request to the server side.
            */
            goToProfile() {
                apiUser.refreshUserData(this.user.profile_id).then((response) => {
                    console.log(response.data);
                    this.updateUserProfile(response.data);
                });
                apiUser.getUserContinuousActivities(this.user.profile_id).then((response) => {
                    this.updateUserContinuousActivities(response.data);
                });
                apiUser.getUserDurationActivities(this.user.profile_id).then((response) => {
                    this.updateUserDurationActivities(response.data);
                });
            },
            /*
               Sends a request to the server side when to log a user out when the log out button is pressed.
             */
            logoutUser() {
                this.logout();
            }
        }
    }
</script>
