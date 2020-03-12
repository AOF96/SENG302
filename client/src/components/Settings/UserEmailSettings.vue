<template>
<div id="settingsWrap">
    <UserSettingsMenu />
    <div class="settingsContent">
        <h1>Edit Email Settings</h1>
        <hr>
        <h3>Primary email</h3>
        <div class="emailBlock">
            <h4>{{ user.email }}</h4>
        </div>
        <h3>Secondary emails:</h3>
        <div class="emailBlock emailSecondary" v-for="email in user.secondaryEmails" v-bind:key="email">
            <h4>{{email}}</h4>
            <button class="setPrimaryButton" v-on:click="updatePrimaryEmail(email)">Set Primary</button>
            <svg class="removeEmailButton" v-on:click="removeEmail(email)" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
                <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                <path d="M0 0h24v24H0z" fill="none" /></svg>
        </div>
        <!-- <div v-if="user.secondaryEmails.length < 4"> -->
        <div v-if="1 < 4">
          <form @submit.prevent>
            <input id="addEmailInput" v-model="textInput" type="email" placeholder="Enter new email (Up to 4)" required>
            <button id="addEmailButton" v-if="showButton" v-on:click="addEmail(textInput)">Add</button>
          </form>
        </div>
    </div>
</div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import UserSettingsMenu from '@/components/Settings/UserSettingsMenu'
import {
    userInfo
} from "../../globals";
import axios from "axios";
const SERVER_URL = 'https://4967d4f4-8301-42d1-a778-e3d150633644.mock.pstmn.io';

export default {
    components: {
        UserSettingsMenu
    },
    data() {
        return {
            primary_email: userInfo.email,
            secondary_emails: userInfo.secondaryEmails,
            showButton: true,
            textInput: '',
            newEmail: '',
        }
    },
    computed: {
        ...mapState(['user'])
    },
    methods: {
        ...mapActions(['updateUserEmail']),

        /*
           Adds a new email into the secondary emails lists. Prevents the user from entering empty text or from trying to
           enter an existing email.
        */
        addEmail(textInput) {
            if (this.secondary_emails.includes(textInput) || textInput == this.primary_email) {
                alert("Please enter an email that has not been used before");
            } else if(textInput != "" && (/[^\s]+@[^\s]+/.test(textInput))) {
                axios.post(SERVER_URL + '/editemail', {
                        new_email: textInput
                    })
                    .then((response) => {
                        console.log(response.data.msg3);
                    }, (error) => {
                        console.log(error);
                    });

                if (this.secondary_emails.length === 4) {
                    alert("You already have 5 emails, delete one if you want to add more.");
                    this.showButton = false;
                    return;
                }
                this.secondary_emails.push(this.textInput);
                this.updateUserEmail()
                var tempThis = this;
                setTimeout(function() {
                  tempThis.textInput = "";
                }, 10);

            }
        },

        /*
         Function that updates the primary email of an user by picking one from the secondary emails list and adding the
         previous primary email to the secondary list.
         */
        updatePrimaryEmail(secondaryEmail) {
            const index = this.secondary_emails.indexOf(secondaryEmail);
            if (index > -1) {
                this.secondary_emails.splice(index, 1);
            }
            this.secondary_emails.push(this.primary_email);
            this.primary_email = secondaryEmail;
            this.updateUserEmail()
        },

        /* Function that removes an email from the secondary emails list */
        removeEmail(email) {
            if (this.secondary_emails.length === 1) {
                this.secondary_emails = []
            } else {
                const index = this.secondary_emails.indexOf(email);
                if (index > -1) {
                    this.secondary_emails.splice(index, 1);
                }
            }
            if (this.secondary_emails.length > 0) {
                this.showButton = true;
            }

            this.updateUserEmail()
        }
    }
}
</script>
