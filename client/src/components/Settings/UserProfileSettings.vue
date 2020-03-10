<template>
<div id="settingsWrap">
    <UserSettingsMenu />
    <div class="settingsContent">
        <h1>Edit Profile Info</h1>
        <hr>
        <form @submit.prevent>
            <h2>First Name</h2>
            <input class="editProfileInput" type="text" name="fname" v-model="fname" placeholder="First Name*" required>
            <h2>Last Name</h2>
            <input class="editProfileInput" type="text" name="lname" v-model="lname" placeholder="Last Name*" required>
            <h2>Nickname</h2>
            <input class="editProfileInput" type="text" name="nickname" v-model="nickname" placeholder="Nickname">
            <h2>Gender</h2>
            <select class="editProfileInput editProfileInputGender" v-model="gender" name="gender" placeholder="Gender" value="Gender" required>
                <option selected disabled hidden>Gender</option>
                <option>Non-Binary</option>
                <option>Female</option>
                <option>Male</option>
            </select>

            <h2>Passport Countries</h2>
            <select class="editProfileInput" v-model="passportCountries"  placeholder="Passport Countries" value="Passport Countries" required>
                <option selected disabled hidden>Passport Countries</option>
                <option v-for="country in countries" v-bind:key="country">{{country}}</option>
            </select>

            <h2>Fitness Level</h2>
            <select class="editProfileInput editProfileInputGender" v-model="fitnesslevel" name="fitnesslevel" placeholder="fitnesslevel" value="fitnesslevel" required>
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
            <button id="settingsProfileSubmit" v-on:click="updateUserInfo()" type="submit">Update Profile</button>
        </form>
    </div>
</div>
</template>

<script>
    import axios from 'axios'
import UserSettingsMenu from '@/components/Settings/UserSettingsMenu'
const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all?fields=name'
import {
    userInfo
} from "../../globals";

export default {
    components: {
        UserSettingsMenu
    },
    data() {
        return {
            fname: userInfo.firstname,
            lname: userInfo.lastname,
            nickname: userInfo.nickname,
            gender: userInfo.gender,
            birthday: userInfo.birthday,
            passportCountries: userInfo.passportCountries,
            countries: [],
            bio: userInfo.bio,
            fitnesslevel: userInfo.fitnesslevel,


        }
    },
    mounted() {
            axios.get(COUNTRIES_URL)
                .then((response) => {
                    const data = response.data;
                    const countries = []
                    for (let country in data) {
                        countries.push(data[country].name)
                    }
                    this.countries = countries;
                    console.log(countries);
                })
                .catch(error => console.log(error));
        },
    methods: {
        updateUserInfo() {
            if (this.fname != "" && this.lname != "" && this.gender != "" && this.birthday != "" && this.fitnesslevel != "") {
                userInfo.firstname = this.fname;
                userInfo.lastname = this.lname;
                userInfo.nickname = this.nickname;
                userInfo.gender = this.gender;
                userInfo.birthday = this.birthday;
                userInfo.bio = this.bio;
                userInfo.fitnesslevel = this.fitnesslevel;
                alert("Profile info updated.");
            }
        }
    }
}
</script>
