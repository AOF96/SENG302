<template>
<div id="settingsWrap">
    <UserSettingsMenu />
    <div class="settingsContent">
        <h1>Edit Profile Info</h1>
        <hr>
        <form @submit.prevent>
            <h2>First Name</h2>
            <input class="editProfileInput" type="text" name="fname" v-model="firstname" placeholder="First Name*" required>
            <h2>Last Name</h2>
            <input class="editProfileInput" type="text" name="lname" v-model="lastname" placeholder="Last Name*" required>
            <h2>Nickname</h2>
            <input class="editProfileInput" type="text" name="nickname" v-model="nickname" placeholder="Nickname">
            <h2>Gender</h2>
            <select class="editProfileInput editProfileInputGender" v-model="gender" name="gender" placeholder="Gender" value="Gender" required>
                <option selected disabled hidden>Gender</option>
                <option>Non-Binary</option>
                <option>Female</option>
                <option>Male</option>
            </select>
            <h2>Fitness Level</h2>
            <select class="editProfileInput editProfileInputGender" v-model="fitness" name="fitnesslevel" placeholder="fitness" value="fitness" required>
                <option selected disabled hidden>Fitness Level</option>
                <option >0</option>
                <option >1</option>
                <option >2</option>
                <option >3</option>
                <option >4</option>
                <option >5</option>
            </select>
            <h2>Birthday</h2>
            <input v-model="birthday" class="editProfileInput" name="birthday" type="date" required>
            <h2>Bio</h2>
            <textarea class="editProfileTextarea" name="bio" v-model="bio" placeholder="Write about yourself"></textarea>
            <button id="settingsProfileSubmit" v-on:click="updateProfile()" type="submit">Update Profile</button>
        </form>
    </div>
</div>
</template>

<script>
import UserSettingsMenu from '@/components/Settings/UserSettingsMenu'
import {
    userInfo
} from "../../globals";
import axios from "axios";
import router from "../../router";

const SERVER_URL = 'https://abf397d3-e348-43f3-965f-c8fba9ee56f1.mock.pstmn.io';

export default {
    components: {
        UserSettingsMenu
    },
    data() {
        return {
            firstname: userInfo.firstname,
            lastname: userInfo.lastname,
            nickname: userInfo.nickname,
            gender: userInfo.gender,
            birthday: userInfo.birthday,
            bio: userInfo.bio,
            fitness: userInfo.fitness,
            profile_id: userInfo.profile_id
        }
    },
    methods: {
        updateUserInfo() {
            if(this.firstname != "" && this.lastname != "" && this.gender != "" && this.birthday != "" && this.fitness != ""){
              userInfo.firstname = this.firstname;
              userInfo.lastname = this.lastname;
              userInfo.nickname = this.nickname;
              userInfo.gender = this.gender;
              userInfo.birthday = this.birthday;
              userInfo.bio = this.bio;
              userInfo.fitness = this.fitness;
              alert("Profile info updated.");
            }
        },

        updateProfile() {
            axios.put(SERVER_URL + '/profiles/' + '1', {
                firstname: this.firstname,
                lastname: this.lastname,
                nickname: this.nickname,
                gender: this.gender,
                bio: this.bio,
                primary_email: this.email,
                birthday: this.birthday,
                fitness: this.fitness
            })
            .then((response) => {
                console.log(response);
                router.push('Profile');
            }, (error) => {
                console.log(error)
            })
        }
    }
}
</script>
