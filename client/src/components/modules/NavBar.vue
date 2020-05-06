<template>
    <header class="navBarContainer">
        <router-link to="/logout" v-if="user.isLogin">
            <button class="navBarButton" v-on:click="logoutUser">
                Logout
            </button>
        </router-link>

        <router-link to="/profile" v-if="user.isLogin">
            <button class="navBarButton" v-on:click="goToProfile">
                Profile
            </button>
        </router-link>

        <router-link to="/signup" v-if="!user.isLogin">
            <button class="navBarButton" name ="Sign Up">
                Sign Up 
            </button>
        </router-link>
        <router-link to="/Login" v-if="!user.isLogin"> 
            <button class="navBarButton" value ="Login In">
                Login
            </button>
        </router-link>
    </header>
</template>

<script>
    import { mapGetters, mapActions } from 'vuex';
    import {apiUser} from "../../api";

    export default {
        name: "NavBar",
        computed: {
            ...mapGetters(['user'])
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
                apiUser.logout();
                this.resetUser();
            }
        }
    }
</script>
