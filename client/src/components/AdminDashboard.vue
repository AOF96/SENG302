<template>
  <div>
    <div class="searchUserWrapper">
      <div>
          <v-snackbar outlined color="error" :timeout="timeout" v-model="snackbar" top>{{errorMessage}}</v-snackbar>
          <v-card class="ma-2 searchActivityCard" style="border-radius:15px;">
            <h1 style="text-align: center; color: var(--v-primaryText-base)">Admin Dashboard</h1>
            <v-text-field id="defaultAdminSearchInput"
                          label="Search User By ID" v-model="searchedUser.profile_id" outlined rounded clearable
                          hide-details dense></v-text-field>
            <v-btn class="genericConfirmButton" v-on:click="goToSearchedUser()">Search User</v-btn>
        </v-card>
      </div>
    </div>
  </div>
</template>

<script>

  import {apiUser} from "../api";

  export default {
    name: "AdminDashboard",
    data: function () {
      return {
        searchedUser: {},
        snackbar: false,
        errorMessage: ""
      }
    },
    mounted() {
      if (!this.user.isLogin || !this.user.isAdmin()) {
        this.$router.push('/login');
      }
    },
    methods: {
      async goToSearchedUser() {
        var tempSearchedUser = await apiUser.getUserById(this.searchedUser.profile_id)
        if (tempSearchedUser == "Invalid permissions" || tempSearchedUser.permission_level == 2) {
          this.errorMessage = "User does not exist";
          this.snackbar = true;
        } else {
          this.$router.push('/profile/' + this.searchedUser.profile_id);
        }
      }
    }
  }
</script>

<style scoped>

  /* The overlay effect with black background */
  .overlay {
    height: 100%;
    width: 100%;
    display: none;
    position: fixed;
    z-index: 1;
    top: 0;
    left: 0;
    background-color: grey;
    background-color: rgba(0, 0, 0, 0.9); /* Black with a little bit see-through */
  }

  /* The content */
  .overlay-content {
    position: relative;
    top: 46%;
    width: 80%;
    text-align: center;
    margin-top: 30px;
    margin: auto;
  }

  /* Close button */
  .overlay .closebtn {
    position: absolute;
    top: 20px;
    right: 45px;
    font-size: 60px;
    cursor: pointer;
    color: white;
  }

  .overlay .closebtn:hover {
    color: #ccc;
  }

  /* Style the search field */
  .overlay input[type=text] {
    padding: 15px;
    font-size: 17px;
    border: none;
    float: left;
    width: 80%;
    background: white;
  }

  .overlay input[type=text]:hover {
    background: #f1f1f1;
  }

  /* Style the submit button */
  .overlay button {
    float: left;
    width: 20%;
    padding: 15px;
    background: #ddd;
    font-size: 17px;
    border: none;
    cursor: pointer;
  }

  .overlay button:hover {
    background: #bbb;
  }

  #openButton {
    background-color: #1dca92;
    padding: 8px 18px;
    border-radius: 3px;
    border: none;
    color: white;
    font-size: 15px;
    margin: 12px;
    font-weight: 500;
    cursor: pointer;
    border: 2px solid #1dca92;
    -webkit-transition: all 0.2s ease;
    transition: all 0.2s ease;
    border-radius: 100px;
  }

  #defaultAdminSearchInput {
      padding-bottom: 12px;
  }

</style>
