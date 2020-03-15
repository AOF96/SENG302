<template>
<div id="settingsWrap">
    <UserSettingsMenu />
    <div class="settingsContent">
        <h1>Edit Email Settings</h1>
        <hr>
        <h3>Primary email</h3>
        <div class="emailBlock">
            <h4>{{ user.user.email }}</h4>
        </div>
        <h3>Secondary emails:</h3>
        <div class="emailBlock emailSecondary" v-for="email in user.user.secondaryEmails" v-bind:key="email">
            <h4>{{email}}</h4>
            <button class="setPrimaryButton" v-on:click="updatePrimaryEmail(email)">Set Primary</button>
            <svg class="removeEmailButton" v-on:click="removeEmail(email)" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
                <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                <path d="M0 0h24v24H0z" fill="none" /></svg>
        </div>
        <div v-if="user.user.secondaryEmails.length < 4">
          <form @submit.prevent>
            <input id="addEmailInput" v-model="textInput" type="email" placeholder="Enter new email (Up to 4)" required>
            <button id="addEmailButton" v-if="showButton" v-on:click="addEmail()">Add</button>
          </form>
        </div>
    </div>
</div>
</template>

<script>
import { mapState, mapActions } from 'vuex'

import UserSettingsMenu from '@/components/Settings/UserSettingsMenu'
import axios from "axios";
const SERVER_URL = 'https://4967d4f4-8301-42d1-a778-e3d150633644.mock.pstmn.io';
const LIMIT_NUM_EMAIL = 4

export default {
    components: {
        UserSettingsMenu
    },
    data() {
        return {
            showButton: true,
            textInput: '',
            newEmail: '',
            limit_num_email: LIMIT_NUM_EMAIL
        }
    },
    computed: {
        ...mapState(['user']),
        isValidEmail(){
            return this.textInput != "" && (/[^\s]+@[^\s]+/.test(this.textInput))
        } 
    },
    methods: {
        ...mapActions(['updateUserEmail']),

        /*
           Adds a new email into the secondary emails lists. Prevents the user from entering empty text or from trying to
           enter an existing email.
        */
        addEmail() {
            if (this.user.user.secondaryEmails.includes(this.textInput) || this.textInput == this.user.user.email) {
                alert("Please enter an email that has not been used before");
                return
            } 
            if(!this.isValidEmail) {
                alert("Please enter an valid email address");
                return
            }

            axios.post(SERVER_URL + '/editemail', {
                    new_email: this.textInput
                })
                .then((response) => {
                    console.log(response.data.msg3);
                }, (error) => {
                    console.log(error);
                });

            if (this.user.user.secondaryEmails.length === 4) {
                alert("You already have 5 emails, delete one if you want to add more.");
                this.showButton = false;
                return;
            }
            this.user.user.secondaryEmails.push(this.textInput);
            this.updateUserEmail(this.user.user)
            var tempThis = this;
            setTimeout(function() {
                tempThis.textInput = "";
            }, 10);
        },

        /*
         Function that updates the primary email of an user by picking one from the secondary emails list and adding the
         previous primary email to the secondary list.
         */
        updatePrimaryEmail(secondaryEmail) {
            const index = this.user.user.secondaryEmails.indexOf(secondaryEmail);
            if (index > -1) {
                this.user.user.secondaryEmails.splice(index, 1);
            }
            this.user.user.secondaryEmails.push(this.user.user.email);
            this.user.user.email = secondaryEmail;
            this.updateUserEmail(this.user.user)
        },

        /* Function that removes an email from the secondary emails list */
        removeEmail(email) {
            if (this.user.user.secondaryEmails.length === 1) {
                this.user.user.secondaryEmails = []
            } else {
                const index = this.user.user.secondaryEmails.indexOf(email);
                if (index > -1) {
                    this.user.user.secondaryEmails.splice(index, 1);
                }
            }
            if (this.user.user.secondaryEmails.length > 0) {
                this.showButton = true;
            }

            this.updateUserEmail(this.user.user)
        }
    }
}
</script>
