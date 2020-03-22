<template>
<div id="settingsWrap">
    <UserSettingsMenu />
    <div class="settingsContent">
        <h1>Edit Profile Info</h1>
        <hr>
        <h6 class="edit_error" id="error" hidden="true"></h6>
        <h6 class="edit_success" id="success" hidden="true"></h6>
        <form @submit.prevent>
            <h2>First Name</h2>
            <input class="editProfileInput" type="text" name="fname" v-model="user.firstname" placeholder="First Name*" required>
            <h2>Middle Name</h2>
            <input class="editProfileInput" type="text" name="lname" v-model="user.middlename" placeholder="Middle Name">
            <h2>Last Name</h2>
            <input class="editProfileInput" type="text" name="lname" v-model="user.lastname" placeholder="Last Name*" required>
            <h2>Nickname</h2>
            <input class="editProfileInput" type="text" name="nickname" v-model="user.nickname" placeholder="Nickname">
            <h2>Gender</h2>
            <select class="editProfileInput editProfileInputGender" v-model="user.gender" name="gender" placeholder="Gender" value="Gender" required>
                <option selected disabled hidden>Gender</option>
                <option>Non-Binary</option>
                <option>Female</option>
                <option>Male</option>
            </select>
            <h2>Fitness Level</h2>
            <select class="editProfileInput editProfileInputGender" v-model="user.fitness" name="fitnesslevel" required>
                <option selected disabled hidden>Fitness Level</option>
                <option>{{option1}}</option>
                <option>{{option2}}</option>
                <option>{{option3}}</option>
                <option>{{option4}}</option>
                <option>{{option5}}</option>
            </select>
            <h2>Birthday</h2>
            <input v-model="user.date_of_birth" class="editProfileInput" name="birthday" type="date" required>
            <h2>Bio</h2>
            <textarea class="editProfileTextarea" name="bio" v-model="user.bio" placeholder="Write about yourself"></textarea>
            <button id="settingsProfileSubmit" v-on:click="updateProfile()" type="submit">Update Profile</button>
        </form>
    </div>
</div>
</template>

<script>

import { mapGetters, mapActions } from 'vuex'

import UserSettingsMenu from '@/components/Settings/UserSettingsMenu'
import {apiUser} from "../../api";

export default {
    components: {
        UserSettingsMenu
    },
    computed: {
        ...mapGetters(['user']),
        fitnessLevel: function() {
            const option1 = "0 - I can walk short distances";
            const option2 = "1 - I can run a short distance";
            const option3 = "2 - I could attempt a small marathon";
            const option4 = "3 - I could complete a marathon";
            const option5 = "4 - I am world class in endurance";

            let fitness = 0;
            if (this.user.fitness === 0) {
                fitness = option1;
            } else if (this.user.fitness === 1) {
                fitness = option2;
            } else if (this.user.fitness === 2) {
                fitness = option3;
            } else if (this.user.fitness === 3) {
                fitness = option4;
            } else {
                fitness = option5;
            }
            return fitness;
        }
    },
    created() {
        const option1 = "0 - I can walk short distances";
        const option2 = "1 - I can run a short distance";
        const option3 = "2 - I could attempt a small marathon";
        const option4 = "3 - I could complete a marathon";
        const option5 = "4 - I am world class in endurance";

        let fitness = 0;
        if (this.user.fitness === 0) {
            fitness = option1;
        } else if (this.user.fitness === 1) {
            fitness = option2;
        } else if (this.user.fitness === 2) {
            fitness = option3;
        } else if (this.user.fitness === 3) {
            fitness = option4;
        } else {
            fitness = option5;
        }

        this.user.fitness = fitness;
    },
    data (){
        return {
            option1: "0 - I can walk short distances",
            option2: "1 - I can run a short distance",
            option3: "2 - I could attempt a small marathon",
            option4: "3 - I could complete a marathon",
            option5: "4 - I am world class in endurance",
        }
    },
    methods: {
        ...mapActions(['logout']),
        ...mapActions(['updateUserProfile']),
        updateProfile() {
            let fitnessLevel = 0;
            if (this.user.fitness === this.option1) {
                fitnessLevel = 0;
            } else if (this.user.fitness === this.option2) {
                fitnessLevel = 1;
            } else if (this.user.fitness === this.option3) {
                fitnessLevel = 2;
            } else if (this.user.fitness === this.option4) {
                fitnessLevel = 3;
            } else {
                fitnessLevel = 4;
            }
            apiUser.editProfile(this.user.profile_id, this.user.firstname, this.user.lastname, this.user.middlename,
                this.user.nickname, this.user.primary_email, this.user.bio, this.user.date_of_birth, this.user.gender,
                fitnessLevel, this.user.additional_email, this.user.passports).then((response) => {
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
        addPassportCountries() {
            this.passportCountries.num_of_countries++;
        },
    }
}
</script>
