<template>
  <header class="navBarContainer">
    <router-link to="/logout" v-if="user.isLogin">
      <button id = "navBarLogoutButton" class="navBarButton" v-on:click="logoutUser">Logout</button>
    </router-link>

    <router-link v-bind:to="'/profile/'+user.profile_id" v-if="user.permission_level < 2 && user.isLogin">
      <button class="myaccount navBarButton" id="profileButton">Profile</button>
    </router-link>

    <router-link v-bind:to="'/search'" v-if="user.isLogin">
      <button class="myaccount navBarButton" id="navBarSearchBtn"><svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24"><path d="M0 0h24v24H0z" fill="none"/><path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/></svg>Search</button>
    </router-link>

    <router-link v-bind:to="'/settings/admin_dashboard'" v-if="isAdmin && user.isLogin">
      <button class="navBarButton" id="dashboardButton">Dashboard</button>
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

    export default {
        name: "NavBar",
        computed: {
            ...mapGetters(["user", "isAdmin"]),
        },
        methods: {
            ...mapActions(['logout', 'updateUserProfile','updateUserContinuousActivities', 'updateUserDurationActivities', "getUserContinuousActivities", "getUserDurationActivities", "refreshUserData"]),
            /*
                Redirects the user into the profile page. Refreshes the data by making a request to the server side.
            */
            goToProfile() {
                this.refreshUserData(this.user.profile_id).then((response) => {
                    this.updateUserProfile(response.data);
                });
                this.getUserContinuousActivities(this.user.profile_id).then((response) => {
                    this.updateUserContinuousActivities(response.data);
                });
                this.getUserDurationActivities(this.user.profile_id).then((response) => {
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
