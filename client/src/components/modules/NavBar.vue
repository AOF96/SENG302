<template>
  <div>
    <header class="navBarContainer">
      <svg id="headerNavToggle" @click.stop="navDrawer = !navDrawer" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24"><path d="M0 0h24v24H0z" fill="none"/><path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/></svg>

      <router-link to="/logout" v-if="user.isLogin">
        <button class="navBarButton" v-on:click="logoutUser">Logout</button>
      </router-link>

      <router-link v-bind:to="'/profile/'+user.profile_id" v-if="user.permission_level < 2 && user.isLogin">
        <button class="myaccount navBarButton">Profile</button>
      </router-link>

      <router-link v-bind:to="'/search'" v-if="user.isLogin">
        <button class="myaccount navBarButton" id="navBarSearchBtn"><svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24"><path d="M0 0h24v24H0z" fill="none"/><path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/></svg>Search</button>
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
    <v-navigation-drawer
      class="accent-4"
      style="z-index:1000;"
      v-model="navDrawer"
      fixed
    >
    <v-list
        dense
        nav
        class="py-0"
      >
        <v-list-item two-line>
          <v-list-item-avatar>
            <img src="/favicon.png">
          </v-list-item-avatar>

          <v-list-item-content>
            <v-list-item-title>Passage</v-list-item-title>
            <v-list-item-subtitle>Signed In</v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>

        <v-divider></v-divider>
        <v-list-item-group
          active-class="text--accent-4"
        >
          <v-list-item link class="mt-2" v-bind:to="'/profile/'+user.profile_id" v-if="user.permission_level < 2 && user.isLogin">
            <v-list-item-icon>
              <v-icon>dashboard</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>Home</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item link class="mt-2" v-bind:to="'/profile/'+user.profile_id" v-if="user.permission_level < 2 && user.isLogin">
            <v-list-item-icon>
              <v-icon>account_box</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>Profile</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item link class="mt-2" v-bind:to="'/search'" v-if="user.isLogin">
            <v-list-item-icon>
              <v-icon>search</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>Search</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item link class="mt-2" v-bind:to="'/settings/profile/'+user.profile_id" v-if="user.permission_level < 2 && user.isLogin">
            <v-list-item-icon>
              <v-icon>settings</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>Settings</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item link class="mt-2" v-bind:to="'/settings/admin_dashboard'" v-if="isAdmin && user.isLogin">
            <v-list-item-icon>
              <v-icon>gavel</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>Admin Dashboard</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
      <template v-slot:append>
        <div class="pa-2">
          <v-btn block color="#c9c9c9">Logout</v-btn>
        </div>
      </template>
    </v-navigation-drawer>
  </div>
</template>

<script>
    import { mapGetters, mapActions } from 'vuex';
    import {apiUser} from "../../api";

    export default {
        name: "NavBar",
        data: function () {
          return {
            navDrawer: false,
          }
        },
        computed: {
            ...mapGetters(["user", "isAdmin"]),
        },
        methods: {
            ...mapActions(['logout']),
            ...mapActions(['updateUserProfile']),
            ...mapActions(['updateUserContinuousActivities']),
            ...mapActions(['updateUserDurationActivities']),
            /*
                Redirects the user into the profile page. Refreshes the data by making a request to the server side.
            */
            goToProfile() {
                apiUser.refreshUserData(this.user.profile_id).then((response) => {
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
        },
    }
</script>
