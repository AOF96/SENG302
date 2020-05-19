<template>
<div class="settingsContainer">
    <UserSettingsMenu />
    <div class="settingsContentContainer">
        <router-link v-bind:to="'/profile/'+this.$route.params.profileId">
            <button class="genericConfirmButton">Back to Profile</button>
        </router-link>
        <h1 class="settingsTitle">Edit Profile Info</h1>
        <hr>
        <h6 class="errorMessage" id="error" hidden="true"></h6>
        <h6 class="successMessage" id="success" hidden="true"></h6>
        <form @submit.prevent class="editForm">
            <div id="adminToggle" v-bind:class="{ showadmin: showAdmin }" v-if="user.permission_level == 2 && searchedUser.permission_level != 2 && user.profile_id != searchedUser.profile_id">
                <h2>Enable Admin Abilities</h2>
                <div class="togswitch" :position="searchedUser.permission_level == 1 ? 'on' : 'off'" v-on:click="toggleAdmin()">
                    <div class="togswitchnob"></div>
                    <div class="togswitchnob_touch"></div>
                </div>
                <div class="floatClear"></div>
            </div>
            <h2>First Name</h2>
            <input type="text" name="fname" v-model="searchedUser.firstname" placeholder="First Name*" required>
            <h2>Middle Name</h2>
            <input type="text" name="lname" v-model="searchedUser.middlename" placeholder="Middle Name">
            <h2>Last Name</h2>
            <input type="text" name="lname" v-model="searchedUser.lastname" placeholder="Last Name*" required>
            <h2>Nickname</h2>
            <input type="text" name="nickname" v-model="searchedUser.nickname" placeholder="Nickname">
            <h2>Gender</h2>
            <select v-model="searchedUser.gender" name="gender" placeholder="Gender" value="Gender" required>
                <option selected disabled hidden>Gender</option>
                <option>Non-Binary</option>
                <option>Female</option>
                <option>Male</option>
            </select>
            <h2>Fitness Level</h2>
            <select v-model="searchedUser.fitness" name="fitnesslevel" placeholder="fitness" value="fitness" required>
                <option value = 0>I never exercise</option>
                <option value = 1>I can walk a short distance</option>
                <option value = 2>I can jog a short distance</option>
                <option value = 3>I can run a medium distance</option>
                <option value = 4>I can run a marathon</option>
            </select>
            <h2>Birthday</h2>
            <input v-model="searchedUser.date_of_birth" name="birthday" type="date" required>
            <h2>Bio</h2>
            <textarea maxlength="255" name="bio" v-model="searchedUser.bio" placeholder="Write about yourself"></textarea>
            <button class="genericConfirmButton updateProfileButton" v-on:click="updateProfile()" type="submit">Update Profile</button>
        </form>
    </div>
    <div class="floatClear"></div>
</div>
</template>

<script>

import { mapGetters, mapActions } from 'vuex'
import UserSettingsMenu from './ProfileSettingsMenu';
import {apiUser} from "../../../api";

export default {
    components: {
        UserSettingsMenu
    },
    computed: {
        ...mapGetters(['user']),
    },
    data: function() {
      return {
        searchedUser: {},
        showAdmin: false
      }
    },
    methods: {
        ...mapActions(['logout']),
        ...mapActions(['updateUserProfile']),

        /*
            Sends a request to the server side to update the searchedUser's profile info. Displays error messages if the update
            was unsuccessful.
         */
        updateProfile() {
            console.log(this.searchedUser.fitness);
            apiUser.editProfile(this.searchedUser.profile_id, this.searchedUser.firstname, this.searchedUser.lastname, this.searchedUser.middlename,
                this.searchedUser.nickname, this.searchedUser.primary_email, this.searchedUser.bio, this.searchedUser.date_of_birth, this.searchedUser.gender,
                Number(this.searchedUser.fitness), this.searchedUser.additional_email, this.searchedUser.passports, this.searchedUser.permission_level, this.searchedUser.activities).then((response) => {
                this.updateUserProfile(this.user);
                document.getElementById("success").hidden = false;
                document.getElementById("success").innerText = "Updated Successfully";
                document.getElementById("error").hidden = true;
                console.log(response);
            }, (error) => {
                document.getElementById("error").hidden = false;
                document.getElementById("error").innerText = error.response.data.Errors;
                document.getElementById("success").hidden = true;
                console.log(error);
            });
        },

        toggleAdmin() {
            if(this.searchedUser.permission_level == 1){
                this.searchedUser.permission_level = 0;
            }else if(this.searchedUser.permission_level == 0){
                this.searchedUser.permission_level = 1;
            }
        },

        /*
            Uses user id from url to request user data.
         */
        async loadSearchedUser() {
          if(this.$route.params.profileId == null || this.$route.params.profileId == ""){
            this.$router.push('/settings/profile/'+this.user.profile_id);
            this.searchedUser = this.user;
          }else{
            var tempUserData = await apiUser.getUserById(this.$route.params.profileId);
            if(tempUserData == "Invalid permissions"){
              this.$router.push('/settings/profile/'+this.user.profile_id);
              this.searchedUser = this.user;
            }else{
              this.searchedUser = tempUserData;
            }
          }
          this.showAdmin = true;
        }
    },
    mounted() {
      this.loadSearchedUser();
    }
};
</script>
