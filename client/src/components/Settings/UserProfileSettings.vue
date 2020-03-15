<template>
<div id="settingsWrap">
    <UserSettingsMenu />
    <div class="settingsContent">
        <h1>Edit Profile Info</h1>
        <hr>
        <form @submit.prevent>
            <h2>First Name</h2>
            <input class="editProfileInput" type="text" name="fname" v-model="user.firstName" placeholder="First Name*" required>
            <h2>Last Name</h2>
            <input class="editProfileInput" type="text" name="lname" v-model="user.lastName" placeholder="Last Name*" required>
            <h2>Nickname</h2>
            <input class="editProfileInput" type="text" name="nickname" v-model="user.nickName" placeholder="Nickname">
            <h2>Gender</h2>
            <select class="editProfileInput editProfileInputGender" v-model="user.gender" name="gender" placeholder="Gender" value="Gender" required>
                <option selected disabled hidden>Gender</option>
                <option>Non-Binary</option>
                <option>Female</option>
                <option>Male</option>
            </select>

            <h2>Fitness Level</h2>
            <select class="editProfileInput editProfileInputGender" v-model="fitnesslevel" name="fitnesslevel" placeholder="fitness" value="fitness" required>
                <option selected disabled hidden>Fitness Level</option>
                <option>0</option>
                <option>1</option>
                <option>2</option>
                <option>3</option>
                <option>4</option>
                <option>5</option>
            </select>
            <h2>Birthday</h2>
            <input v-model="user.birthday" class="editProfileInput" name="birthday" type="date" required>
            <h2>Bio</h2>
            <textarea class="editProfileTextarea" name="bio" v-model="user.bio" placeholder="Write about yourself"></textarea>
            <button id="settingsProfileSubmit" v-on:click="updateUserProfile(user)" type="submit">Update Profile</button>
        </form>
    </div>
</div>
</template>

<script>
import { mapState, mapActions } from 'vuex'

import UserSettingsMenu from '@/components/Settings/UserSettingsMenu'
import axios from "axios";
import router from "../../router";

const SERVER_URL = 'http://localhost:9499';

export default {
    components: {
        UserSettingsMenu
    },
    computed: {
        ...mapState(['user'])
    },
    methods: {
        ...mapActions(['updateUserProfile']),
        methods: {
            updateProfile() {
                axios.put(SERVER_URL + '/profiles/' + this.user.profile_id, {
                    firstname: this.user.firstName,
                    lastname: this.user.lastName,
                    nickname: this.user.nickName,
                    gender: this.gender,
                    bio: this.bio,
                    primary_email: this.user.email,
                    date_of_birth: this.user.birthday,
                    fitness: this.user.fitnesslevel
                })
                .then((response) => {
                    console.log(response);
                    router.push('Profile');
                }, (error) => {
                    console.log(error)
                })
            },
            addPassportCountries() {
                this.passportCountries.num_of_countries++;
            }
        }
    }
}
</script>